package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.MessagesData
import ai.zenkai.zenkai.services.bot.DialogFlowService
import klogging.KLoggerHolder
import klogging.WithLogging

class MessagesRepositoryImpl: MessagesRepository, WithLogging by KLoggerHolder() {
    
    private var needGreetings = true
    private val session = MessagesData()
    
    override suspend fun greetings(): List<BotMessage> {
        val greetings = if (needGreetings) {
            logger.debug { "[${this::class.simpleName}] Greetings!" }
            DialogFlowService.getGreetings()
        } else emptyList()
        needGreetings = false
        return greetings
    }
    
    override suspend fun add(message: Message) {
        session.add(message)
    }
    
    // TODO get all previous messages from Firebase Database
    override suspend fun getHistory() = session
}