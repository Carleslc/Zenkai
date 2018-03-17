package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.data.VoiceMessage

interface SpeechService: Service {
    
    fun say(message: VoiceMessage)
    
}
