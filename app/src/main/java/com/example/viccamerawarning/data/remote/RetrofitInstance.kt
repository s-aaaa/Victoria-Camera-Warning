package com.example.viccamerawarning.data.remote
import com.example.viccamerawarning.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val baseUrl = BuildConfig.BASE_URL

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: CameraApi = retrofit.create(CameraApi::class.java)

}