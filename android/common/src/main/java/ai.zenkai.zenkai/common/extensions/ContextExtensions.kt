package ai.zenkai.zenkai.common.extensions

import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import org.jetbrains.anko.*
import java.util.regex.Pattern

val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.openUrl(text: String?, logger: AnkoLogger): Boolean {
    fun openUrl(url: String) {
        logger.info("Opening $url")
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (browserIntent.resolveActivity(packageManager) != null) {
            startActivity(browserIntent)
        }
    }
    if (text == null || text.isBlank()) return false
    for (line in text.lineSequence()) {
        val matcher = Patterns.WEB_URL.matcher(line)
        if (!matcher.matches()) continue
        openUrl(matcher.group(1))
        return true
    }
    return false
}

fun Context.startShareIntent(subject: String, text: String, title: String = i18n[S.SHARE]): Boolean {
    val baseIntent = Intent(Intent.ACTION_SEND)
    val canShare = baseIntent.resolveActivity(packageManager) != null
    if (canShare) {
        val sendIntent = baseIntent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(sendIntent, title))
        return true
    }
    return false
    
}