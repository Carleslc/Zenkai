package ai.zenkai.zenkai.layout

import ai.zenkai.zenkai.MainActivity
import android.support.constraint.ConstraintSet.PARENT_ID
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*

class MainActivityUI : AnkoComponent<MainActivity> {
    
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        constraintLayout {
            val hello = textView("Hello World!")
            
            applyConstraintSet {
                hello {
                    connect(
                        START to START of PARENT_ID,
                        END to END of PARENT_ID,
                        TOP to TOP of PARENT_ID,
                        BOTTOM to BOTTOM of PARENT_ID
                    )
                }
            }
        }
    }
    
}