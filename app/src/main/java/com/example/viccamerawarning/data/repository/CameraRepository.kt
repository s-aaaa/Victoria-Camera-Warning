package com.example.viccamerawarning.data.repository

import com.example.viccamerawarning.data.remote.RetrofitInstance

class CameraRepository {
    suspend fun getCameras() = RetrofitInstance.api.getCameras()
}