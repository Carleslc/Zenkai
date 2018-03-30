package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.common.Provider
import ai.zenkai.zenkai.model.BotResult
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.serialization.BotError

interface MessagesRepository {
    
    suspend fun greetings(): BotResult

    suspend fun getHistory(): List<Message>
    
    suspend fun add(message: Message)
    
    companion object : Provider<MessagesRepository>() {
        override fun create(): MessagesRepository = RepositoriesProvider.getMessagesRepository()
    }
    
}