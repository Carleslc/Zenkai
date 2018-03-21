package ai.zenkai.zenkai.common.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION_CODES
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import org.jetbrains.anko.*

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun <T: View> T.elevate(height: Int) {
    requiresVersion(VERSION_CODES.LOLLIPOP) {
        @SuppressLint("NewApi")
        elevation = context.dip(height).toFloat()
    }
}

fun MarginLayoutParams.margin(context: Context, start: Int = 0, end: Int = 0, top: Int = 0, bottom: Int = 0) {
    with (context) {
        marginStart = dip(start)
        marginEnd = dip(end)
        topMargin = dip(top)
        bottomMargin = dip(bottom)
    }
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.longSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}