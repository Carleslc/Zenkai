package ai.zenkai.zenkai.data

import ai.zenkai.zenkai.DateTime

abstract class Message(val message: String) {
    
    val date = DateTime.now()
    
}