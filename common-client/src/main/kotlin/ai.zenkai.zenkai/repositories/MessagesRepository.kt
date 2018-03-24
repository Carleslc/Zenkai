package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.common.Provider
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.MessagesData

interface MessagesRepository {
    
    suspend fun greetings(): List<BotMessage>

    suspend fun getHistory(): MessagesData
    
    suspend fun add(message: Message)

    companion object : Provider<MessagesRepository>() {
        override fun create(): MessagesRepository = RepositoriesProvider.getMessagesRepository()
    }
    
}