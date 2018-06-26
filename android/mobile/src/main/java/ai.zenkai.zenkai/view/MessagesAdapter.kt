package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.R
import ai.zenkai.zenkai.common.extensions.bindView
import ai.zenkai.zenkai.common.extensions.inflate
import ai.zenkai.zenkai.common.extensions.visible
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.Message
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.sdk19.coroutines.*

class MessagesAdapter(initialMessages: List<Message> = listOf(), private val attached: RecyclerView,
    private val onMessageClick: (Message) -> Unit) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val messages = initialMessages.toMutableList()
    
    init { setHasStableIds(true) }

    fun add(message: Message) {
        messages.add(message)
        scrollToBottom()
        notifyItemInserted(itemCount - 1)
    }
    
    fun addAll(newMessages: Collection<Message>) {
        val start = itemCount
        val count = newMessages.size
        messages.addAll(newMessages)
        notifyItemRangeInserted(start, count)
        scrollToBottom()
    }
    
    fun scrollToBottom() = attached.scrollToPosition(itemCount - 1)
    
    override fun getItemId(position: Int) = position.toLong()
    
    override fun getItemViewType(position: Int) = position
    
    override fun getItemCount() = messages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.message), onMessageClick)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(messages[position])

    class ViewHolder(itemView: View, private val onMessageClick: (Message) -> Unit)
        : RecyclerView.ViewHolder(itemView) {
    
        private val botText: TextView by bindView(R.id.botText)
        private val userText: TextView by bindView(R.id.userText)
        
        fun bind(message: Message) {
            if (message is BotMessage) {
                botText.text = message.message
                botText.onClick { onMessageClick(message) }
                userText.visible = false
            } else {
                userText.text = message.message
                userText.onClick { onMessageClick(message) }
                botText.visible = false
            }
        }
        
    }
}