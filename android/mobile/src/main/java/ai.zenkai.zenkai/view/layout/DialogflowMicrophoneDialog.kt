package ai.zenkai.zenkai.view.layout

import ai.api.ui.AIDialog
import ai.zenkai.zenkai.common.services.bot.AndroidAIConfiguration
import ai.zenkai.zenkai.common.services.speech.VoiceListener
import ai.zenkai.zenkai.common.services.speech.VoiceUI
import ai.zenkai.zenkai.presentation.BaseView
import ai.zenkai.zenkai.view.BaseActivity
import android.content.Context

class DialogflowMicrophoneDialog(override val context: Context, override val view: BaseView) : VoiceUI {
    
    constructor(activity: BaseActivity): this(activity, activity)
    
    override fun show(config: AndroidAIConfiguration, listener: VoiceListener) {
        val mic = AIDialog(context, config).also {
            it.setResultsListener(listener)
            it.showAndListen()
        }
    
        mic.dialog.setOnDismissListener {
            mic.pause()
            mic.close()
            listener.onCancelled()
        }
    }
    
}