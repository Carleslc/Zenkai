package ai.zenkai.zenkai.data

data class MessagesData(val messages: List<Message>) : Collection<Message> by messages {
    
    constructor() : this(emptyList())
    
}