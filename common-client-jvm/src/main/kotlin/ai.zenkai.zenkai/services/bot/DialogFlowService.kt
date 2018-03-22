package ai.zenkai.zenkai.services.bot

import ai.api.AIConfiguration
import ai.api.AIConfiguration.SupportedLanguages
import ai.api.AIDataService
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import ai.api.model.Fulfillment
import ai.zenkai.zenkai.common.doAsync
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.TextMessage
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.i18n.DEFAULT_LANGUAGE
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.i18n.SupportedLanguage.ENGLISH
import ai.zenkai.zenkai.i18n.SupportedLanguage.SPANISH
import kotlin.properties.Delegates.notNull

actual object DialogFlowService : BotService {
    
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
        }
    
    fun setConfigurationProvider(provider: AIConfigurationProvider) {
        this.provider = provider
    }
    
    override suspend fun ask(message: Message): BotMessage {
        return doAsync {
            val response = dataService.request(AIRequest().apply {
                setQuery(message.message)
            })
            getBotMessage(response)
        }.await()
    }
    
    fun getBotMessage(response: AIResponse) = with(response.result.fulfillment) {
        BotMessage(TextMessage(text), VoiceMessage(speech))
    }
    
    private fun getConfigurationLanguage(language: SupportedLanguage) = when(language) {
        ENGLISH -> SupportedLanguages.English
        SPANISH -> SupportedLanguages.Spanish
    }
    
}

val Fulfillment.text: String
    get() = displayText ?: speech