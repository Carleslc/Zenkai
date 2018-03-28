package ai.zenkai.zenkai.data

import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n

open class TextMessage(text: String) : Message(text) {
    
    constructor(id: S) : this(i18n[id])
    
    constructor() : this("")
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        
        other as TextMessage
        
        if (message != other.message) return false
        
        return true
    }
    
    override fun hashCode() = message.hashCode()
    
}