package ai.zenkai.zenkai.model

import ai.zenkai.zenkai.i18n.i18n.removeEmojis
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Factory.merge

data class VoiceMessage(private val speech: String) : Message(speech.removeEmojis()) {
    
    var speakingListener: SpeakingListener? = null
        set(value) {
            val current = speakingListener
            field = if (current != null && value != null) merge(current, value) else value
        }
    
    constructor() : this("")
    
    fun say() = ServicesProvider.getSpeechService().say(this)
    
}