package ai.zenkai.zenkai.services

import ai.zenkai.zenkai.common.Provider
import ai.zenkai.zenkai.services.speech.DefaultSpeechService
import ai.zenkai.zenkai.services.speech.SpeechService

object ServicesProvider {
    
    private var speechProvider: Provider<SpeechService> = DefaultSpeechService.Factory
    
    fun getSpeechService(): SpeechService = speechProvider.get()
    
    fun setSpeechServiceProvider(speechProvider: Provider<SpeechService>) {
        this.speechProvider = speechProvider
    }
    
    fun setSpeechService(speechService: SpeechService) {
        setSpeechServiceProvider(Provider.create(speechService))
    }
    
}
