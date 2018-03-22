package ai.zenkai.zenkai

import ai.zenkai.zenkai.common.DeviceSettingsSharedPreferences
import ai.zenkai.zenkai.common.UIx
import ai.zenkai.zenkai.common.services.bot.AndroidDialogFlowConfigurationProvider
import ai.zenkai.zenkai.common.services.speech.AndroidSpeechService
import ai.zenkai.zenkai.i18n.SupportedLanguage
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.i18n.supportedLanguage
import ai.zenkai.zenkai.repositories.SettingsRepository
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.bot.DialogFlowService
import android.support.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import org.jetbrains.anko.*
import java.util.Locale
import kotlinx.coroutines.experimental.android.UI as AndroidUI

class App : MultiDexApplication() {
    
    override fun onCreate() {
        super.onCreate()
        UIx = AndroidUI
        AndroidThreeTen.init(ctx)
        SettingsRepository.deviceSettings = DeviceSettingsSharedPreferences.attach(ctx)
        DialogFlowService.setConfigurationProvider(AndroidDialogFlowConfigurationProvider)
        ServicesProvider.setSpeechService(AndroidSpeechService)
        setLanguage(Locale.getDefault().supportedLanguage)
    }
    
    private fun setLanguage(language: SupportedLanguage) {
        i18n.setLanguage(language)
        ServicesProvider.getSpeechService().language = language
    }
    
}