package ai.zenkai.zenkai.services.bot

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.i18n.SupportedLanguage

interface BotService : Service {
    
    var language: SupportedLanguage
    
    suspend fun ask(message: Message): List<BotMessage>
    
    suspend fun getGreetings(): List<BotMessage>
    
    suspend fun sendEvent(name: String, data: Map<String, String>? = null): List<BotMessage>
    
}