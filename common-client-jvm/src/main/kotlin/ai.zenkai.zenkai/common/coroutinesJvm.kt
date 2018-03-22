package ai.zenkai.zenkai.common

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.experimental.delay as delayCoroutine

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

actual suspend fun delay(millis: Long): Unit = delayCoroutine(millis, TimeUnit.MILLISECONDS)