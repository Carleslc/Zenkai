package ai.zenkai.zenkai.data

data class BotMessage(var text: TextMessage, var speech: VoiceMessage) : Message(text.message) {
    
    constructor(text: String) : this(TextMessage(text), VoiceMessage(text))
    
}