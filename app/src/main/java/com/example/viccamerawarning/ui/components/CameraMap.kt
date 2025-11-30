package com.example.viccamerawarning.ui.components

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.*
import com.example.viccamerawarning.data.model.CameraLocation
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CameraMap(
    userLocation: Location?,
    cameras: List<CameraLocation>,
    nearestCamera: CameraLocation?
) {
    val cameraPosition = userLocation?.let {
        LatLng(it.latitude, it.longitude)
    } ?: LatLng(-37.8136, 144.9631) // fallback Melbourne CBD

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(cameraPosition, 14f)
        }
    ) {

        // User location marker
        userLocation?.let {
            Marker(
                state = MarkerState(position = cameraPosition),
                title = "You"
            )
        }

        // Camera markers
        cameras.forEach { cam ->
            Marker(
                state = MarkerState(LatLng(cam.latitude, cam.longitude)),
                title = cam.cameraId
            )
        }

        // Highlight nearest camera
        nearestCamera?.let { cam ->
            Marker(
                state = MarkerState(LatLng(cam.latitude, cam.longitude)),
                title = "Nearest",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            )
        }
    }
}
