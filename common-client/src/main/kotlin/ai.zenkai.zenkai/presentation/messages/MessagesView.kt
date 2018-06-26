package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.presentation.BaseView

interface MessagesView : BaseView {
    
    var loading: Boolean
    
    fun add(message: Message)
    
    fun addAll(messages: Collection<Message>)
    
    fun clearInput()
    
    fun openUrl(text: String): Boolean
    
    fun share(title: String, content: String): Boolean
    
    fun copyToClipboard(label: String, text: String)
    
    fun hasMicrophonePermission(request: Boolean = false): Boolean
    
}