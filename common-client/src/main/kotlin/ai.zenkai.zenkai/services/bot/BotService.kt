package ai.zenkai.zenkai.services.bot

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.model.BotResult
import ai.zenkai.zenkai.model.Message

const val GREETINGS_EVENT = "Greetings"

const val REPEAT_TIMEOUT_REQUEST_LIMIT = 1

interface BotService : Service {
    
    var language: SupportedLanguage
    
    suspend fun ask(message: Message): BotResult
    
    suspend fun getGreetings(): BotResult
    
    suspend fun sendEvent(name: String, data: Map<String, String>? = null,
        repetitionsOnTimeout: Int = REPEAT_TIMEOUT_REQUEST_LIMIT,
        overrideTimeoutMessages: Boolean = true): BotResult
    
}