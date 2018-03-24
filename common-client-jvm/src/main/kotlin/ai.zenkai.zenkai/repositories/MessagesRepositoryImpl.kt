package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.MessagesData
import ai.zenkai.zenkai.i18n.S.GREETING
import ai.zenkai.zenkai.i18n.S.HELLO
import klogging.KLoggerHolder
import klogging.WithLogging

class MessagesRepositoryImpl: MessagesRepository, WithLogging by KLoggerHolder() {
    
    private var greetingsList = listOf(BotMessage(HELLO), BotMessage(GREETING))
    private val session = MessagesData()
    
    override suspend fun greetings(): List<BotMessage> {
        val greetings = greetingsList
        logger.debug { "[${this::class.simpleName}] Greetings!" }
        greetingsList = emptyList()
        return greetings
    }
    
    override suspend fun add(message: Message) {
        session.add(message)
    }
    
    // TODO get all previous messages from Firebase Database
    override suspend fun getHistory() = session
}