package ai.zenkai.zenkai.presentation

import ai.zenkai.zenkai.common.doAsync
import ai.zenkai.zenkai.common.launchUI
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job

abstract class BasePresenter : Presenter {

    private var jobs: MutableList<Job> = mutableListOf()
    
    protected fun async(block: suspend CoroutineScope.() -> Unit) {
        jobs.add(doAsync(block))
    }
    
    protected fun UI(block: suspend CoroutineScope.() -> Unit) {
        jobs.add(launchUI(block))
    }

    override fun onDestroy() {
        jobs.forEach { it.cancel() }
    }
    
}