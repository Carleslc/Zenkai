package ai.zenkai.zenkai.model

import kotlinx.serialization.Serializable

@Serializable
data class Token(val type: String, var token: String?, val regex: String, val loginEvent: String)