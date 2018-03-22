package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.services.ServicesProvider

abstract class SpeechService: Service {
    
    var microphoneEnabled = true
    var speakerEnabled = true
    
    var language: SupportedLanguage
        get() = ServicesProvider.getBotService().language
        set(value) { ServicesProvider.getBotService().language = value }
    
    fun say(message: VoiceMessage) {
        if (speakerEnabled) {
            onSpeak(message)
        } else {
            message.speakingListener?.onSpeakStarted()
            message.speakingListener?.onSpeakCompleted()
        }
    }
    
    fun say(message: String) {
        say(VoiceMessage(message))
    }
    
    protected abstract fun onSpeak(message: VoiceMessage)
    
    fun listen(callback: ListeningCallback) {
        if (microphoneEnabled) {
            onListen(callback)
        } else {
            callback.onCancel()
        }
    }
    
    protected abstract fun onListen(callback: ListeningCallback)
    
    interface ListeningCallback {
        fun onResult(request: Message, response: BotMessage)
        fun onError(error: ListeningException)
        fun onCancel()
    }
    
    interface SpeakingListener {
        fun onSpeakStarted() { }
        fun onSpeakCompleted() { }
        
        companion object Merge {
            fun merge(message: VoiceMessage, new: SpeakingListener) {
                val currentListener = message.speakingListener
                message.speakingListener = object : SpeakingListener {
                    override fun onSpeakStarted() {
                        currentListener?.onSpeakStarted()
                        new.onSpeakStarted()
                    }
                    override fun onSpeakCompleted() {
                        currentListener?.onSpeakCompleted()
                        new.onSpeakCompleted()
                    }
                }
            }
        }
    }
    
}
