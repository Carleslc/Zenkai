package ai.zenkai.zenkai.services.bot

import ai.api.AIConfiguration
import ai.api.AIConfiguration.SupportedLanguages
import ai.api.AIDataService
import ai.api.model.AIOriginalRequest
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import ai.api.model.Fulfillment
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
    
    var config: AIConfiguration by notNull()
        private set
    
    var dataService: AIDataService by notNull()
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
    
    override suspend fun ask(message: Message): BotMessage {
        return doAsync {
            logger.debug { "[${this::class.simpleName}] Ask: ${message.message}" }
            val response = dataService.request(AIRequest().apply {
                setQuery(message.message)
                timezone = DateTime.getTimeZone()
                originalRequest = AIOriginalRequest().apply {
                    source = "Zenkai App"
                    data = M["inputs" to L[M["arguments" to L[
                        Argument("timezone", DateTime.getTimeZone()),
                        Argument("trello-token", "e3c03af3e5af0d62991cc722780d1ff81c442424d343e019ff379528aea6af5e")
                    ]]]]
                }
            })
            getBotMessage(response)
        }.await()
    }
    
    fun getBotMessage(response: AIResponse): BotMessage {
        logger.debug { "[${this::class.simpleName}] Response: ${gson.toJson(response)}" }
        val status = response.status
        return if (status.code == PARTIAL_CONTENT) {
            if ("timeout" in status.errorDetails) {
                logger.debug { "[${this::class.simpleName}] ${status.errorDetails}" }
                BotMessage(S.TIMEOUT)
            } else {
                logger.debug { "[${this::class.simpleName}] ${status.errorDetails}" }
                BotMessage(TextMessage(S.EMPTY_ANSWER), VoiceMessage.EMPTY)
            }
        } else {
            with(response.result.fulfillment) {
                BotMessage(TextMessage(text), VoiceMessage(speech))
            }
        }
    }
    
    private fun getConfigurationLanguage(language: SupportedLanguage) = when(language) {
        ENGLISH -> SupportedLanguages.English
        SPANISH -> SupportedLanguages.Spanish
    }
    
}

val Fulfillment.text: String
    get() = displayText ?: speech