package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.R.string
import ai.zenkai.zenkai.common.recycler.BaseRecyclerViewAdapter
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.TextMessage
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.presentation.messages.MessagesPresenter
import ai.zenkai.zenkai.presentation.messages.MessagesView
import ai.zenkai.zenkai.services.speech.say
import ai.zenkai.zenkai.view.layout.ChatUI
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.marcinmoskala.kotlinandroidviewbindings.bindToVisibility
import me.carleslc.kotlin.extensions.standard.letIfTrue
import org.jetbrains.anko.*
import kotlin.properties.Delegates.notNull

class ChatActivity : BaseActivity(), MessagesView {
    
    private val presenter by presenter { MessagesPresenter(this) }
    
    private val UI by lazy { ChatUI() }
    
    private var messagesAdapter: BaseRecyclerViewAdapter<MessageItemAdapter> by notNull()
    
    override var loading by bindToVisibility { UI.refresh }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UI.setContentView(this)
        init()
        info("${getString(string.name)} Started")
    }
    
    private fun init() {
        loadMessages()
        UI.action.setOnClickListener {
            UI.text.isEmpty().letIfTrue(::onMicrophone, ::onSend)
        }
    }
    
    private fun loadMessages() {
        UI.messages.layoutManager = LinearLayoutManager(this)
        messagesAdapter = BaseRecyclerViewAdapter<MessageItemAdapter>()
        UI.messages.adapter = messagesAdapter
        presenter.load()
    }
    
    override fun add(messages: Collection<Message>) {
        messagesAdapter.items.addAll(messages.map { MessageItemAdapter(it) })
        messagesAdapter.notifyDataSetChanged()
    }
    
    override fun add(message: Message) {
        messagesAdapter.items.add(MessageItemAdapter(message))
        messagesAdapter.notifyDataSetChanged()
    }
    
    private fun onSend() {
        presenter.onNewMessage(TextMessage(UI.text))
        UI.text = ""
    }
    
    private fun onMicrophone() {
        show("Not implemented yet")
    }
    
}