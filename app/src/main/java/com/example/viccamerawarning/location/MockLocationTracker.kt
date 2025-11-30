package com.example.viccamerawarning.location

import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

class FakeLocationTracker : LocationTracker {
    private val _locations = MutableSharedFlow<Location>()

    override fun getLocationUpdates(): Flow<Location> = _locations

    fun emitLocation(lat: Double, lng: Double) {
        val loc = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = lat
            longitude = lng
        }
        runBlocking { _locations.emit(loc) }
    }

    override suspend fun getLastLocation() : Location?{
        val loc =  Location("Manual")
        loc.latitude = -37.8100
        loc.longitude = 144.9600
        return loc
    }
}
