package com.example.viccamerawarning.ui.components

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viccamerawarning.data.model.CameraLocation
import com.example.viccamerawarning.utils.distanceMeters

@Composable
fun CameraCard(nearestCamera: CameraLocation?, userLocation: Location?, cameraList: List<CameraLocation>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Log.d("CameraCard", "Current Cameras: $cameraList")
            Log.d("CameraCard", "Nearest Camera: $nearestCamera")
            Log.d("CameraCard", "Current Location: $userLocation")
            if (nearestCamera == null || userLocation == null || cameraList == emptyList<CameraLocation>()) {
                Text("No camera detected")
                return@Column
            }

            // Compute distance
            val distance = distanceMeters(
                userLocation.latitude,
                userLocation.longitude,
                nearestCamera.latitude,
                nearestCamera.longitude
            ).toInt()

            Text("Nearest camera:", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Text("${nearestCamera.cameraId} — ${nearestCamera.street}, ${nearestCamera.postCode}")
            Text("Distance: $distance m")
            Text("Current Loc: ${userLocation.latitude}, ${userLocation.longitude}")
            Text("Number of known cameras: ${cameraList.size}")

            if (distance < 500) Text("Status: ALARM READY")
        }
    }
}
