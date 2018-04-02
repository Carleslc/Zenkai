package ai.zenkai.zenkai.services.bot

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.model.BotResult
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.serialization.BotError

const val GREETINGS_EVENT = "Greetings"

interface BotService : Service {
    
    var language: SupportedLanguage
    
    suspend fun ask(message: Message): BotResult
    
    suspend fun getGreetings(): BotResult
    
    suspend fun sendEvent(name: String, data: Map<String, String>? = null): BotResult
    
}