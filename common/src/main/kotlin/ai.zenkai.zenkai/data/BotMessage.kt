package ai.zenkai.zenkai.data

data class BotMessage(var text: TextMessage, var speech: VoiceMessage) : Message(text.message) {
    
    companion object Factory {
        fun create(text: String) = BotMessage(TextMessage(text), VoiceMessage(text))
    }
    
}