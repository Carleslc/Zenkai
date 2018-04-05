package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.App
import ai.zenkai.zenkai.R
import ai.zenkai.zenkai.R.string
import ai.zenkai.zenkai.common.AndroidPermissions
import ai.zenkai.zenkai.common.TextMicAnimator
import ai.zenkai.zenkai.common.extensions.hasPermission
import ai.zenkai.zenkai.common.extensions.openUrl
import ai.zenkai.zenkai.common.extensions.visible
import ai.zenkai.zenkai.common.services.speech.AndroidSpeechService
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.i18n.supportedLanguage
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.model.TextMessage
import ai.zenkai.zenkai.presentation.messages.MessagesPresenter
import ai.zenkai.zenkai.presentation.messages.MessagesView
import ai.zenkai.zenkai.view.layout.ChatUI
import ai.zenkai.zenkai.view.layout.DialogflowMicrophoneDialog
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import klogging.KLogger
import me.carleslc.kotlin.extensions.standard.letIfTrue
import org.jetbrains.anko.*
import java.util.Locale
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
            debug { "Loading $value" }
        }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        UI.setContentView(this)
        AndroidSpeechService.attach(applicationContext, DialogflowMicrophoneDialog(this))
        init(savedInstanceState)
        info("${getString(string.name)} Started")
    }
    
    private fun init(savedInstanceState: Bundle?) {
        App.setLanguage(Locale.getDefault().supportedLanguage)
        fun RecyclerView.initMessages() {
            setHasFixedSize(true)
            messagesAdapter = MessagesAdapter(attached = UI.messages, onMessageClick = ::onMessageClick)
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(ctx)
            itemAnimator = DefaultItemAnimator()
        }
        UI.messages.initMessages()
        UI.textInput.hint = i18n[S.INPUT_TEXT_HINT]
        UI.textInput.addTextChangedListener(TextMicAnimator(ctx, UI.actionImage))
        UI.text = savedInstanceState?.getString("textInput") ?: ""
        UI.action.setOnClickListener {
            UI.text.isEmpty().letIfTrue(::onMicrophone, ::onSend)
        }
        presenter.onInit()
    }
    
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("textInput", UI.text)
        super.onSaveInstanceState(outState)
    }
    
    override fun onPause() {
        debug { "Pause" }
        presenter.pause()
        super.onPause()
    }
    
    override fun onResume() {
        if (!firstResume) {
            debug { "Resume" }
            presenter.resume()
        } else {
            firstResume = false
        }
        super.onResume()
    }
    
    override fun onDestroy() {
        debug { "Destroy" }
        presenter.stop()
        super.onDestroy()
    }
    
    override fun add(message: Message) {
        messagesAdapter.add(message)
    }
    
    override fun addAll(messages: Collection<Message>) {
        messagesAdapter.addAll(messages)
    }
    
    override fun onMessageInteraction(message: Message) {
        presenter.onMessageInteraction(message)
    }
    
    override fun clearInput() {
        UI.text = ""
    }
    
    override fun openUrl(text: String): Boolean {
        return ctx.openUrl(text, this)
    }
    
    override fun share(title: String, content: String) = ctx.share(content, subject = title)
    
    override fun copyToClipboard(label: String, text: String) {
        if (UI.text.isEmpty()) UI.text = text
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.primaryClip = ClipData.newPlainText(label, text)
        toast(i18n[S.CLIPBOARD])
        info { "$label $text copied" }
    }
    
    private fun onMessageClick(message: Message) = onMessageInteraction(message)
    
    private fun onSend() {
        presenter.onNewMessage(TextMessage(UI.text))
    }
    
    private fun onMicrophone() {
        presenter.onMicrophone(true)
    }
    
    override fun hasMicrophonePermission(request: Boolean): Boolean {
        return hasPermission(this, Manifest.permission.RECORD_AUDIO, AndroidPermissions.Code.RECORD_AUDIO, request)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        AndroidPermissions.onRequestMicrophone(presenter, requestCode, permissions, grantResults)
    }
    
}