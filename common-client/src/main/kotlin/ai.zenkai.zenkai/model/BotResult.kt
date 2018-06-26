package ai.zenkai.zenkai.model

import ai.zenkai.zenkai.i18n.S.TIMEOUT
import ai.zenkai.zenkai.serialization.BotError

data class BotResult(
    val messages: MutableList<BotMessage>,
    val timeout: Boolean,
    val error: BotError? = null,
    val tokens: List<Token>? = null) {
    
    val login get() = error?.requestToken
    
    fun isLoginError() = login != null
    fun isError() = error != null
    
    companion object Factory {
        fun timeout() = BotResult(mutableListOf(BotMessage(TIMEOUT)), true)
        fun success(messages: List<BotMessage>) = BotResult(messages.toMutableList(), false)
        fun empty() = success(listOf())
        fun error(error: BotError) = BotResult(mutableListOf(), false, error)
    }
}