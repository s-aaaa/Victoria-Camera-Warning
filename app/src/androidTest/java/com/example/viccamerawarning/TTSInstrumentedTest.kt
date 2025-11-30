package com.example.viccamerawarning

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TTSInstrumentedTest {

    @Test
    fun testTTS() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val initLatch = CountDownLatch(1)
        val speakLatch = CountDownLatch(1)

        var tts: TextToSpeech? = null

        // Create TTS on main thread
        Handler(Looper.getMainLooper()).post {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    initLatch.countDown()
                }
            }
        }

        assertTrue("TTS failed to init", initLatch.await(5, TimeUnit.SECONDS))

        // Set listener for utterance completion
        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "testId") {
                    speakLatch.countDown()
                }
            }

            override fun onError(utteranceId: String?) {
                speakLatch.countDown()
            }
        })

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "testId")
        }

        tts!!.speak("Test message", TextToSpeech.QUEUE_FLUSH, params, "testId")

        assertTrue("TTS did not speak in time", speakLatch.await(10, TimeUnit.SECONDS))

        tts!!.shutdown()
    }
}

