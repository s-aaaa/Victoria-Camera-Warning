package com.example.viccamerawarning.data.repository

import com.example.viccamerawarning.data.remote.RetrofitInstance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepository @Inject constructor() {
    suspend fun getCameras() = RetrofitInstance.api.getCameras()
}