package ai.zenkai.zenkai.model

import ai.zenkai.zenkai.DateTime
import ai.zenkai.zenkai.i18n.i18n.formatEmojis

abstract class Message(message: String) {
    
    val message = message.trim().formatEmojis()
    
    val date = DateTime.now()
    
    open fun isEmpty() = message.isBlank()
    
    override fun toString() = "${this::class.simpleName}(message=$message, date=$date)"
    
}