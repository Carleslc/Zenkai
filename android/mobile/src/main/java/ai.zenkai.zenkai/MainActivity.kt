package ai.zenkai.zenkai

import ai.zenkai.zenkai.common.UIx
import ai.zenkai.zenkai.common.services.AndroidSpeechService
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.layout.MainActivityUI
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.speech.say
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import kotlinx.coroutines.experimental.android.UI as AndroidUI

class MainActivity : AppCompatActivity(), AnkoLogger {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIx = AndroidUI
        MainActivityUI().setContentView(this)
        ServicesProvider.setSpeechService(AndroidSpeechService(ctx))
        VoiceMessage("Hi! I'm alive! :)").say()
        info("${getString(R.string.name)} Started")
    }
    
}
