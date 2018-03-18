package ai.zenkai.zenkai.common.services

import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.services.speech.SpeechService
import android.content.Context
import org.jetbrains.anko.*
import kotlin.properties.Delegates.notNull

object AndroidSpeechService : SpeechService() {
    
    private var context: Context by notNull()
    
    fun attach(context: Context): AndroidSpeechService {
        this.context = context
        return this
    }
    
    override fun speak(message: VoiceMessage) {
        context.toast(message.message)
    }
    
}