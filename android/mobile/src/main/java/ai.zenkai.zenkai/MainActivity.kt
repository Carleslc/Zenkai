package ai.zenkai.zenkai

import ai.zenkai.zenkai.layout.MainActivityUI
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), AnkoLogger {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUI().setContentView(this)
        info("${getString(R.string.name)} Started")
    }
    
}
