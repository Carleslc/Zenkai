package ai.zenkai.zenkai.presentation

interface BaseView {
    fun logError(error: Throwable)
    fun showError(error: Throwable)
}