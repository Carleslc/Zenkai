package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.common.delay
import ai.zenkai.zenkai.common.doAsync
import ai.zenkai.zenkai.model.Token
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.i18n.i18n
import ai.zenkai.zenkai.model.BotMessage
import ai.zenkai.zenkai.model.BotResult
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
    
    private var tokenLoginRequest: Token? = null
    
    private var loading
        get() = view.loading
        set(value) = UI { view.loading = value }
    
    private fun lastInteraction(message: VoiceMessage) {
        message.speakingListener = onFinish {
            logger.info { "[${this::class.simpleName}] Last interaction on finish ($message)" }
            loading = false
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
                    UI { add(it) }
                },
                onFinish = {
                    logger.info { "[${this::class.simpleName}] On finish ($it)" }
                    sayNext()
                }
            )
        }
        sayNext()
    }
    
    private fun addSay(result: BotResult, lastListener: (VoiceMessage) -> Unit = {}) = with(result) {
        if (!addCommon(result)) return
        logger.info { "[${this::class.simpleName}] Add Say" }
        val last = messages.last().speech
        lastInteraction(last)
        lastListener(last)
        addSayAll(messages)
    }
    
    private fun addSayMic(result: BotResult): Unit = addSay(result) {
        logger.info { "[${this::class.simpleName}] Setting Mic Last Listener" }
        it.speakingListener = onCompleted {
            logger.info { "[${this::class.simpleName}] Mic" }
            implicitMicrophone()
        }
    }
    
    private fun addText(result: BotResult) = with(result) {
        if (!addCommon(result)) return
        logger.info { "[${this::class.simpleName}] Add Text" }
        UI {
            messages.forEach { add(it) }
            loading = false
        }
    }
    
    private fun addCommon(result: BotResult): Boolean = with(result) {
        onResult()
        if (messages.isEmpty()) {
            loading = false
            return false
        }
        return true
    }
    
    private fun add(message: Message) {
        if (message.message.isNotBlank()) {
            view.add(message)
        }
        if (!message.isEmpty()) {
            async { repository.add(message) }
        }
    }
    
    fun onInit() = UI {
        logger.info { "Presenter Init" }
        loading = true
        ServicesProvider.getSpeechService().speakerEnabled = false
        val firstTime = RepositoriesProvider.getSettingsRepository().isFirstTime()
        view.addAll(repository.getHistory().sortedBy { it.date })
        ServicesProvider.getSpeechService().speakerEnabled = firstTime
        val greetings = repository.greetings()
        addSay(greetings)
        ServicesProvider.getSpeechService().speakerEnabled = true
        if (firstTime) {
            if (!greetings.isError() || greetings.isLoginError()) {
                RepositoriesProvider.getSettingsRepository().setFirstTime()
            }
        } else {
            loading = false
            implicitMicrophone()
        }
        logger.info { "Presenter End Init" }
    }
    
    private fun BotResult.onResult() {
        logger.info { "BotResult Success?" }
        if (isLoginError()) {
            tokenLoginRequest = login!! // wait for a token
        } else if (isError()) {
            logger.error("Error ${error!!.message} with code ${error.status}")
            UI { view.showError(i18n[S.INTERNAL_ERROR]) }
            return
        } else if (tokens != null && isWaitingForAToken()) {
            val validToken = tokens.find { it.type == tokenLoginRequest!!.type }
            if (validToken != null) {
                tokenLoginRequest = null
            }
        }
        logger.info { "SUCCESS" }
    }
    
    private fun isWaitingForAToken() : Boolean {
        if (tokenLoginRequest != null) {
            logger.info("Waiting for a token")
            return true
        }
        return false
    }
    
    private fun canSendMessage(message: Message): Boolean {
        if (!message.isEmpty()) {
            if (!RepositoriesProvider.getSettingsRepository().isNetworkAvailable()) {
                view.show(S.NO_NETWORK)
                return false
            }
            return true
        }
        return false
    }
    
    private fun preprocessMessage(request: Message): String? {
        UI { add(request) }
        if (isWaitingForAToken()) {
            val token = tokenLoginRequest!!
            if (request.message.matches(token.regex.toRegex())) {
                token.token = request.message
                RepositoriesProvider.getSettingsRepository().setToken(token)
                return token.loginEvent
            }
        }
        return null
    }
    
    fun onNewMessage(request: Message) {
        if (canSendMessage(request)) {
            loading = true
            view.clearInput()
            val event = preprocessMessage(request)
            async {
                val result = if (event != null) {
                    ServicesProvider.getBotService().sendEvent(event)
                } else ServicesProvider.getBotService().ask(request)
                addText(result)
            }
        } else loading = false
    }
    
    private fun implicitMicrophone() {
        if (!isWaitingForAToken()) onMicrophone()
    }
    
    fun onMicrophone(request: Boolean = false) = UI {
        logger.info { "onMicrophone" }
        if (!loading && view.hasMicrophonePermission(request)) {
            loading = true
            ServicesProvider.getSpeechService().listen(object : ListeningCallback {
                override fun onRequest(request: Message): String? {
                    return preprocessMessage(request)
                }
                
                override fun onResults(responses: BotResult) {
                    logger.debug { "onResults" }
                    addSayMic(responses)
                }
                
                override fun onError(error: ListeningException) {
                    view.showError(error)
                    loading = false
                }
                
                override fun onCancel() {
                    logger.info { "[${MessagesPresenter@ this::class.simpleName}] Microphone Cancelled" }
                    loading = false
                }
            })
        }
    }
    
    fun pause() {
        logger.info { "Pause Presenter" }
        ServicesProvider.getSpeechService().pause()
    }
    
    fun resume() {
        logger.info { "Resume Presenter" }
        ServicesProvider.getSpeechService().resume()
        implicitMicrophone()
    }
    
    fun stop() {
        logger.info { "Stop Presenter" }
        ServicesProvider.getSpeechService().stop()
    }
    
    fun onMessageInteraction(message: Message) {
        if (message is BotMessage) {
            ServicesProvider.getSpeechService().microphoneEnabled = false
            if (!view.openUrl(message.text.message)) {
                view.share("", message.share())
            }
            doAsync { // keep disabled while opening url or sharing (paused app UI)
                delay(1000)
                ServicesProvider.getSpeechService().microphoneEnabled = true
            }
        } else {
            view.copyToClipboard("Zenkai Chat Message", message.message)
        }
    }
    
    fun onMicrophonePermission(allowed: Boolean) {
        logger.info { "[${MessagesPresenter@ this::class.simpleName}] Microphone Permission, allowed = $allowed" }
        ServicesProvider.getSpeechService().microphoneEnabled = allowed
        if (allowed) {
            onMicrophone()
        } else {
            loading = false
            view.show(S.MICROPHONE_DISABLED)
        }
    }
    
}