package com.example.viccamerawarning.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viccamerawarning.data.model.CameraLocation
import com.example.viccamerawarning.data.repository.CameraRepository
import com.example.viccamerawarning.location.LocationTracker
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import android.location.Location
import android.util.Log
import com.example.viccamerawarning.CameraAlertManager
import com.example.viccamerawarning.tts.ISpeechService
import com.example.viccamerawarning.utils.MockLocationProvider
import com.example.viccamerawarning.utils.distanceMeters
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val tracker: LocationTracker,
    private val repo: CameraRepository,
) : ViewModel() {

    private var alertManager: CameraAlertManager? = null

    private var mockMode = false
    private val mockProvider =  MockLocationProvider()

    // Camera list
    private val _cameraList = MutableStateFlow<List<CameraLocation>>(emptyList())
    val cameraList: StateFlow<List<CameraLocation>> = _cameraList

    // Current user location
    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation : StateFlow<Location?> = _userLocation

    // Nearest Camera
    private val _nearestCamera = MutableStateFlow<CameraLocation?>(null)
    val nearestCamera: StateFlow<CameraLocation?> = _nearestCamera

    val ready = CompletableDeferred<Unit>()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Keep track of latest alert message for the UI
    private val _latestAlert = MutableStateFlow<String?>(null)
    val latestAlert: StateFlow<String?> = _latestAlert


    init {
        Log.d("CameraVM", "Viewmodel Initializing")
        viewModelScope.launch{
            // Load cameras once
            val cams = repo.getCameras()
            alertManager = CameraAlertManager(cams)
            _cameraList.value = cams
            ready.complete(Unit) // Signal ready
            Log.d("CameraVM", "Current camera list: ${_cameraList.value}")

            // Start tracking location
            launch {
                tracker.getLocationUpdates().collect { loc ->
                    if (!mockMode) {
                        updateLocation(loc)
                    } else {
                        Log.d("CameraVM", "Mock mode active, ignoring real GPS")
                    }
                }
            }

            // Listen for alert events from alert manager
            launch {
                alertManager?.events?.collect { event ->
                    _latestAlert.value = event
                    _events.send(event)  // UI-only alert
                }
            }

        }
    }

    private fun updateLocation(loc: Location?){
        if(loc == null) return

        _userLocation.value = loc
        updateNearestCamera(loc)
        alertManager?.updateLocation(loc)
//        Log.d("CameraVM", "Current nearest camera: $_nearestCamera.value")
//        Log.d("CameraVM", "Current Location: $loc")
    }

    fun getLastRealLocation(){
        viewModelScope.launch{
            val loc = tracker.getLastLocation() // suspend call is now OK
            _userLocation.value = loc
            loc?.let { updateNearestCamera(it) }
        }
    }

    private fun updateNearestCamera(location: Location) {
        val cams = _cameraList.value
        if (cams.isEmpty()) return

        val nearest = cams.minByOrNull { cam ->
            distanceMeters(location.latitude, location.longitude,
                cam.latitude, cam.longitude
            )
        }
        _nearestCamera.value = nearest
    }

    // ------------ Mocking tools ------------
    fun toggleMockMode(): Boolean {
        mockMode = !mockMode

        viewModelScope.launch{
            _events.send("GPS Mocking: $mockMode")
        }

        if(mockMode){
            // Set location to mock location provider location
            updateLocation(mockProvider.getLocation())
        }
        return mockMode
    }

    fun simulateMovement(targetCameraLatLng: LatLng) {
        if(!mockMode){
            return
        }

        mockProvider.moveCloserTo(targetCameraLatLng)
        updateLocation(mockProvider.getLocation())
    }
    fun resetMockPosition(){
        mockProvider.resetLocation()
        if(mockMode){
            updateLocation(mockProvider.getLocation())
        }
        else{
            getLastRealLocation()
        }
    }

}