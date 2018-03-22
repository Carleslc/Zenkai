package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.common.Provider
import ai.zenkai.zenkai.data.VoiceMessage
import klogging.KLoggerHolder
import klogging.WithLogging

open class DefaultSpeechService: SpeechService(), WithLogging by KLoggerHolder() {
    
    override fun onListen(callback: ListeningCallback) {
        logger.info(DefaultSpeechService::class.simpleName + " cannot listen.")
        callback.onCancel()
    }
    
    override fun onSpeak(message: VoiceMessage) {
        message.speakingListener?.onSpeakStarted()
        logger.info(DefaultSpeechService::class.simpleName + " say: " + message.message)
        message.speakingListener?.onSpeakCompleted()
    }
    
    companion object Factory : Provider<SpeechService>() {
        override fun create() = DefaultSpeechService()
    }
    
}