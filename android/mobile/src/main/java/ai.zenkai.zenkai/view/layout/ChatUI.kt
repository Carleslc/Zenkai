package ai.zenkai.zenkai.view.layout

import ai.zenkai.zenkai.R
import ai.zenkai.zenkai.common.extensions.elevate
import ai.zenkai.zenkai.common.extensions.margin
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.view.ChatActivity
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.widget.RecyclerView
import android.view.Gravity.CENTER
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.*
import kotlin.properties.Delegates.notNull

private const val INPUT_TEXT_HEIGHT = 46

class ChatUI : AnkoComponent<ChatActivity> {
    
    var refresh: ProgressBar by notNull()
    var messages: RecyclerView by notNull()
    var action: RelativeLayout by notNull()
    var actionImage: ImageView by notNull()
    var textInput: EditText by notNull()
    
    var text
        get() = textInput.text.toString().trim()
        set(value) = textInput.setText(value)
    
    private var enabledFabBg: Drawable by notNull()
    private var disabledFabBg: Drawable by notNull()
    
    override fun createView(ui: AnkoContext<ChatActivity>) = with(ui) {
        relativeLayout {
            id = R.id.main_layout
            
            // Refresh Status
            refresh = progressBar().lparams {
                gravity = CENTER
            }
            
            // Chat Messages
            messages = recyclerView {
                bottomPadding = dip(10 + INPUT_TEXT_HEIGHT)
                clipToPadding = true
                background = getDrawable(ctx, R.color.grey_light)
            }.lparams(width = matchParent, height = matchParent)
            
            // Message Input
            relativeLayout {
                id = R.id.chat_messages_layout
    
                // Send / Microphone
                action = relativeLayout {
                    id = R.id.voice_input_layout
                    elevate(4)
        
                    actionImage = imageView {
                        id = R.id.action_image
                        imageResource = R.drawable.ic_mic
                    }.lparams(width = dip(25), height = dip(25)) {
                        centerInParent()
                        centerHorizontally()
                    }
                }.lparams(width = dip(50), height = dip(50)) {
                    margin(ctx, 0, 5, 0, 10)
                    alignParentEnd()
                    centerInParent()
                }
    
                enabledFabBg = getDrawable(ctx, R.drawable.fab)!!
                disabledFabBg = getDrawable(ctx, R.drawable.disabled_fab)!!
                actionEnabled(true)
                
                // Text
                relativeLayout {
                    id = R.id.chat_input_layout
                    
                    background = getDrawable(ctx, R.drawable.text_input)
                    elevate(2)
    
                    textInput = editText {
                        hint = i18n[S.INPUT_TEXT_HINT]
                        minHeight = dip(INPUT_TEXT_HEIGHT)
                        textSize = 18f
                        background = getDrawable(ctx, R.color.white)
                    }.lparams(width = matchParent) {
                        margin(ctx, 20, 20)
                    }
                    
                }.lparams(width = matchParent) {
                    startOf(action)
                    margin(ctx, 10, 5, 0, 10)
                    centerVertically()
                }
            }.lparams(width = matchParent) {
                alignParentBottom()
            }
        }
    }
    
    fun actionEnabled(enabled: Boolean) {
        action.isEnabled = enabled
        action.background = if (enabled) enabledFabBg else disabledFabBg
    }
    
}