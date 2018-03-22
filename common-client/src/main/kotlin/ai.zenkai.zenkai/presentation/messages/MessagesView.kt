package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.presentation.BaseView

interface MessagesView : BaseView {
    
    var loading: Boolean
    
    fun add(message: Message)
    
    fun addAll(messages: Collection<Message>)
    
    fun hasMicrophonePermission(request: Boolean = false): Boolean
    
}