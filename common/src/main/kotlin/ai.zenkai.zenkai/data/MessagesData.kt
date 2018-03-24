package ai.zenkai.zenkai.data

data class MessagesData(private val messages: MutableList<Message>) : MutableCollection<Message> by messages {
    
    constructor() : this(mutableListOf())
    
}