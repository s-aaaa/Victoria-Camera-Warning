package com.example.viccamerawarning.location

import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class DefaultLocationTracker(
    private val context: Context
) : LocationTracker {


    override fun getLocationUpdates() = callbackFlow<Location> {
        val client = LocationServices.getFusedLocationProviderClient(context)
        // Emit last known location immediately, if available
        val lastLocation = try { client.lastLocation.await() } catch (_: Exception) { null }
        lastLocation?.let { trySend(it).isSuccess }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000 // 1 second
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                if (loc != null) {
                    trySend(loc).isSuccess
                }
            }
        }

        client.requestLocationUpdates(request, callback, null)

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    override suspend fun getLastLocation() :Location?{
        val client = LocationServices.getFusedLocationProviderClient(context)
        val lastLocation = try { client.lastLocation.await() } catch (_: Exception) { null }
        return lastLocation
    }
}