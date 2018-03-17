package ai.zenkai.zenkai.common

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

// Should be set for different platforms
var UIx: CoroutineContext = DefaultDispatcher

fun launchUI(block: suspend () -> Unit): Job = launch(UIx) { block() }

expect suspend fun delay(time: Long)