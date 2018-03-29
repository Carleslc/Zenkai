package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.common.Provider
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.Message

interface MessagesRepository {
    
    suspend fun greetings(): List<BotMessage>

    suspend fun getHistory(): List<Message>
    
    suspend fun add(message: Message)

    companion object : Provider<MessagesRepository>() {
        override fun create(): MessagesRepository = RepositoriesProvider.getMessagesRepository()
    }
    
}