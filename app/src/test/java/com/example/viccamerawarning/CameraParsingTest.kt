package com.example.viccamerawarning

import com.example.viccamerawarning.data.model.CameraLocation
import org.junit.Test
import com.google.gson.Gson
import org.junit.Assert.assertEquals
class CameraParsingTest {

    @Test
    fun parseCameraLocationJson(){
        val json = """
                {"latitude":-37.742681,"longitude":145.022543,"latLongString":"-37.742681,145.022543","locationName":"Albert Street, at the intersection of Albert Street and Gower Street, Preston","cameraType":"Fixed Camera","siteType":"Intersection","status":"commissioned","cameraId":"F07","street":"Albert Street","intersectsWith":"Gower Street","postCode":"3072","state":"Victoria"}
                """
        val obj = Gson().fromJson(json, CameraLocation::class.java)
        assertEquals("F07", obj.cameraId)
        assertEquals(-37.742681, obj.latitude, 0.0001)
        assertEquals(145.022543, obj.longitude, 0.0001)
        assertEquals("-37.742681,145.022543", obj.latLongString)
        assertEquals("commissioned", obj.status)
        assertEquals("Intersection", obj.siteType)
        assertEquals("Fixed Camera", obj.cameraType)
    }
}