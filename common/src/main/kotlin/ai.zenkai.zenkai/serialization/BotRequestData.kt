package ai.zenkai.zenkai.serialization

import ai.zenkai.zenkai.model.Token

data class BotRequestData(val timezone: String, val tokens: Collection<Token>)