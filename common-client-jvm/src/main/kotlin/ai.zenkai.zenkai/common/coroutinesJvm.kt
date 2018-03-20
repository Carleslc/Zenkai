package ai.zenkai.zenkai.common

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

actual suspend fun delay(millis: Long): Unit = kotlinx.coroutines.experimental.delay(millis, TimeUnit.MILLISECONDS)