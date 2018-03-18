package ai.zenkai.zenkai.common

import android.annotation.SuppressLint
import android.os.Build.VERSION
import android.support.design.widget.Snackbar
import android.view.View
import me.carleslc.kotlin.extensions.standard.alsoIfTrue

@SuppressLint("NewApi")
fun requiresVersion(apiVersion: Int, block: () -> Unit): Boolean {
    return (VERSION.SDK_INT >= apiVersion).alsoIfTrue(block)
}
