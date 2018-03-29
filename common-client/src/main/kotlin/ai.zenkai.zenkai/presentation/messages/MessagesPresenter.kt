package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.Message
import ai.zenkai.zenkai.model.VoiceMessage
import ai.zenkai.zenkai.presentation.BasePresenter
import ai.zenkai.zenkai.repositories.MessagesRepository
import ai.zenkai.zenkai.repositories.RepositoriesProvider
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.speech.SpeechService.ListeningCallback
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Factory.onCompleted
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Factory.onFinish
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Factory.performOnce
import klogging.KLoggerHolder
import klogging.WithLogging

class MessagesPresenter(val view: MessagesView) : BasePresenter(), WithLogging by KLoggerHolder() {
    
    private val repository by MessagesRepository.lazyGet()
    
    private fun lastInteraction(message: VoiceMessage) {
        message.speakingListener = onFinish {
            logger.info { "[${this::class.simpleName}] Last interaction on finish ($message)" }
            UI { view.loading = false }
        }
    }
    
    private fun addSayAll(messages: List<BotMessage>) {
        logger.info { "[${this::class.simpleName}] Add Say All" }
        val iterator = messages.iterator()
        fun sayNext() {
            if (iterator.hasNext()) {
                iterator.next().say()
            }
        }
        messages.forEach {
            logger.info { "[${this::class.simpleName}] Set listener for $it" }
            it.speech.speakingListener = performOnce(
                onStart = {
                    logger.info { "[${this::class.simpleName}] On start ($it)" }
                    add(it)
                },
                onFinish = {
                    logger.info { "[${this::class.simpleName}] On finish ($it)" }
                    sayNext()
                }
            )
        }
        sayNext()
    }
    
    private fun addSay(messages: List<BotMessage>, lastListener: (VoiceMessage) -> Unit = {}) {
        logger.info { "[${this::class.simpleName}] Add Say" }
        if (messages.isEmpty()) {
            logger.info { "[${this::class.simpleName}] Messages empty" }
            view.loading = false
            return
        }
        val last = messages.last().speech
        lastInteraction(last)
        lastListener(last)
        addSayAll(messages)
    }
    
    private fun addSayMic(messages: List<BotMessage>) {
        logger.info { "[${this::class.simpleName}] Add Say Mic" }
        addSay(messages) {
            it.speakingListener = onCompleted {
                logger.info { "[${this::class.simpleName}] Mic" }
                onMicrophone(true)
            }
        }
    }
    
    private fun add(message: Message) {
        if (message.message.isNotBlank()) {
            UI { view.add(message) }
        }
        if (!message.isEmpty()) {
            async { repository.add(message) }
        }
    }
    
    fun onInit() = UI {
        view.loading = true
        ServicesProvider.getSpeechService().speakerEnabled = false
        val firstTime = RepositoriesProvider.getSettingsRepository().isFirstTime()
        view.addAll(repository.getHistory().sortedBy { it.date })
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
                    logger.info { "[${MessagesPresenter@ this::class.simpleName}] Microphone Cancelled" }
                    view.loading = false
                }
            })
        }
    }
    
    fun onMessageInteraction(message: Message) {
        if (message is BotMessage) {
            if (!view.openUrl(message.text.message)) {
                view.share("", message.share())
            }
        }
    }
    
    fun onMicrophonePermission(allowed: Boolean) {
        logger.info { "[${MessagesPresenter@ this::class.simpleName}] Microphone Permission, allowed = $allowed" }
        ServicesProvider.getSpeechService().microphoneEnabled = allowed
        if (allowed) {
            onMicrophone(true)
        } else {
            view.loading = false
            view.show(S.MICROPHONE_DISABLED)
        }
    }
    
}