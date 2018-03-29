package ai.zenkai.zenkai.serialization

import ai.zenkai.zenkai.model.Token

data class BotResponse(val messages: List<SimpleBotMessage>?, val error: BotError?, val tokens: List<Token>?)