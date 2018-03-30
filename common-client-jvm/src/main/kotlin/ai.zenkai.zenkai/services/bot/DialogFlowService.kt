package ai.zenkai.zenkai.services.bot

import ai.api.AIConfiguration
import ai.api.AIConfiguration.SupportedLanguages
import ai.api.AIDataService
import ai.api.model.AIEvent
import ai.api.model.AIOriginalRequest
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import ai.api.model.ResponseMessage.ResponseSpeech
import ai.zenkai.zenkai.DateTime
import ai.zenkai.zenkai.common.doAsync
import ai.zenkai.zenkai.gson
import ai.zenkai.zenkai.i18n.DEFAULT_LANGUAGE
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.S.EMPTY_ANSWER
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.i18n.SupportedLanguage.ENGLISH
import ai.zenkai.zenkai.i18n.SupportedLanguage.SPANISH
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.BotResult
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.model.TextMessage
import ai.zenkai.zenkai.model.VoiceMessage
import ai.zenkai.zenkai.repositories.SettingsRepository
import ai.zenkai.zenkai.serialization.BotError
import ai.zenkai.zenkai.serialization.BotRequestData
import ai.zenkai.zenkai.serialization.BotResponse
import klogging.KLoggerHolder
import klogging.WithLogging
import me.carleslc.kotlin.extensions.collections.isBlank
import me.carleslc.kotlin.extensions.collections.mapToMutableList
import kotlin.properties.Delegates.notNull

actual object DialogFlowService : BotService, WithLogging by KLoggerHolder() {
    
    private const val REPEAT_TIMEOUT_REQUEST_LIMIT = 1
    private const val PARTIAL_CONTENT = 206
    private const val OK = 200
    
    const val NO_NETWORK = "Can't connect to the api.ai service."
    
    private var provider: AIConfigurationProvider by notNull()
    
    private var dataService: AIDataService by notNull()
    
    var config: AIConfiguration by notNull()
        private set
    
    override var language = DEFAULT_LANGUAGE
        set(value) {
            config = provider.get(DIALOGFLOW_CLIENT_ACCESS_TOKEN, getConfigurationLanguage(value))
            dataService = AIDataService(config)
            field = value
            logger.debug { "[${this::class.simpleName}] Language set to $value" }
        }
    
    fun setConfigurationProvider(provider: AIConfigurationProvider) {
        this.provider = provider
    }
    
    private suspend fun repeatOnTimeout(request: () -> AIResponse): Pair<AIResponse, BotResult> {
        var timeoutCounter = 0
        
        fun BotResult.isTimeout() = timeout && ++timeoutCounter <= REPEAT_TIMEOUT_REQUEST_LIMIT
        
        fun getBotMessages(): Pair<AIResponse, BotResult> {
            logger.info { "Getting Bot Messages" }
            try {
                val response = request()
                with(getBotResult(response)) {
                    if (isTimeout()) return getBotMessages()
                    return response to this
                }
            } catch(e: Exception) {
                val message = e.message ?: NO_NETWORK
                logger.error { "[${DialogFlowService::class.simpleName}] Error processing Dialogflow request: ${e.message}" }
                val botError = BotError(checkNetworkErrorMessage(message), 418, e.stackTrace.joinToString("\n"), null)
                return AIResponse() to BotResult.error(botError)
            }
        }
        
        return doAsync { getBotMessages() }.await()
    }
    
    override suspend fun sendEvent(name: String, data: Map<String, String>?): BotResult = repeatOnTimeout {
        dataService.request(AIRequest().apply {
            setEvent(AIEvent(name).apply {
                setData(data)
            })
            fillParameters(this)
        })
    }.second
    
    override suspend fun getGreetings() = sendEvent("Greetings")
    
    suspend fun ask(originalQuery: String, request: AIRequest, setQuery: AIRequest.() -> Unit = {}) = repeatOnTimeout {
        logger.debug { "[${this::class.simpleName}] Ask: $originalQuery" }
        dataService.request(request.apply {
            setQuery()
            fillParameters(this)
        })
    }
    
    override suspend fun ask(message: Message): BotResult = ask(message.message, AIRequest()) {
        setQuery(message.message) // Unique query for non voice recognition
    }.second
    
    fun checkNetworkErrorMessage(message: String): String {
        return if (NO_NETWORK in message) i18n[S.NO_NETWORK] else message
    }
    
    private fun fillParameters(request: AIRequest) {
        request.apply {
            timezone = DateTime.getTimeZone()
            originalRequest = AIOriginalRequest().apply {
                source = "zenkai"
                data = mapOf(source to BotRequestData(
                    timezone,
                    SettingsRepository.getTokens()
                ))
            }
        }
    }
    
    private fun getBotResult(response: AIResponse): BotResult = with (response) {
        logger.debug { "[${this::class.simpleName}] Response: ${gson.toJson(this)}" }
        val status = status
        if (status.code == PARTIAL_CONTENT) {
            logger.warn { "[${this::class.simpleName}] ${status.errorDetails}" }
            if ("timeout" in status.errorDetails) {
                return BotResult.timeout()
            }
        } else if (status.code == OK && result.metadata.isWebhookUsed) {
            val result = getZenkaiResult()
            if (result.messages.isNotEmpty()) {
                return result
            } else if (result.isError()) {
                val messages = mutableListOf(BotMessage(result.error!!.message))
                return BotResult(messages, false, result.error)
            }
        }
        return BotResult.success(dialogFlowMessages())
    }
    
    private fun AIResponse.dialogFlowMessages(): List<BotMessage> {
        val messages = result.fulfillment.messages.filter { it is ResponseSpeech }.flatMap {
            val speech = (it as ResponseSpeech).speech
            if (speech.isBlank()) {
                listOf()
            } else {
                speech.filter { it.isNotBlank() }.map { BotMessage(it) }
            }
        }
        return if (messages.isEmpty()) {
            listOf(BotMessage(TextMessage(EMPTY_ANSWER),
                VoiceMessage()))
        } else messages
    }
    
    private fun AIResponse.getZenkaiResult(): BotResult {
        val data = result.fulfillment.data?.get("zenkai")
        if (data != null) {
            val response = gson.fromJson(data.asJsonObject, BotResponse::class.java)
            logger.debug { "Response: $response" }
            val messages = response.messages.orEmpty().mapToMutableList {
                BotMessage(text = it.displayText.orEmpty(), speech = it.textToSpeech.orEmpty())
            }
            return BotResult(messages, false, response.error, response.tokens)
        }
        return BotResult.empty()
    }
    
    private fun getConfigurationLanguage(language: SupportedLanguage) = when(language) {
        ENGLISH -> SupportedLanguages.English
        SPANISH -> SupportedLanguages.Spanish
    }
    
}