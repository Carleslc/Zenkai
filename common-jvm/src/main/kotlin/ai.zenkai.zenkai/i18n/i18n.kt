package ai.zenkai.zenkai.i18n

import ai.zenkai.zenkai.i18n.SupportedLanguage.ENGLISH
import ai.zenkai.zenkai.i18n.SupportedLanguage.SPANISH
import ai.zenkai.zenkai.i18n.i18n.LOCALES_SUPPORT
import ai.zenkai.zenkai.i18n.i18n.SUPPORT_LOCALES
import java.util.Locale
import java.util.ResourceBundle

actual object i18n {
    
    private val strings by lazy { HashMap<S, String>() }
    
    actual operator fun get(id: S) = strings[id]!!
    
    actual fun setLanguage(language: SupportedLanguage) {
        val defaultBundle = ResourceBundle.getBundle("strings")
        val localeBundle = ResourceBundle.getBundle("strings", language.locale)
        
        S.values().forEach {
            val key = it.toString()
            val bundle = if (localeBundle.containsKey(key)) localeBundle else defaultBundle
            
            strings[it] = bundle.getString(key).trim()
        }
    }
    
    val SUPPORT_LOCALES = mapOf(
        ENGLISH to "en",
        SPANISH to "es"
    )
    
    val LOCALES_SUPPORT = mapOf(*SUPPORT_LOCALES.map { it.value to it.key }.toTypedArray())
}

val SupportedLanguage.locale get() = Locale(SUPPORT_LOCALES[this])

val Locale.supportedLanguage get() = LOCALES_SUPPORT.getOrDefault(language, DEFAULT_LANGUAGE)