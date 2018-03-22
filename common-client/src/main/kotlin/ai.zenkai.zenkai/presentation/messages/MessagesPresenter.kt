package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.common.launchUI
import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.exceptions.ListeningException
import ai.zenkai.zenkai.presentation.BasePresenter
import ai.zenkai.zenkai.repositories.MessagesRepository
import ai.zenkai.zenkai.services.ServicesProvider
import ai.zenkai.zenkai.services.speech.SpeechService.ListeningCallback
import ai.zenkai.zenkai.i18n.S
import ai.zenkai.zenkai.repositories.RepositoriesProvider
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener
import ai.zenkai.zenkai.services.speech.SpeechService.SpeakingListener.Merge.merge
import klogging.KLoggerHolder
import klogging.WithLogging
import kotlinx.coroutines.experimental.CoroutineScope

class MessagesPresenter(val view: MessagesView) : BasePresenter(), WithLogging by KLoggerHolder() {

    private val repository by MessagesRepository.lazyGet()
    
    override fun onCreate() {
        addHistory()
    }
    
    private fun addSayMic(message: BotMessage) {
        merge(message.speech, object : SpeakingListener {
            override fun onSpeakStarted() {
                view.add(message)
            }
            override fun onSpeakCompleted() {
                onMicrophone(true)
            }
        })
        message.say()
    }
    
    private fun addSay(message: BotMessage) {
        merge(message.speech, object : SpeakingListener {
            override fun onSpeakStarted() {
                launchUI {
                    view.add(message)
                }
            }
        })
        message.say()
    }
    
    fun greetings() {
        val firstTime = RepositoriesProvider.getSettingsRepository().isFirstTime()
        ServicesProvider.getSpeechService().speakerEnabled = firstTime
        repository.getGreetings().forEach(::addSay)
        ServicesProvider.getSpeechService().speakerEnabled = true
        if (firstTime) {
            RepositoriesProvider.getSettingsRepository().setFirstTime()
        } else {
            onMicrophone(true)
        }
    }
    
    fun onNewMessage(request: Message) = loading {
        if (!request.isEmpty()) {
            view.add(request)
            val response = repository.ask(request)
            addSay(response)
        }
    }
    
    fun onMicrophone(request: Boolean = false) = UI {
        if (!view.loading && view.hasMicrophonePermission(request)) {
            view.loading = true
            ServicesProvider.getSpeechService().listen(object : ListeningCallback {
                override fun onResult(request: Message, response: BotMessage) {
                    view.add(request)
                    addSayMic(response)
                    view.loading = false
                }
        
                override fun onError(error: ListeningException) {
                    view.showError(error)
                    view.loading = false
                }
        
                override fun onCancel() {
                    view.loading = false
                }
            })
        }
    }
    
    fun onMicrophonePermission(allowed: Boolean) {
        ServicesProvider.getSpeechService().microphoneEnabled = allowed
        if (allowed) {
            onMicrophone(true)
        } else {
            view.show(S.MICROPHONE_DISABLED)
        }
    }

    private fun addHistory() = loading {
        val messages = repository.getHistory().sortedBy { it.date }
        view.addAll(messages)
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