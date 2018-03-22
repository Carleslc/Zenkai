package ai.zenkai.zenkai.i18n

expect object i18n {
    
    operator fun get(id: S): String
    
    fun setLanguage(language: SupportedLanguage)
    
}

enum class S {
    INPUT_TEXT_HINT,
    MICROPHONE_DISABLED,
    TTS_ERROR,
    HELLO,
    GREETING;
}