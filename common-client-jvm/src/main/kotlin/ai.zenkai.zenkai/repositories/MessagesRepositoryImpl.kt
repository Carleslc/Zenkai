package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.BotResult
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.services.ServicesProvider
import klogging.KLoggerHolder
import klogging.WithLogging

class MessagesRepositoryImpl: MessagesRepository, WithLogging by KLoggerHolder() {
    
    private var needGreetings = true
    private val session = mutableListOf<Message>()
    
    override suspend fun greetings(): BotResult {
        val greetings = if (needGreetings) {
            logger.info { "[${this::class.simpleName}] Greetings!" }
            val greetings = ServicesProvider.getBotService().getGreetings()
            if (greetings.isError()) {
                val fallbackMessages = i18n[S.NO_GREETINGS].split('\n')
                fallbackMessages.forEach { greetings.messages.add(BotMessage(it)) }
            }
            greetings
        } else BotResult.empty()
        needGreetings = false
        return greetings
    }
    
    override suspend fun add(message: Message) {
        session.add(message)
    }
    
    override suspend fun getHistory() = session
}