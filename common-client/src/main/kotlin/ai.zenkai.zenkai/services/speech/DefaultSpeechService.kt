package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.common.Provider
import ai.zenkai.zenkai.data.VoiceMessage
import klogging.WithLogging
import klogging.KLoggerHolder

open class DefaultSpeechService: SpeechService(), WithLogging by KLoggerHolder() {
    
    override fun speak(message: VoiceMessage) {
        logger.info(DefaultSpeechService::class.simpleName + " say: " + message.message)
    }
    
    companion object Factory : Provider<SpeechService>() {
        override fun create() = DefaultSpeechService()
    }
    
}