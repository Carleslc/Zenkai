package ai.zenkai.zenkai.repositories

import ai.zenkai.zenkai.model.Token

object SettingsRepository {
    
    const val TOKEN_SUFFIX = "-token"
    
    private lateinit var tokens: MutableMap<String, Token>
    
    private lateinit var deviceSettings: DeviceSettings
    
    fun isFirstTime() = deviceSettings[Ids.FIRST_TIME.toString(), true]
    
    fun setFirstTime() {
        deviceSettings[Ids.FIRST_TIME.toString()] = false
    }
    
    fun setToken(update: Token) = with(update) {
        deviceSettings[type + TOKEN_SUFFIX] = token
        tokens[type] = update
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
            type to Token(type, it.value.toString())
        }.toMap(mutableMapOf())
    }
    
    private enum class Ids {
        FIRST_TIME
    }
    
}