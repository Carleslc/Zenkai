package ai.zenkai.zenkai.presentation

import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n

interface BaseView {
    
    fun show(message: String)
    
    fun show(stringId: S) = show(i18n[stringId])
    
    fun logError(error: Throwable)
    
    fun showError(error: Throwable)
    
    fun showError(message: String)
    
}