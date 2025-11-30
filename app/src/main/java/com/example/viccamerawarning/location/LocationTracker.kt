package com.example.viccamerawarning.location

import kotlinx.coroutines.flow.Flow
import android.location.Location

interface LocationTracker {
    fun getLocationUpdates(): Flow<Location>
    suspend fun getLastLocation(): Location?
}