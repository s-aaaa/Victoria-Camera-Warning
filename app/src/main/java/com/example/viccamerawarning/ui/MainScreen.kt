package com.example.viccamerawarning.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.viccamerawarning.ui.components.CameraCard
import com.example.viccamerawarning.ui.components.CameraMap
import com.example.viccamerawarning.viewmodel.CameraViewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun MainScreen(viewModel: CameraViewModel = hiltViewModel()) {
    val context = LocalContext.current

    LaunchedEffect(Unit){
        viewModel.events.collect{ message ->
            run {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val nearestCamera by viewModel.nearestCamera.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val cameraList by viewModel.cameraList.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // TOP CARD: Nearest camera
        CameraCard(
            nearestCamera = nearestCamera,
            userLocation = userLocation,
            cameraList = cameraList
        )

        Spacer(Modifier.height(12.dp))

        // MAP
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text("Map Stand-in")
//            CameraMap(
//                userLocation = userLocation,
//                cameras = cameraList,
//                nearestCamera = nearestCamera
//            )
        }

        Spacer(Modifier.height(12.dp))


        // Toggle mocked GPS position mode
        fun onToggleMockMode(){
            viewModel.toggleMockMode()
        }

        // Reset mocked GPS position to default
        fun onResetPosition(){
            viewModel.resetMockPosition()
        }

        // Simulate moving closer to nearest camera with mocked GPS location
        fun onClickSimulateMove(){
            if(nearestCamera != null){
                viewModel.simulateMovement(LatLng(nearestCamera!!.latitude, nearestCamera!!.longitude))
            }
        }


        // Controls
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { onToggleMockMode() }) {
                Text("Mock GPS")
            }
            Button(onClick = {onResetPosition()}){
                Text("Reset")
            }
            Button(onClick = { onClickSimulateMove() }) {
                Text("Simulate Move")
            }
        }
    }
}
