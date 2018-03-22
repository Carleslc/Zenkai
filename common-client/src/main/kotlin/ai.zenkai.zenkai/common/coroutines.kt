package ai.zenkai.zenkai.common

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

// Should be set for different platforms
var UIx: CoroutineContext = DefaultDispatcher

fun launchUI(block: suspend CoroutineScope.() -> Unit): Job = launch(UIx) { block() }

fun <T> doAsync(block: suspend CoroutineScope.() -> T): Deferred<T> = async { block() }

expect suspend fun delay(millis: Long)