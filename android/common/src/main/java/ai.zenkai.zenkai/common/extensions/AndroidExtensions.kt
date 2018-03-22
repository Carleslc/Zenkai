package ai.zenkai.zenkai.common.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import me.carleslc.kotlin.extensions.standard.alsoIfTrue

@SuppressLint("NewApi")
fun requiresVersion(apiVersion: Int, block: () -> Unit): Boolean {
    return (VERSION.SDK_INT >= apiVersion).alsoIfTrue(block)
}

fun hasPermission(activity: Activity, permission: String, requestCode: Int): Boolean {
    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        return false
    }
    return true
}