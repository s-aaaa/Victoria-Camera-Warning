package com.example.viccamerawarning.data.remote

import com.example.viccamerawarning.data.model.CameraLocation
import retrofit2.http.GET


interface CameraApi {

    @GET("/api/cameras")
    suspend fun getCameras(): List<CameraLocation>
}