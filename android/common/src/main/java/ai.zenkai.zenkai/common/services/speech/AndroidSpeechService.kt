package ai.zenkai.zenkai.common.services.speech

import ai.api.RequestExtras
import ai.api.model.AIError
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import ai.zenkai.zenkai.common.IdGenerator
import ai.zenkai.zenkai.common.services.bot.AndroidAIConfiguration
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.i18n.locale
import ai.zenkai.zenkai.model.VoiceMessage
import ai.zenkai.zenkai.services.bot.DialogflowService
import ai.zenkai.zenkai.services.speech.SpeechService
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Factory.onFinish
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.*
import kotlin.properties.Delegates.notNull

object AndroidSpeechService : SpeechService(), VoiceListener, AnkoLogger {
    
    private var UI: VoiceUI by notNull()
    private var context: Context by notNull()
    private var TTS: TextToSpeech by notNull()
    private val utteranceIds by lazy { IdGenerator() }
    private var listeningCallback: ListeningCallback? = null
    private val pendingMessagesQueue by lazy { mutableListOf<VoiceMessage>() }
    private val speakingMessages by lazy { mutableMapOf<String, VoiceMessage>() }
    
    private var started = false
    private var speakerEnabledOnPause = true
    
    private const val NO_INPUT = "Speech recognition engine error: No speech input."
    private const val NO_RESULT = "Speech recognition engine error: No recognition result matched."
    
    override var speakerEnabled: Boolean = true
        set(value) {
            if (value && !field && pendingMessagesQueue.isNotEmpty()) {
                delaySpeakerEnabling()
            } else {
                field = value
                debug { "Speaker " + if (value) "enabled" else "disabled" }
            }
        }
    
    override var microphoneEnabled: Boolean = true
        set(value) {
            field = value
            debug { "Microphone " + if (value) "enabled" else "disabled" }
        }
    
    fun attach(context: Context, ui: VoiceUI): AndroidSpeechService {
        this.context = context
        UI = ui
        start()
        return this
    }
    
    fun start() {
        if (started) return
        debug { "Starting TTS ($language)" }
        TTS = TextToSpeech(context) {
            it.letTTSIfNoError(::started)
        }
    }
    
    private fun started() {
        TTS.language = language.locale
        TTS.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                    speakingMessages[utteranceId]?.speakingListener?.onSpeakStarted()
                }
                
                override fun onDone(utteranceId: String) {
                    speakingMessages[utteranceId]?.speakingListener?.onSpeakCompleted()
                    speakingMessages.remove(utteranceId)
                }
    
                override fun onError(utteranceId: String?) {
                    speakingMessages[utteranceId]?.speakingListener?.onSpeakCancelled()
                    speakingMessages.remove(utteranceId)
                    error("Speaking Utterance Error (id: $utteranceId)")
                }
            }
        )
        started = true
        consumePendingMessages()
        debug {"TTS ($language) Started" }
    }
    
    private fun consumePendingMessages() {
        debug { "Speaking ${pendingMessagesQueue.size} delayed messages" }
        val pendingMessages = pendingMessagesQueue.listIterator()
        while (pendingMessages.hasNext()) {
            val message = pendingMessages.next()
            pendingMessages.remove()
            message.say()
        }
    }
    
    override fun pause() {
        savePauseState()
        UI.pause()
        TTS.stop()
        debug { "TTS ($language) Stopping" }
        pendingMessagesQueue.forEach {
            it.speakingListener?.onSpeakStarted()
            it.speakingListener?.onSpeakCancelled()
        }
        pendingMessagesQueue.clear()
        speakingMessages.forEach {
            it.value.speakingListener?.onSpeakCancelled()
        }
        speakingMessages.clear()
        debug { "TTS ($language) Stopped" }
    }
    
    override fun resume() {
        restorePauseState()
        UI.resume()
        debug { "TTS ($language) Resumed" }
    }
    
    override fun stop() {
        UI.close()
        TTS.shutdown()
        started = false
        savePauseState()
        debug { "TTS ($language) Shutdown" }
    }
    
    private fun savePauseState() {
        speakerEnabledOnPause = speakerEnabled
        speakerEnabled = false
    }
    
    private fun restorePauseState() {
        speakerEnabled = speakerEnabledOnPause
    }
    
    override fun onSpeak(message: VoiceMessage) {
        if (started) {
            speak(message)
        } else {
            debug { "Message '${message.message}' delayed because TTS is not started" }
            pendingMessagesQueue.add(message)
        }
    }
    
    override fun onListen(callback: ListeningCallback) {
        listeningCallback = callback
        UI.show(DialogflowService.config as AndroidAIConfiguration, this)
    }
    
    override fun onError(error: AIError) {
        when {
            error.message == NO_INPUT -> {}
            error.message == NO_RESULT -> UI.context.toast(i18n[S.TRY_AGAIN])
            else -> {
                UI.close()
                val message = DialogflowService.checkNetworkErrorMessage(error.message)
                listeningCallback?.onError(ListeningException(message))
                listeningCallback = null
            }
        }
    }
    
    override fun onCancelled() {
        debug { "Cancelled microphone" }
        listeningCallback?.onCancel()
    }
    
    override fun onRequest(query: String, request: AIRequest, requestExtras: RequestExtras?): AIResponse? {
        val event = listeningCallback?.onRequest(VoiceMessage(query))
        return runBlocking {
            val result = if (event != null) {
                DialogflowService.sendEventForResponse(event)
            } else DialogflowService.ask(query, request)
            val response = result.first
            if (result.second.isError() && !result.second.isLoginError()) {
                listeningCallback?.onError(ListeningException(result.second.error!!.message))
                listeningCallback = null
            } else with (response.result) {
                debug { "Received response for '${response.result.resolvedQuery}' on action " +
                    "${response.result.action} with status ${response.status.code}" }
                listeningCallback?.onResults(result.second)
                listeningCallback = null
            }
            response
        }
    }
    
    override fun onResult(response: AIResponse) { /* Response intercepted above in onRequest */ }
    
    private fun Int.letTTSIfNoError(block: () -> Unit) {
        if (this != TextToSpeech.ERROR) {
            block()
        } else {
            UI.view.show(S.TTS_ERROR)
        }
    }
    
    @SuppressWarnings("deprecation")
    private fun speak(message: VoiceMessage) {
        debug { "Speaking: $message" }
        val id = utteranceIds.getNextString()
        speakingMessages[id] = message
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            TTS.speak(message.message, TextToSpeech.QUEUE_ADD, null, id)
        } else {
            TTS.speak(message.message, TextToSpeech.QUEUE_ADD, idMap(id))
        }
    }
    
    private fun delaySpeakerEnabling() {
        pendingMessagesQueue.last().speakingListener = onFinish {
            speakerEnabled = true
        }
    }
    
    private fun idMap(id: String): HashMap<String, String> {
        return HashMap<String, String>(1).apply { put(KEY_PARAM_UTTERANCE_ID, id) }
    }
    
}