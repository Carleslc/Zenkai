package ai.zenkai.zenkai.data

import ai.zenkai.zenkai.DateTime

abstract class Message(message: String) {
    
    val message = message.trim()
    val date = DateTime.now()
    
    fun isEmpty() = message.isEmpty()
    
}