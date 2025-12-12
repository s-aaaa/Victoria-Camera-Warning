package com.example.viccamerawarning


import com.example.viccamerawarning.tts.ISpeechService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import android.location.Location
import com.example.viccamerawarning.data.model.CameraLocation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

sealed class CameraAlertState {
    object Idle : CameraAlertState()                    // >500 m, no alerts fired
    object Warned500 : CameraAlertState()               // <500 m alert fired
    object Warned300 : CameraAlertState()               // <300 m alert fired
}


class CameraAlertManager(
    private var cameras: List<CameraLocation>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val alertState = mutableMapOf<String, CameraAlertState>()
    private val _events = MutableSharedFlow<String>(replay=0)
    val events: SharedFlow<String> = _events

    var speaker: ISpeechService? = null

    fun updateLocation(user: Location) {
        for (cam in cameras) {
            val d = getDistance(user, cam)
            val state = alertState.getOrPut(cam.cameraId) { CameraAlertState.Idle }

            val newState = when (state) {

                // Idle: no alerts yet
                CameraAlertState.Idle -> when {
                    d < 300 -> {
                        emitMessage("Speed camera near - 300 metres")
                        CameraAlertState.Warned300
                    }
                    d < 500 -> {

                        emitMessage("Speed camera ahead")
                        CameraAlertState.Warned500
                    }
                    else -> CameraAlertState.Idle
                }

                // Already gave the 500 m warning
                CameraAlertState.Warned500 -> when {
                    d < 300 -> {
                        emitMessage("Speed camera near - 300 metres")
                        CameraAlertState.Warned300
                    }
                    d >= 505 -> CameraAlertState.Idle   // moved back out — reset
                    else -> CameraAlertState.Warned500
                }

                // Already gave the 300 m warning
                CameraAlertState.Warned300 -> when {
                    d >= 305 -> CameraAlertState.Warned500   // fall back to 500 m state
                    else -> CameraAlertState.Warned300
                }
            }

            // Full reset if you're far away
            alertState[cam.cameraId] = if (d > 1000) CameraAlertState.Idle else newState
        }

    }

    private fun emitMessage(message: String) {
        // Launch a coroutine to handle emitting and TTS
        scope.launch {
            speaker?.speak(message)
            _events.emit(message)
        }
    }


    private fun getDistance(user: Location, cam: CameraLocation): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            user.latitude, user.longitude,
            cam.latitude, cam.longitude,
            results
        )
        return results[0]
    }

    fun updateCameras(cameras : List<CameraLocation>){
        this.cameras = cameras
    }
}