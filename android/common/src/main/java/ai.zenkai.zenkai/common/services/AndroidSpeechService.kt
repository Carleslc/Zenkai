package ai.zenkai.zenkai.common.services

import ai.zenkai.zenkai.common.launchUI
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.services.speech.SpeechService
import android.content.Context
import org.jetbrains.anko.*

class AndroidSpeechService(private val context: Context) : SpeechService {
    
    override fun say(message: VoiceMessage) {
        launchUI { context.toast(message.message) }
    }
    
}