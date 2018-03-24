package ai.zenkai.zenkai.i18n

import ai.zenkai.zenkai.i18n.SupportedLanguage.ENGLISH
import ai.zenkai.zenkai.i18n.SupportedLanguage.SPANISH
import ai.zenkai.zenkai.i18n.i18n.LOCALES_SUPPORT
import ai.zenkai.zenkai.i18n.i18n.SUPPORT_LOCALES
import com.vdurmont.emoji.EmojiParser
import klogging.KLoggerHolder
import klogging.WithLogging
import java.util.Locale
import java.util.ResourceBundle

actual object i18n : WithLogging by KLoggerHolder() {
    
    private val strings by lazy { HashMap<S, String>() }
    
    actual operator fun get(id: S) = strings[id]!!
    
    actual fun String.formatEmojis() = EmojiParser.parseFromUnicode(this) {
        emoji(it.emoji.htmlDecimal.replace("[^0-9]".toRegex(), "").toInt())
    }
    
    actual fun String.removeEmojis() = EmojiParser.removeAllEmojis(this)
    
    private fun emoji(code: Int) = String(Character.toChars(code))
    
    actual fun setLanguage(language: SupportedLanguage) {
        val defaultBundle = ResourceBundle.getBundle("strings")
        val localeBundle = ResourceBundle.getBundle("strings", language.locale)
        
        S.values().forEach {
            val key = it.toString()
            val bundle = if (localeBundle.containsKey(key)) localeBundle else defaultBundle
            
            strings[it] = bundle.getString(key).trim()
        }
    
        logger.debug { "[${this::class.simpleName}] Set language to $language" }
    }
    
    val SUPPORT_LOCALES = mapOf(
        ENGLISH to "en",
        SPANISH to "es"
    )
    
    val LOCALES_SUPPORT = mapOf(*SUPPORT_LOCALES.map { it.value to it.key }.toTypedArray())
    
}

val SupportedLanguage.locale get() = Locale(SUPPORT_LOCALES[this])

val Locale.supportedLanguage get() = LOCALES_SUPPORT.getOrDefault(language, DEFAULT_LANGUAGE)