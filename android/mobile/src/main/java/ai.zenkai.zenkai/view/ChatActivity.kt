package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.R.string
import ai.zenkai.zenkai.common.TextMicAnimator
import ai.zenkai.zenkai.common.extensions.visible
import ai.zenkai.zenkai.common.recycler.BaseRecyclerViewAdapter
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.TextMessage
import ai.zenkai.zenkai.presentation.messages.MessagesPresenter
import ai.zenkai.zenkai.presentation.messages.MessagesView
import ai.zenkai.zenkai.view.layout.ChatUI
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import me.carleslc.kotlin.extensions.standard.letIfTrue
import org.jetbrains.anko.*
import kotlin.properties.Delegates.notNull

class ChatActivity : BaseActivity(), MessagesView {
    
    private val presenter by presenter { MessagesPresenter(this) }
    
    private val UI by lazy { ChatUI() }
    
    private var messagesAdapter: BaseRecyclerViewAdapter<MessageItemAdapter> by notNull()
    
    override var loading = false
        set(value) {
            UI.actionEnabled(!value)
            UI.refresh.visible = value
        }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UI.setContentView(this)
        init()
        info("${getString(string.name)} Started")
    }
    
    private fun init() {
        fun RecyclerView.initMessages() {
            setHasFixedSize(true)
            messagesAdapter = BaseRecyclerViewAdapter(attached = UI.messages)
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(ctx)
            itemAnimator = DefaultItemAnimator()
        }
        UI.messages.initMessages()
        UI.textInput.addTextChangedListener(TextMicAnimator(ctx, UI.actionImage))
        UI.action.setOnClickListener {
            UI.text.isEmpty().letIfTrue(::onMicrophone, ::onSend)
        }
    }
    
    override fun add(message: Message) {
        messagesAdapter.add(MessageItemAdapter(message))
    }
    
    override fun addAll(messages: Collection<Message>) {
        messagesAdapter.addAll(messages.map { MessageItemAdapter(it) })
    }
    
    private fun onSend() {
        presenter.onNewMessage(TextMessage(UI.text))
        UI.text = ""
    }
    
    private fun onMicrophone() {
        show("Not implemented yet")
    }
    
}