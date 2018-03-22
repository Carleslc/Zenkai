package ai.zenkai.zenkai.repositories

import kotlin.properties.Delegates.notNull

object SettingsRepository {
    
    var deviceSettings: DeviceSettings by notNull()
    
    fun isFirstTime() = deviceSettings[Ids.FIRST_TIME.toString(), true]
    
    fun setFirstTime() {
        deviceSettings[Ids.FIRST_TIME.toString()] = false
    }
    
    private enum class Ids {
        FIRST_TIME
    }
    
}