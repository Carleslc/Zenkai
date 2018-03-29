package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.common.Service
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.model.VoiceMessage
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
            logger.info { "[${this::class.simpleName}] Speaker is disabled for '${message.message}'" }
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
            logger.info { "[${this::class.simpleName}] Listening" }
            onListen(callback)
        } else {
            logger.info { "[${this::class.simpleName}] Cannot listen, microphone is disabled" }
            callback.onCancel()
        }
    }
    
    protected abstract fun onListen(callback: ListeningCallback)
    
    interface ListeningCallback {
        fun onRequest(request: Message)
        fun onResults(responses: List<BotMessage>)
        fun onError(error: ListeningException)
        fun onCancel()
    }
    
    interface SpeakingListener {
        
        fun onSpeakStarted() { }
        fun onSpeakCompleted() { }
        fun onSpeakCancelled() { }
        
        companion object Factory : WithLogging by KLoggerHolder() {
            
            fun merge(old: SpeakingListener, new: SpeakingListener): SpeakingListener {
                logger.info { "[${this::class.simpleName}] Merge" }
                return object : SpeakingListener {
                    override fun onSpeakStarted() {
                        old.onSpeakStarted()
                        new.onSpeakStarted()
                    }
                    override fun onSpeakCompleted() {
                        old.onSpeakCompleted()
                        new.onSpeakCompleted()
                    }
                    override fun onSpeakCancelled() {
                        old.onSpeakCancelled()
                        new.onSpeakCancelled()
                    }
                }
            }
            
            fun performOnce(onStart: () -> Unit, onFinish: () -> Unit): SpeakingListener {
                return object : SpeakingListener {
                    private var spoken = false
                    override fun onSpeakStarted() {
                        onStart()
                    }
                    override fun onSpeakCancelled() {
                        logger.info { "[${this::class.simpleName}] Cancelled" }
                        finish()
                    }
                    override fun onSpeakCompleted() {
                        logger.info { "[${this::class.simpleName}] Completed" }
                        finish()
                    }
                    private fun finish() {
                        if (!spoken) {
                            onFinish()
                            spoken = true
                        }
                    }
                }
            }
            
            fun onCompleted(onCompleted: ()-> Unit): SpeakingListener {
                return object : SpeakingListener {
                    override fun onSpeakCompleted() {
                        onCompleted()
                    }
                }
            }
            
            fun onFinish(completedOrCancelled: () -> Unit): SpeakingListener {
                return object : SpeakingListener {
                    override fun onSpeakCompleted() {
                        completedOrCancelled()
                    }
                    override fun onSpeakCancelled() {
                        completedOrCancelled()
                    }
                }
            }
            
        }
    }
    
}