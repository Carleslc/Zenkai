package ai.zenkai.zenkai.data

sealed class Token(val token: String, val type: String)

class TrelloToken(token: String) : Token(token, "trello")