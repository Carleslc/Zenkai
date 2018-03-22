package ai.zenkai.zenkai.common

import ai.zenkai.zenkai.repositories.DeviceSettings
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlin.properties.Delegates.notNull

object DeviceSettingsSharedPreferences : DeviceSettings {
    
    private var context: Context by notNull()
    private var sharedPreferences: SharedPreferences by notNull()
    
    fun attach(context: Context): DeviceSettingsSharedPreferences {
        this.context = context
        sharedPreferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        return this
    }
    
    override operator fun get(id: String, default: Int): Int = sharedPreferences.getInt(id, default)
    
    override operator fun get(id: String, default: String): String = sharedPreferences.getString(id, default)
    
    override operator fun get(id: String, default: Boolean): Boolean = sharedPreferences.getBoolean(id, default)
    
    override operator fun set(id: String, value: Int) = edit { putInt(id, value) }
    
    override operator fun set(id: String, value: String) = edit { putString(id, value) }
    
    override operator fun set(id: String, value: Boolean) = edit { putBoolean(id, value) }
    
    private fun edit(block: Editor.() -> Unit) {
        val editor = sharedPreferences.edit()
        block(editor)
        editor.apply()
    }
    
}