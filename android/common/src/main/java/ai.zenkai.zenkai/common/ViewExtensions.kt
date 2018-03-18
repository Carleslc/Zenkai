package ai.zenkai.zenkai.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build.VERSION_CODES
import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.getColor
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import org.jetbrains.anko.*

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun ImageView.loadImage(photoUrl: String) {
    Glide.with(context)
        .load(photoUrl)
        .asBitmap()
        .transform(BorderTransformation(context, 2, getColor(context, R.color.grey_light)))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
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

private class BorderTransformation(context: Context, private val borderSize: Int,
    @ColorInt private val borderColor: Int) : BitmapTransformation(context) {
    
    override fun transform(pool: BitmapPool, bmp: Bitmap, outWidth: Int, outHeight: Int): Bitmap =
        bmp.addBorder(borderSize, borderColor)
    
    override fun getId(): String = this.javaClass.name
}

private fun Bitmap.addBorder(borderSize: Int, @ColorInt color: Int): Bitmap {
    val bmpWithBorder = Bitmap.createBitmap(width + borderSize * 2, height + borderSize * 2, config)
    val canvas = Canvas(bmpWithBorder)
    canvas.drawColor(color)
    canvas.drawBitmap(this, borderSize.toFloat(), borderSize.toFloat(), null)
    return bmpWithBorder
}