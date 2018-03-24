package ai.zenkai.zenkai.data

import ai.zenkai.zenkai.DateTime
import ai.zenkai.zenkai.i18n.i18n.formatEmojis

abstract class Message(message: String) {
    
    val message = message.trim().formatEmojis()
    
    val date = DateTime.now()
    
    fun isEmpty() = message.isEmpty()
    
    override fun toString() = "${this::class.simpleName}(message=$message, date=$date)"
    
}