package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.services.ServicesProvider
import klogging.KLoggerHolder
import klogging.WithLogging

abstract class SpeechService: Service, WithLogging by KLoggerHolder() {
    
    open var microphoneEnabled = true
    open var speakerEnabled = true
    
    var language: SupportedLanguage
        get() = ServicesProvider.getBotService().language
        set(value) { ServicesProvider.getBotService().language = value }
    
    fun say(message: VoiceMessage) {
        if (speakerEnabled && !message.isEmpty()) {
            onSpeak(message)
        } else {
            logger.debug { "[${this::class.simpleName}] Speaker is disabled for '${message.message}'" }
            message.speakingListener?.onSpeakStarted()
            message.speakingListener?.onSpeakCancelled()
        }
    }
    
    fun say(message: String) {
        say(VoiceMessage(message))
    }
    
    protected abstract fun onSpeak(message: VoiceMessage)
    
    fun listen(callback: ListeningCallback) {
        if (microphoneEnabled) {
            logger.debug { "[${this::class.simpleName}] Listening" }
            onListen(callback)
        } else {
            logger.debug { "[${this::class.simpleName}] Cannot listen, microphone is disabled" }
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
        fun onSpeakCancelled() { }
        
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
                    override fun onSpeakCancelled() {
                        currentListener?.onSpeakCancelled()
                        new.onSpeakCancelled()
                    }
                }
            }
        }
    }
    
}
