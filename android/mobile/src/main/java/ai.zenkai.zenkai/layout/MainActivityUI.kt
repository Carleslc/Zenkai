package ai.zenkai.zenkai.layout

import ai.zenkai.zenkai.MainActivity
import ai.zenkai.zenkai.R
import ai.zenkai.zenkai.common.elevate
import ai.zenkai.zenkai.common.margin
import android.support.v4.content.ContextCompat.getDrawable
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.*

private const val INPUT_TEXT_HEIGHT = 50

class MainActivityUI : AnkoComponent<MainActivity> {
    
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        relativeLayout {
            id = R.id.main_layout
            
            // Chat Messages
            recyclerView {
                bottomPadding = dip(INPUT_TEXT_HEIGHT)
                clipToPadding = true
                background = getDrawable(ctx, R.color.grey_light)
            }.lparams(width = matchParent, height = matchParent)
            
            relativeLayout {
                id = R.id.chat_messages_layout
    
                // Microphone
                val microphone = relativeLayout {
                    id = R.id.voice_input_layout
                    elevate(4)
        
                    imageView {
                        id = R.id.microphone
                        imageResource = R.drawable.ic_mic
                    }.lparams(width = dip(25), height = dip(25)) {
                        centerInParent()
                    }
                    
                    background = getDrawable(ctx, R.drawable.fab)
                }.lparams(width = dip(50), height = dip(50)) {
                    margin(ctx, 0, 5, 0, 10)
                    alignParentEnd()
                    centerInParent()
                }
                
                // Text
                relativeLayout {
                    id = R.id.chat_input_layout
                    
                    background = getDrawable(ctx, R.drawable.text_input)
                    elevate(2)
                    
                    editText {
                        hint = ctx.getString(R.string.text_input_hint)
                        minHeight = dip(INPUT_TEXT_HEIGHT)
                        textSize = 18f
                        background = getDrawable(ctx, R.color.white)
                    }.lparams(width = matchParent) {
                        margin(ctx, 20, 20)
                    }
                    
                }.lparams(width = matchParent) {
                    startOf(microphone)
                    margin(ctx, 10, 5, 0, 10)
                    centerVertically()
                }
            }.lparams(width = matchParent) {
                alignParentBottom()
            }
        }
    }
    
}