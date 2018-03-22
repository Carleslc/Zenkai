package ai.zenkai.zenkai.services.bot

import ai.api.AIConfiguration
import ai.api.AIConfiguration.SupportedLanguages

interface AIConfigurationProvider {

    fun get(clientAccessToken: String, language: SupportedLanguages): AIConfiguration

}