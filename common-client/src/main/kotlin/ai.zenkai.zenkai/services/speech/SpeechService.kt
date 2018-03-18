package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.data.VoiceMessage

abstract class SpeechService: Service {
    
    var enabled = true
    
    fun say(message: VoiceMessage) {
        if (enabled) speak(message)
    }
    
    protected abstract fun speak(message: VoiceMessage)
    
}
