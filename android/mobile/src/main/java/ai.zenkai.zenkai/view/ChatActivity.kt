package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.R.string
import ai.zenkai.zenkai.common.AndroidPermissions
import ai.zenkai.zenkai.common.TextMicAnimator
import ai.zenkai.zenkai.common.extensions.hasPermission
import ai.zenkai.zenkai.common.extensions.visible
import ai.zenkai.zenkai.common.services.speech.AndroidSpeechService
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.TextMessage
import ai.zenkai.zenkai.presentation.messages.MessagesPresenter
import ai.zenkai.zenkai.presentation.messages.MessagesView
import ai.zenkai.zenkai.view.layout.ChatUI
import ai.zenkai.zenkai.view.layout.DialogflowMicrophoneDialog
import android.Manifest
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
    
    private var messagesAdapter: MessagesAdapter by notNull()
    
    private var firstResume = true
    
    override var loading = false
        set(value) {
            UI.actionEnabled(!value)
            UI.refresh.visible = value
            field = value
        }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UI.setContentView(this)
        AndroidSpeechService.attach(applicationContext, DialogflowMicrophoneDialog(this))
        init()
        greetings()
        info("${getString(string.name)} Started")
    }
    
    private fun init() {
        fun RecyclerView.initMessages() {
            setHasFixedSize(true)
            messagesAdapter = MessagesAdapter(attached = UI.messages)
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
    
    private fun greetings() {
        presenter.greetings()
    }
    
    override fun onPause() {
        AndroidSpeechService.stop()
        super.onPause()
    }
    
    override fun onResume() {
        if (!firstResume) {
            presenter.onMicrophone()
        } else {
            firstResume = false
        }
        super.onResume()
    }
    
    override fun onDestroy() {
        AndroidSpeechService.shutdown()
        super.onDestroy()
    }
    
    override fun add(message: Message) {
        messagesAdapter.add(message)
    }
    
    override fun addAll(messages: Collection<Message>) {
        messagesAdapter.addAll(messages)
    }
    
    private fun onSend() {
        presenter.onNewMessage(TextMessage(UI.text))
        UI.text = ""
    }
    
    private fun onMicrophone() {
        presenter.onMicrophone()
    }
    
    override fun hasMicrophonePermission(): Boolean {
        return hasPermission(this, Manifest.permission.RECORD_AUDIO, AndroidPermissions.Code.RECORD_AUDIO)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        AndroidPermissions.onRequestMicrophone(presenter, requestCode, permissions, grantResults)
    }
    
}