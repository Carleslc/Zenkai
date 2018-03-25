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
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.TextMessage
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.gson
import ai.zenkai.zenkai.i18n.DEFAULT_LANGUAGE
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.i18n.SupportedLanguage.ENGLISH
import ai.zenkai.zenkai.i18n.SupportedLanguage.SPANISH
import klogging.KLoggerHolder
import klogging.WithLogging
import me.carleslc.kotlin.extensions.collections.L
import me.carleslc.kotlin.extensions.map.M
import kotlin.properties.Delegates.notNull

actual object DialogFlowService : BotService, WithLogging by KLoggerHolder() {
    
    private const val PARTIAL_CONTENT = 206
    
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
    
    private suspend fun sendEvent(name: String, data: Map<String, String>? = null): List<BotMessage> {
        return doAsync {
            val response = dataService.request(AIRequest().apply {
                setEvent(AIEvent(name).apply {
                    setData(data)
                })
                fillParameters(this)
            })
            getBotMessages(response)
        }.await()
    }
    
    override suspend fun getGreetings() = sendEvent("Greetings")
    
    override suspend fun ask(message: Message): List<BotMessage> {
        return doAsync {
            logger.debug { "[${this::class.simpleName}] Ask: ${message.message}" }
            val response = dataService.request(AIRequest().apply {
                setQuery(message.message)
                fillParameters(this)
            })
            getBotMessages(response)
        }.await()
    }
    
    fun fillParameters(request: AIRequest) {
        request.apply {
            timezone = DateTime.getTimeZone()
            originalRequest = AIOriginalRequest().apply {
                source = "Zenkai App"
                data = M["inputs" to L[M["arguments" to L[
                    Argument("timezone", timezone),
                    Argument("trello-token", "e3c03af3e5af0d62991cc722780d1ff81c442424d343e019ff379528aea6af5e")
                ]]]]
            }
        }
    }
    
    fun getBotMessages(response: AIResponse): List<BotMessage> = with (response) {
        logger.debug { "[${this::class.simpleName}] Response: ${gson.toJson(this)}" }
        val status = status
        return if (status.code == PARTIAL_CONTENT) {
            if ("timeout" in status.errorDetails) {
                logger.debug { "[${this::class.simpleName}] ${status.errorDetails}" }
                listOf(BotMessage(S.TIMEOUT))
            } else {
                logger.debug { "[${this::class.simpleName}] ${status.errorDetails}" }
                listOf(BotMessage(TextMessage(S.EMPTY_ANSWER), VoiceMessage()))
            }
        } else if (result.metadata.isWebhookUsed) {
            // Actions on Google response format
            getGoogleSimpleResponses()
        } else {
            // Dialogflow messages format
            val messages = result.fulfillment.messages.filter { it is ResponseSpeech }.flatMap {
                val speech = (it as ResponseSpeech).speech
                if (speech.isEmpty()) {
                    listOf(BotMessage())
                } else {
                    speech.map { BotMessage(it) }
                }
            }
            if (messages.isEmpty()) {
                listOf(BotMessage(result.fulfillment.speech))
            } else messages
        }
    }
    
    private fun AIResponse.getGoogleSimpleResponses(): List<BotMessage> {
        val data = result.fulfillment.data?.get("google")
        if (data != null) {
            val items = data.asJsonObject.get("richResponse")?.asJsonObject?.get("items")?.asJsonArray
            if (items != null) {
                return items.map {
                    val simpleResponse = it.asJsonObject.get("simpleResponse")?.asJsonObject
                    val text = simpleResponse?.get("displayText")?.asString ?: ""
                    val speech = simpleResponse?.get("textToSpeech")?.asString ?: ""
                    BotMessage(text, speech)
                }
            }
        }
        return emptyList()
    }
    
    private fun getConfigurationLanguage(language: SupportedLanguage) = when(language) {
        ENGLISH -> SupportedLanguages.English
        SPANISH -> SupportedLanguages.Spanish
    }
    
}