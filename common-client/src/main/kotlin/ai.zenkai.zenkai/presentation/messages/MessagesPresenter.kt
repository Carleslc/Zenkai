package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.presentation.BasePresenter
import ai.zenkai.zenkai.repositories.MessagesRepository
import ai.zenkai.zenkai.repositories.RepositoriesProvider
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.speech.SpeechService.ListeningCallback
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Factory.merge
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Factory.performIfNoError
import klogging.KLoggerHolder
import klogging.WithLogging

class MessagesPresenter(val view: MessagesView) : BasePresenter(), WithLogging by KLoggerHolder() {

    private val repository by MessagesRepository.lazyGet()
    
    private fun lastInteraction(message: VoiceMessage) {
        merge(message, object : SpeakingListener {
            override fun onSpeakCompleted() {
                UI { view.loading = false }
            }
            override fun onSpeakCancelled() {
                UI { view.loading = false }
            }
        })
    }
    
    private fun addIfNoError(messages: List<BotMessage>) {
        messages.forEach { performIfNoError(it.speech) { add(it) } }
    }
    
    private fun addSayMic(messages: List<BotMessage>) {
        if (messages.isEmpty()) {
            view.loading = false
            return
        }
        addIfNoError(messages)
        val last = messages.last()
        lastInteraction(last.speech)
        merge(last.speech, object : SpeakingListener {
            override fun onSpeakCompleted() {
                onMicrophone(true)
            }
        })
        messages.forEach(BotMessage::say)
    }
    
    private fun addSay(messages: List<BotMessage>) {
        if (messages.isEmpty()) {
            view.loading = false
            return
        }
        addIfNoError(messages)
        lastInteraction(messages.last().speech)
        messages.forEach(BotMessage::say)
    }
    
    private fun add(message: Message) {
        UI { view.add(message) }
        async { repository.add(message) }
    }
    
    fun onInit() = UI {
        view.loading = true
        ServicesProvider.getSpeechService().speakerEnabled = false
        val firstTime = RepositoriesProvider.getSettingsRepository().isFirstTime()
        val messages = repository.getHistory().sortedBy { it.date }
        view.addAll(messages)
        ServicesProvider.getSpeechService().speakerEnabled = firstTime
        addSay(repository.greetings())
        ServicesProvider.getSpeechService().speakerEnabled = true
        if (firstTime) {
            RepositoriesProvider.getSettingsRepository().setFirstTime()
        } else {
            view.loading = false
            onMicrophone(true)
        }
    }
    
    fun onNewMessage(request: Message) {
        if (!request.isEmpty()) {
            view.loading = true
            add(request)
            async {
                val responses = ServicesProvider.getBotService().ask(request)
                addSay(responses)
            }
        }
    }
    
    fun onMicrophone(request: Boolean = false) = UI {
        if (!view.loading && view.hasMicrophonePermission(request)) {
            view.loading = true
            ServicesProvider.getSpeechService().listen(object : ListeningCallback {
                override fun onRequest(request: Message) {
                    add(request)
                }
                override fun onResults(responses: List<BotMessage>) {
                    addSayMic(responses)
                }
                override fun onError(error: ListeningException) {
                    view.showError(error)
                    view.loading = false
                }
                override fun onCancel() {
                    logger.info { "[${MessagesPresenter@this::class.simpleName}] Microphone Cancelled" }
                    view.loading = false
                }
            })
        }
    }
    
    fun onMicrophonePermission(allowed: Boolean) {
        logger.info { "[${MessagesPresenter@this::class.simpleName}] Microphone Permission, allowed = $allowed" }
        ServicesProvider.getSpeechService().microphoneEnabled = allowed
        if (allowed) {
            onMicrophone(true)
        } else {
            view.loading = false
            view.show(S.MICROPHONE_DISABLED)
        }
    }
    
}