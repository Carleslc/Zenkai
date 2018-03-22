package ai.zenkai.zenkai.services

import ai.zenkai.zenkai.services.bot.BotService
import ai.zenkai.zenkai.services.bot.DialogFlowService
import ai.zenkai.zenkai.services.speech.DefaultSpeechService
import ai.zenkai.zenkai.services.speech.SpeechService

object ServicesProvider {
    
    private var speechService: SpeechService = DefaultSpeechService.create()
    
    fun getSpeechService(): SpeechService = speechService
    
    fun setSpeechService(speechService: SpeechService) {
        this.speechService = speechService
    }
    
    fun getBotService(): BotService = DialogFlowService
    
}