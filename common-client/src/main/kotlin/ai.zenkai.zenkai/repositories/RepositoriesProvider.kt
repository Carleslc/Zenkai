package ai.zenkai.zenkai.repositories

expect object RepositoriesProvider {
    
    fun getMessagesRepository(): MessagesRepository
    
    fun getSettingsRepository(): SettingsRepository
    
}