package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.model.Token
import ai.zenkai.zenkai.services.bot.GREETINGS_EVENT
import klogging.KLoggerHolder
import klogging.WithLogging

object SettingsRepository : WithLogging by KLoggerHolder() {
    
    const val TOKEN_SUFFIX = "-token"
    const val EVENT_SUFFIX = "-event"
    const val REGEX_SUFFIX = "-regex"
    
    private lateinit var tokens: MutableMap<String, Token>
    
    private lateinit var deviceSettings: DeviceSettings
    
    fun isFirstTime() = deviceSettings[Ids.FIRST_TIME.toString(), true]
    
    fun setFirstTime() {
        deviceSettings[Ids.FIRST_TIME.toString()] = false
    }
    
    fun setToken(update: Token) = with(update) {
        logger.info { "Update token $type to $token" }
        deviceSettings[type + TOKEN_SUFFIX] = token!!
        deviceSettings[type + EVENT_SUFFIX] = loginEvent
        deviceSettings[type + REGEX_SUFFIX] = regex
        tokens[type] = update
    }
    
    fun clearToken(type: String) {
        logger.info { "Clear token $type" }
        deviceSettings.clear(type + TOKEN_SUFFIX)
        deviceSettings.clear(type + EVENT_SUFFIX)
        deviceSettings.clear(type + REGEX_SUFFIX)
        tokens.remove(type)
    }
    
    fun getTokens() = tokens.values
    
    fun setDeviceSettings(deviceSettings: DeviceSettings) {
        this.deviceSettings = deviceSettings
        readCurrentTokens()
    }
    
    fun isNetworkAvailable() = deviceSettings.isNetworkAvailable()
    
    private fun readCurrentTokens() {
        tokens = this.deviceSettings.getAll().filter { it.key.endsWith(TOKEN_SUFFIX) }.map {
            val type = it.key.removeSuffix(TOKEN_SUFFIX)
            val event = this.deviceSettings[type + EVENT_SUFFIX, GREETINGS_EVENT]
            val regex = this.deviceSettings[type + REGEX_SUFFIX, ".*"]
            type to Token(type, it.value?.toString(), regex, event)
        }.toMap(mutableMapOf())
    }
    
    private enum class Ids {
        FIRST_TIME
    }
    
}