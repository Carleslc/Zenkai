package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.R
import ai.zenkai.zenkai.common.extensions.bindView
import ai.zenkai.zenkai.common.extensions.inflate
import ai.zenkai.zenkai.common.extensions.visible
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MessagesAdapter(initialMessages: List<Message> = listOf(), private val attached: RecyclerView)
    : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val messages = initialMessages.toMutableList()

    fun add(message: Message) {
        messages.add(message)
        val end = itemCount - 1
        notifyItemInserted(end)
        attached.scrollToPosition(end)
    }
    
    fun addAll(newMessages: Collection<Message>) {
        val start = itemCount
        messages.addAll(newMessages)
        notifyItemRangeInserted(start, newMessages.size)
        attached.scrollToPosition(start)
    }
    
    override fun getItemCount() = messages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.message))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(messages[position])

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
        private val botText: TextView by bindView(R.id.botText)
        private val userText: TextView by bindView(R.id.userText)
        
        fun bind(message: Message) {
            if (message is BotMessage) {
                botText.text = message.message
                userText.visible = false
            } else {
                userText.text = message.message
                botText.visible = false
            }
        }
        
    }
}
