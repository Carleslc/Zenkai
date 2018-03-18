package ai.zenkai.zenkai.common

import kotlinx.coroutines.experimental.Job

interface PeriodicCaller {

    fun start(timeMillis: Long, callback: () -> Unit): Job

    class PeriodicCallerImpl : PeriodicCaller {
        override fun start(timeMillis: Long, callback: () -> Unit) = launchUI {
            while (true) {
                delay(timeMillis)
                callback()
            }
        }
    }

    companion object : Provider<PeriodicCaller>() {
        override fun create(): PeriodicCaller = PeriodicCallerImpl()
    }
}