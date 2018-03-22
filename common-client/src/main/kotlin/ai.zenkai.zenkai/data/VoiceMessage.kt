package ai.zenkai.zenkai.data

import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener
import ai.zenkai.zenkai.services.ServicesProvider

data class VoiceMessage(val speech: String, var speakingListener: SpeakingListener? = null) : Message(speech) {
    
    fun say() = ServicesProvider.getSpeechService().say(this)
    
}