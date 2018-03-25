package ai.zenkai.zenkai.common.services.speech

import ai.zenkai.zenkai.common.services.bot.AndroidAIConfiguration
import ai.zenkai.zenkai.presentation.BaseView
import android.content.Context

interface VoiceUI {

    val view: BaseView
    
    val context: Context
    
    fun show(config: AndroidAIConfiguration, listener: VoiceListener)
    
    fun pause()
    
    fun resume()
    
    fun close()
    
    fun isShown(): Boolean
    
}