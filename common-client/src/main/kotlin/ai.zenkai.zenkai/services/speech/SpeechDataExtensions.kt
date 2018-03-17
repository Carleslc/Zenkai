package ai.zenkai.zenkai.services.speech

import ai.zenkai.zenkai.data.BotMessage
import ai.zenkai.zenkai.data.VoiceMessage
import ai.zenkai.zenkai.services.ServicesProvider

fun VoiceMessage.say() = ServicesProvider.getSpeechService().say(this)

fun BotMessage.say() = speech.say()