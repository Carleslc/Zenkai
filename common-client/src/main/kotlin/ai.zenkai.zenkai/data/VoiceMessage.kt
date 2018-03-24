package ai.zenkai.zenkai.data

import ai.zenkai.zenkai.i18n.i18n.removeEmojis
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener

data class VoiceMessage(private val speech: String, var speakingListener: SpeakingListener? = null)
    : Message(speech.removeEmojis()) {
    
    fun say() = ServicesProvider.getSpeechService().say(this)
    
    companion object Factory {
        val EMPTY get() = VoiceMessage("")
    }
    
}