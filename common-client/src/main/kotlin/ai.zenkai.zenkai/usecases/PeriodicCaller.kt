package ai.zenkai.zenkai.usecases

import kotlinx.coroutines.experimental.Job
import ai.zenkai.zenkai.common.Provider
import ai.zenkai.zenkai.common.delay
import ai.zenkai.zenkai.common.launchUI

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