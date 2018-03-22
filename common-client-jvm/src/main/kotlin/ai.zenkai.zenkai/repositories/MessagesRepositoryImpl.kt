package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.MessagesData
import ai.zenkai.zenkai.i18n.S.GREETING
import ai.zenkai.zenkai.i18n.S.HELLO
import ai.zenkai.zenkai.services.ServicesProvider

class MessagesRepositoryImpl: MessagesRepository {
    
    override fun getGreetings() = listOf(BotMessage(HELLO),
        BotMessage(GREETING))
    
    override suspend fun ask(message: Message): BotMessage {
        //database.add(message)
        val answer = ServicesProvider.getBotService().ask(message)
        //database.add(answer)
        return answer
    }
    
    // TODO get all previous messages from Firebase Database
    override suspend fun getHistory() = MessagesData()
    
}