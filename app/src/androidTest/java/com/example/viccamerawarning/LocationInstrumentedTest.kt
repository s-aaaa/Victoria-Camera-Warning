package com.example.viccamerawarning


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.viccamerawarning.data.repository.CameraRepository
import com.example.viccamerawarning.location.FakeLocationTracker
import com.example.viccamerawarning.tts.MockSpeechService
import com.example.viccamerawarning.viewmodel.CameraViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationInstrumentedTest {

    @Test
    fun testApproachingCameraTriggersDistance() = runBlocking {
        val tracker = FakeLocationTracker()
        val repo = CameraRepository()
        val speaker = MockSpeechService()
        val testScope = CoroutineScope(Dispatchers.Unconfined)
        val vm = CameraViewModel(repo, tracker, speaker, testScope)
        vm.ready.await()

        delay(1000)

        tracker.emitLocation(-37.8170, 144.9600) // ~600m
        delay(50)
        tracker.emitLocation(-37.8120, 144.9600) // ~300m
        delay(50)
        tracker.emitLocation(-37.8110, 144.9600) // ~200m
        delay(50)

        assertEquals("Speed camera ahead", speaker.messages[0])
        assertEquals("300 metres", speaker.messages[1])


    }
}
