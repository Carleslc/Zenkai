package ai.zenkai.zenkai.common.extensions

import ai.zenkai.zenkai.common.R.color
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import android.graphics.drawable.Drawable



fun ImageView.loadImage(photoUrl: String) {
    Glide.with(context)
        .load(photoUrl)
        .asBitmap()
        .transform(BorderTransformation(context, 2,
            ContextCompat.getColor(context, color.grey_light)))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun Bitmap.addBorder(borderSize: Int, @ColorInt color: Int): Bitmap {
    val bmpWithBorder = Bitmap.createBitmap(width + borderSize * 2, height + borderSize * 2, config)
    val canvas = Canvas(bmpWithBorder)
    canvas.drawColor(color)
    canvas.drawBitmap(this, borderSize.toFloat(), borderSize.toFloat(), null)
    return bmpWithBorder
}

fun Context.getDrawableBitmap(@DrawableRes resId: Int): Bitmap {
    return (ContextCompat.getDrawable(this, resId) as BitmapDrawable).bitmap
}

fun Drawable.toBitmap(widthPixels: Int, heightPixels: Int): Bitmap {
    val mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(mutableBitmap)
    setBounds(0, 0, widthPixels, heightPixels)
    draw(canvas)
    return mutableBitmap
}

private class BorderTransformation(context: Context, private val borderSize: Int,
    @ColorInt private val borderColor: Int) : BitmapTransformation(context) {
    
    override fun transform(pool: BitmapPool, bmp: Bitmap, outWidth: Int, outHeight: Int): Bitmap =
        bmp.addBorder(borderSize, borderColor)
    
    override fun getId(): String = this.javaClass.name
}