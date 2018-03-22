package ai.zenkai.zenkai.common.services.speech

import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.api.ui.AIButton.AIButtonListener
import ai.api.ui.AIDialog.AIDialogListener

interface VoiceListener : AIDialogListener, AIButtonListener {
    
    override fun onResult(result: AIResponse)
    
    override fun onError(error: AIError)
    
    override fun onCancelled()
    
}