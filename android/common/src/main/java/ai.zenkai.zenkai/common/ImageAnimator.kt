package ai.zenkai.zenkai.common

import android.content.Context
import android.support.annotation.DrawableRes
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import org.jetbrains.anko.*

open class ImageAnimator(context: Context, val image: ImageView,
    @DrawableRes private val emptyImage: Int, @DrawableRes private val filledImage: Int) : TextWatcher {
    
    var active = false
        private set
    
    private val animOut = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
    private val animIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
    
    override fun afterTextChanged(s: Editable?) {}
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s.toString().trim { it <= ' ' }
        if (text.isNotEmpty() && !active) {
            animateTo(filledImage)
            active = true
        } else if (text.isEmpty() && active) {
            animateTo(emptyImage)
            active = false
        }
    }
    
    private fun animateTo(@DrawableRes newImage: Int) {
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                image.imageResource = newImage
                image.startAnimation(animIn)
            }
        })
        image.startAnimation(animOut)
    }
    
}