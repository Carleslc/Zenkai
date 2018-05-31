package ai.zenkai.zenkai.repositories

interface DeviceSettings {
    
    fun getAll(): Map<String, *>
    
    operator fun get(id: String, default: Int = 0): Int
    
    operator fun get(id: String, default: String = ""): String
    
    operator fun get(id: String, default: Boolean = false): Boolean
    
    operator fun set(id: String, value: Int)
    
    operator fun set(id: String, value: String)
    
    operator fun set(id: String, value: Boolean)
    
    fun clear(id: String)
    
    fun isNetworkAvailable(): Boolean
    
}