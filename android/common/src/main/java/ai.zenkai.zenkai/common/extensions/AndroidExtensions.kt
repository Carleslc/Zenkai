package ai.zenkai.zenkai.common.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import me.carleslc.kotlin.extensions.standard.alsoIfTrue

@SuppressLint("NewApi")
fun requiresVersion(apiVersion: Int, block: () -> Unit): Boolean {
    return (VERSION.SDK_INT >= apiVersion).alsoIfTrue(block)
}

fun hasPermission(activity: Activity, permission: String, requestCode: Int, request: Boolean = false): Boolean {
    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
        if (request) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
        return false
    }
    return true
}

fun hideKeyboard(activity: Activity) {
    val view = activity.findViewById<View>(android.R.id.content)
    if (view != null) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}