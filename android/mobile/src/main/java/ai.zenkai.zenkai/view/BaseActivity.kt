package ai.zenkai.zenkai.view

import ai.zenkai.zenkai.BuildConfig
import ai.zenkai.zenkai.common.extensions.longSnackbar
import ai.zenkai.zenkai.common.extensions.snackbar
import ai.zenkai.zenkai.presentation.BaseView
import ai.zenkai.zenkai.presentation.Presenter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*

abstract class BaseActivity : AppCompatActivity(), BaseView, AnkoLogger {

    protected fun <T : Presenter> presenter(init: () -> T) = lazy(init).also { lazyPresenters += it }

    private var lazyPresenters: List<Lazy<Presenter>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lazyPresenters.forEach { it.value.onCreate() }
    }

    override fun onDestroy() {
        super.onDestroy()
        lazyPresenters.forEach { it.value.onDestroy() }
    }
    
    override fun show(message: String) {
        contentView?.snackbar(message) ?: toast(message)
    }
    
    private fun showLong(message: String) {
        contentView?.longSnackbar(message) ?: longToast(message)
    }

    override fun showError(error: Throwable) {
        logError(error)
        showLong(error.message ?: "Error: ${error::class.simpleName}")
    }
    
    override fun showError(message: String) {
        logError(message)
        showLong(message)
    }

    override fun logError(error: Throwable) {
        if (BuildConfig.DEBUG) error(error.message, error)
    }
    
    private fun logError(message: String) {
        if (BuildConfig.DEBUG) error(message)
    }
}