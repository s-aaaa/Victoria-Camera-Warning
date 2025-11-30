package com.example.viccamerawarning.tts

import kotlinx.coroutines.CompletableDeferred

class MockSpeechService : ISpeechService{
    var messages : MutableList<String> = mutableListOf()
    val spoken = CompletableDeferred<Unit>()

    override fun speak(message: String){
        messages.add(message)
        if (!spoken.isCompleted) spoken.complete(Unit)
    }
}