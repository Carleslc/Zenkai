package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.presentation.BasePresenter
import ai.zenkai.zenkai.repositories.MessagesRepository
import ai.zenkai.zenkai.services.speech.say
import kotlinx.coroutines.experimental.CoroutineScope

class MessagesPresenter(val view: MessagesView) : BasePresenter() {

    private val repository by MessagesRepository.lazyGet()
    
    override fun onCreate() {
        addAll()
    }
    
    fun onNewMessage(message: Message) = loading {
        view.add(message)
        val answer = repository.query(message)
        view.add(answer)
        answer.say()
    }

    private fun addAll() = loading {
        val messages = repository.getHistory().messages.sortedBy { it.date }
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