package ai.zenkai.zenkai.serialization

import ai.zenkai.zenkai.model.Token

class BotError(val message: String, val status: Int, val error: String, val login: Token?)