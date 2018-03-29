package ai.zenkai.zenkai.model

import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n

data class BotMessage(val text: TextMessage, val speech: VoiceMessage, val share: () -> String = { text.message })
    : Message(text.message) {
    
    constructor(text: String, speech: String) : this(TextMessage(text), VoiceMessage(speech))
    
    constructor(text: String) : this(TextMessage(text), VoiceMessage(text))
    
    constructor(s: S) : this(i18n[s])
    
    constructor() : this("")
    
    override fun isEmpty() = text.isEmpty() && speech.isEmpty()
    
    fun say() = speech.say()
    
}