package ai.zenkai.zenkai.i18n

expect object i18n {
    
    operator fun get(id: S): String
    
    fun String.formatEmojis(): String
    
    fun String.removeEmojis(): String
    
    fun setLanguage(language: SupportedLanguage)
    
}

enum class S {
    INPUT_TEXT_HINT,
    MICROPHONE_DISABLED,
    TTS_ERROR,
    HELLO,
    GREETING,
    TIMEOUT,
    EMPTY_ANSWER,
    TRY_AGAIN;
}