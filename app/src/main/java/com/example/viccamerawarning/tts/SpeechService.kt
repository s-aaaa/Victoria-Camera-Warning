package com.example.viccamerawarning.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.time.Instant
import java.util.Locale

interface ISpeechService {
    fun speak(message: String)
}

data class SpeechHistory(
    val timestamp: Instant,
    val message: String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpeechHistory) return false
        return message == other.message
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }

}

class SpeechService (context: Context) : ISpeechService{

    var lastMessageSpoken : SpeechHistory? = null

    private val tts = TextToSpeech(context){}

    init{
        tts.language = Locale.UK
    }

    override fun speak(message: String){
        val event = SpeechHistory(Instant.now(), message)
        if(shouldPlay(event)){
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
        }
        else{
            Log.d("viccameras", "Duplicate message sent to TTS service: {message}")
        }
    }

    fun shutdown(){
        tts.shutdown()
    }

    fun shouldPlay(event: SpeechHistory): Boolean{
        val prev = lastMessageSpoken ?: return true.also {lastMessageSpoken = event}

        val duplicate = prev.message == event.message &&
                prev.timestamp.plusSeconds(5).isAfter(event.timestamp)

        if(duplicate){
            return false
        }
        else{
            lastMessageSpoken = event
            return true
        }
    }
}