package ai.zenkai.zenkai

import ai.zenkai.zenkai.common.UIx
import ai.zenkai.zenkai.common.services.AndroidSpeechService
import ai.zenkai.zenkai.services.ServicesProvider
import android.support.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import org.jetbrains.anko.*
import kotlinx.coroutines.experimental.android.UI as AndroidUI

class App : MultiDexApplication() {
    
    override fun onCreate() {
        super.onCreate()
        UIx = AndroidUI
        AndroidThreeTen.init(ctx)
        ServicesProvider.setSpeechService(AndroidSpeechService.attach(ctx))
    }
    
}
