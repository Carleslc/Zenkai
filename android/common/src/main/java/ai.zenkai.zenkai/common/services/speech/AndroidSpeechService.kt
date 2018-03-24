package ai.zenkai.zenkai.common.services.speech

import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.zenkai.zenkai.common.IdGenerator
import ai.zenkai.zenkai.common.services.bot.AndroidAIConfiguration
import ai.zenkai.zenkai.data.TextMessage
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.i18n.locale
import ai.zenkai.zenkai.services.bot.DialogFlowService
import ai.zenkai.zenkai.services.speech.SpeechService
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
import android.speech.tts.UtteranceProgressListener
import me.carleslc.kotlin.extensions.standard.letIfTrue
import org.jetbrains.anko.*
import kotlin.properties.Delegates.notNull

object AndroidSpeechService : SpeechService(), VoiceListener, AnkoLogger {
    
    private var UI: VoiceUI by notNull()
    private var context: Context by notNull()
    private var TTS: TextToSpeech by notNull()
    private val utteranceIds by lazy { IdGenerator() }
    private var listeningCallback: ListeningCallback by notNull()
    private val pendingMessagesQueue by lazy { mutableListOf<VoiceMessage>() }
    private val speakingMessages by lazy { mutableMapOf<String, VoiceMessage>() }
    
    private var started = false
    
    private const val NO_INPUT = "Speech recognition engine error: No speech input."
    private const val NO_RESULT = "Speech recognition engine error: No recognition result matched."
    
    override var speakerEnabled: Boolean = true
        set(value) {
            if (value && !field && pendingMessagesQueue.isNotEmpty()) {
                delaySpeakerEnabling()
            } else {
                field = value
                debug { "Speaker disabled" }
            }
        }
    
    fun attach(context: Context, ui: VoiceUI): AndroidSpeechService {
        this.context = context
        UI = ui
        start()
        return this
    }
    
    fun start() {
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
    
    fun stop() {
        TTS.stop()
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
    
    fun shutdown() {
        stop()
        TTS.shutdown()
        started = false
        debug { "TTS ($language) Shutdown" }
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
        UI.show(DialogFlowService.config as AndroidAIConfiguration, this)
    }
    
    override fun onError(error: AIError) {
        when {
            error.message == NO_INPUT -> onCancelled()
            error.message == NO_RESULT -> UI.context.toast(i18n[S.TRY_AGAIN])
            else -> listeningCallback.onError(ListeningException(error.toString()))
        }
    }
    
    override fun onCancelled() {
        listeningCallback.onCancel()
    }
    
    override fun onResult(result: AIResponse) {
        with (result.result) {
            debug { "Received response for '${result.result.resolvedQuery}' on action " +
                "${result.result.action} with status ${result.status.code}" }
            listeningCallback.onResult(TextMessage(resolvedQuery.capitalize()), DialogFlowService.getBotMessage(result))
        }
    }
    
    private fun Int.letTTSIfNoError(block: () -> Unit) {
        if (this != TextToSpeech.ERROR) {
            block()
        } else {
            UI.view.show(S.TTS_ERROR)
        }
    }
    
    private fun speak(message: VoiceMessage) {
        val id = utteranceIds.getNextString()
        speakingMessages[id] = message
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            TTS.speak(message.message, TextToSpeech.QUEUE_ADD, null, id)
        } else {
            TTS.speak(message.message, TextToSpeech.QUEUE_ADD, idMap(id))
        }
    }
    
    private fun delaySpeakerEnabling() {
        fun enableSpeaker() {
            speakerEnabled = true
            debug { "Speaker enabled" }
        }
        SpeakingListener.merge(pendingMessagesQueue.last(), object : SpeakingListener {
            override fun onSpeakCompleted() {
                enableSpeaker()
            }
            override fun onSpeakCancelled() {
                enableSpeaker()
            }
        })
    }
    
    private fun idMap(id: String): HashMap<String, String> {
        return HashMap<String, String>(1).apply { put(KEY_PARAM_UTTERANCE_ID, id) }
    }
    
}