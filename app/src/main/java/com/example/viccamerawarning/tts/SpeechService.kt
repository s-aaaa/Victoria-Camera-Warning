package com.example.viccamerawarning.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

interface ISpeechService {
    fun speak(message: String)
}

class SpeechService (context: Context) : ISpeechService{

    private val tts = TextToSpeech(context){}

    init{
        tts.language = Locale.UK
    }

    override fun speak(message: String){
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown(){
        tts.shutdown()
    }
}