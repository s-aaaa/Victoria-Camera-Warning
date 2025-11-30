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
    private val repo: CameraRepository,
    private val tracker: LocationTracker,
    private val speaker: ISpeechService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : ViewModel() {

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

    private val alertState = mutableMapOf<String, Pair<Boolean, Boolean>>()
    val ready = CompletableDeferred<Unit>()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        Log.d("CameraVM", "Viewmodel Initializing")
        loadEverything()
    }

    fun loadEverything() = scope.launch{
        // Load cameras once
        val cams = repo.getCameras()
        _cameraList.value = cams
        ready.complete(Unit) // Signal ready
        Log.d("CameraVM", "Current camera list: ${_cameraList.value}")

        // Begin tracking user location
        tracker.getLocationUpdates().collect { loc ->
            if (!mockMode) {
                updateLocation(loc)
            }
            else{
                // Ignore real GPS while in mock mode
                Log.d("CameraVM", "Mock mode active, ignoring real location update")
            }
        }
    }

    private fun updateLocation(loc: Location?){
        if(loc == null) return

        _userLocation.value = loc
        updateNearestCamera(loc)
        checkCameraDistances(loc)
        Log.d("CameraVM", "Current nearest camera: $_nearestCamera.value")
        Log.d("CameraVM", "Current Location: $loc")
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


    private fun checkCameraDistances(user: Location){
        for (cam in _cameraList.value) {
            val distance = FloatArray(1)
            Location.distanceBetween(
                user.latitude, user.longitude,
                cam.latitude, cam.longitude,
                distance
            )

            val d = distance[0]
            val state = alertState.getOrPut(cam.cameraId) { false to false }

            if (d < 500 && !state.first) {
                speaker.speak("Speed camera ahead")
                alertState[cam.cameraId] = true to state.second
            }

            if (d < 300 && !state.second) {
                speaker.speak("300 metres")
                alertState[cam.cameraId] = state.first to true
            }

            if (d > 1000) {
                // reset once user is far away
                alertState[cam.cameraId] = false to false
            }
        }
    }

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
        val mockedLoc = mockProvider.getLocation()
        updateLocation(mockedLoc)
    }
    fun resetMockPosition(){
        mockProvider.resetLocation()
        if(mockMode){
            updateLocation(mockProvider.getLocation())
        }
    }

}