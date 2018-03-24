package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.common.doAsync
import ai.zenkai.zenkai.common.launchUI
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.presentation.BasePresenter
import ai.zenkai.zenkai.repositories.MessagesRepository
import ai.zenkai.zenkai.repositories.RepositoriesProvider
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.speech.SpeechService.ListeningCallback
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Merge.merge
import klogging.KLoggerHolder
import klogging.WithLogging
import kotlinx.coroutines.experimental.CoroutineScope

class MessagesPresenter(val view: MessagesView) : BasePresenter(), WithLogging by KLoggerHolder() {

    private val repository by MessagesRepository.lazyGet()
    
    private fun addSayMic(message: BotMessage) {
        merge(message.speech, object : SpeakingListener {
            override fun onSpeakStarted() {
                add(message)
            }
            override fun onSpeakCompleted() {
                onMicrophone(true)
            }
        })
        message.say()
    }
    
    private fun addSay(message: BotMessage) {
        merge(message.speech, object : SpeakingListener {
            private var spoken = false
            
            override fun onSpeakStarted() {
                add(message)
                spoken = true
            }
            
            override fun onSpeakCancelled() {
                if (!spoken) add(message)
            }
        })
        message.say()
    }
    
    private fun add(message: Message) {
        launchUI {
            view.add(message)
        }
        doAsync {
            repository.add(message)
        }
    }
    
    fun onInit() = launchUI {
        ServicesProvider.getSpeechService().speakerEnabled = false
        val firstTime = RepositoriesProvider.getSettingsRepository().isFirstTime()
        val messages = repository.getHistory().sortedBy { it.date }
        logger.debug("Add history (${messages.size} messages)")
        view.addAll(messages)
        ServicesProvider.getSpeechService().speakerEnabled = firstTime
        repository.greetings().forEach(::addSay)
        ServicesProvider.getSpeechService().speakerEnabled = true
        if (firstTime) {
            RepositoriesProvider.getSettingsRepository().setFirstTime()
        } else {
            onMicrophone(true)
        }
    }
    
    fun onNewMessage(request: Message) {
        if (!request.isEmpty()) {
            add(request)
            loading {
                val response = ServicesProvider.getBotService().ask(request)
                addSay(response)
            }
        }
    }
    
    fun onMicrophone(request: Boolean = false) = UI {
        if (!view.loading && view.hasMicrophonePermission(request)) {
            view.loading = true
            ServicesProvider.getSpeechService().listen(object : ListeningCallback {
                override fun onResult(request: Message, response: BotMessage) {
                    add(request)
                    addSayMic(response)
                    view.loading = false
                }
        
                override fun onError(error: ListeningException) {
                    view.showError(error)
                    view.loading = false
                }
        
                override fun onCancel() {
                    logger.debug { "[${this::class.simpleName}] Microphone Cancelled" }
                    view.loading = false
                }
            })
        }
    }
    
    fun onMicrophonePermission(allowed: Boolean) {
        logger.debug { "[${this::class.simpleName}] Microphone Permission, allowed = $allowed" }
        ServicesProvider.getSpeechService().microphoneEnabled = allowed
        if (allowed) {
            onMicrophone(true)
        } else {
            view.show(S.MICROPHONE_DISABLED)
        }
    }
    
    private fun loading(block: suspend CoroutineScope.() -> Unit) = UI {
        try {
            view.loading = true
            block()
        } catch (e: Throwable) {
            view.showError(e)
        } finally {
            view.loading = false
        }
    }
    
}