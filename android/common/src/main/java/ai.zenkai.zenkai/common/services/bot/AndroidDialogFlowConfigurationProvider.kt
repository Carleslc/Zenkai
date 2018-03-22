package ai.zenkai.zenkai.common.services.bot

import ai.api.AIConfiguration
import ai.api.AIConfiguration.SupportedLanguages
import ai.zenkai.zenkai.services.bot.AIConfigurationProvider
import ai.api.android.AIConfiguration as AndroidAIConfiguration

typealias AndroidAIConfiguration = AndroidAIConfiguration

object AndroidDialogFlowConfigurationProvider: AIConfigurationProvider {
    
    override fun get(clientAccessToken: String, language: SupportedLanguages): AIConfiguration {
        return AndroidAIConfiguration(clientAccessToken, language, AndroidAIConfiguration.RecognitionEngine.System)
    }
    
}