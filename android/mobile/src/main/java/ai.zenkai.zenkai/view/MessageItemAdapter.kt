package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.R
import ai.zenkai.zenkai.common.extensions.bindView
import ai.zenkai.zenkai.common.recycler.BaseViewHolder
import ai.zenkai.zenkai.common.recycler.ItemAdapter
import ai.zenkai.zenkai.common.extensions.visible
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MessageItemAdapter(private val message: Message) : ItemAdapter<MessageItemAdapter.ViewHolder>(R.layout.message) {
    
    override fun onCreateViewHolder(itemView: View, parent: ViewGroup) = ViewHolder(itemView)
    
    override fun ViewHolder.onBindViewHolder() {
        if (message is BotMessage) {
            botText.text = message.message
            userText.visible = false
        } else {
            userText.text = message.message
            botText.visible = false
        }
    }
    
    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val botText: TextView by bindView(R.id.botText)
        val userText: TextView by bindView(R.id.userText)
    }
    
}