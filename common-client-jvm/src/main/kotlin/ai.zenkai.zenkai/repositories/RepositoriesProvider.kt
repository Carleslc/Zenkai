package ai.zenkai.zenkai.repositories

actual object RepositoriesProvider {
    actual fun getMessagesRepository(): MessagesRepository = MessagesRepositoryImpl()
}