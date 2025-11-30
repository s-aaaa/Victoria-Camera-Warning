package com.example.viccamerawarning.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng

class MockLocationProvider {
    var defaultLat = -37.8700
    var defaultLng = 144.9600
    var lat = -37.8100
    var lng = 144.9600

    constructor(){
        resetLocation()
    }

    fun resetLocation()
    {
        lat = defaultLat
        lng = defaultLng
    }


    fun moveCloserTo(target: LatLng) {
        lat += (target.latitude - lat) * 0.2
        lng += (target.longitude - lng) * 0.2
    }

    fun getLocation() = Location("mock").apply {
        latitude = lat
        longitude = lng
    }


}
