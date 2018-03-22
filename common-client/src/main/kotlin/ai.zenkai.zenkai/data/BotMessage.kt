package ai.zenkai.zenkai.data

import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n

data class BotMessage(val text: TextMessage, val speech: VoiceMessage) : Message(text.message) {
    
    constructor(text: String) : this(TextMessage(text), VoiceMessage(text))
    
    constructor(s: S) : this(i18n[s])
    
    fun say() = speech.say()
    
}