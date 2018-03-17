package ai.zenkai.zenkai.common

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri

val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.openUrl(url: String?) {
    if (url.isNullOrBlank()) return
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    if (browserIntent.resolveActivity(packageManager) != null) {
        startActivity(browserIntent)
    }
}

fun Context.startShareIntent(subject: String, text: String, title: String = "Share via") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, title))
}

fun Context.canShare() = Intent(Intent.ACTION_SEND).resolveActivity(packageManager) != null