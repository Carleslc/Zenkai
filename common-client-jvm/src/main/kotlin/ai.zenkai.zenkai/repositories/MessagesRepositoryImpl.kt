package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.MessagesData

class MessagesRepositoryImpl: MessagesRepository {
    
    private val examples = mutableListOf<Message>(
        BotMessage("Hi! I'm Zenkai, your personal assistant."),
        BotMessage("What do you want to do?")
    )
    
    override suspend fun query(message: Message): BotMessage {
        examples.add(message)
        val answer = BotMessage(message.message)
        examples.add(answer)
        return answer
    }
    
    override suspend fun getHistory(): MessagesData {
        // TODO get from Firebase Database
        return MessagesData(examples)
    }
    
}