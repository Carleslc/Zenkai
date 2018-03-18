package ai.zenkai.zenkai.presentation.messages

import ai.zenkai.zenkai.data.Message
import ai.zenkai.zenkai.presentation.BasePresenter
import ai.zenkai.zenkai.repositories.MessagesRepository
import ai.zenkai.zenkai.services.speech.say

class MessagesPresenter(val view: MessagesView) : BasePresenter() {

    private val repository by MessagesRepository.lazyGet()
    
    fun load() {
        addAll()
    }
    
    fun onNewMessage(message: Message) {
        UI {
            view.add(message)
            val answer = repository.query(message)
            view.add(answer)
            answer.say()
        }
    }

    private fun addAll() {
        UI {
            try {
                view.loading = true
                val messages = repository.getHistory().messages.sortedByDescending { it.date }
                view.add(messages)
            } catch (e: Throwable) {
                view.showError(e)
            } finally {
                view.loading = false
            }
        }
    }
    
}