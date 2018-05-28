package ai.zenkai.zenkai.view.layout

import ai.api.ui.AIDialog
import ai.zenkai.zenkai.common.services.bot.AndroidAIConfiguration
import ai.zenkai.zenkai.common.services.speech.VoiceListener
import ai.zenkai.zenkai.common.services.speech.VoiceUI
import ai.zenkai.zenkai.presentation.BaseView
import ai.zenkai.zenkai.view.BaseActivity
import android.content.Context
import me.carleslc.kotlin.extensions.standard.isNotNull
import org.jetbrains.anko.*

class DialogflowMicrophoneDialog(override val context: Context, override val view: BaseView) : VoiceUI, AnkoLogger {
    
    private lateinit var listener: VoiceListener
    
    private var mic: AIDialog? = null
    
    constructor(activity: BaseActivity): this(activity, activity)
    
    override fun show(config: AndroidAIConfiguration, listener: VoiceListener) {
        if (isShown()) {
            debug { "Already shown" }
            mic!!.setResultsListener(listener)
            resume()
            return
        }
        this.listener = listener
        mic = AIDialog(context, config).also {
            it.setResultsListener(listener)
            it.showAndListen()
            debug { "Shown" }
        }
    
        mic?.dialog?.setOnDismissListener {
            debug { "Dismiss" }
            mic?.pause()
            mic = null
            listener.onCancelled()
        }
    }
    
    override fun isShown() = mic.isNotNull()
    
    override fun pause() {
        if (isShown()) {
            debug { "Pause" }
            mic?.pause()
        }
    }
    
    override fun resume() {
        if (isShown()) {
            debug { "Resume" }
            mic?.resume()
        }
    }
    
    override fun close() {
        if (isShown()) {
            debug { "Close" }
            mic?.dialog?.dismiss()
        }
    }
    
}