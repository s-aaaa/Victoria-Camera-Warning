package com.example.viccamerawarning

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.viccamerawarning.data.remote.RetrofitInstance
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApiInstrumentedTest {

    @Test
    fun testApiReturnsCameraLocations() = runBlocking{
        val api = RetrofitInstance.api

        val result = api.getCameras()

        // Log the first camera
        Log.d("ApiInstrumentedTest", result[0].toString())

        assertTrue(result.isNotEmpty())
        assertNotNull(result[0].latitude)
        assertNotNull(result[0].longitude)
    }

}