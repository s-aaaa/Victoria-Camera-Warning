package com.example.viccamerawarning.data.model

data class CameraLocation(
    val latitude: Double,
    val longitude: Double,
    val latLongString: String,
    val locationName: String,
    val cameraType: String,
    val siteType: String,
    val status: String,
    val cameraId: String,
    val street: String,
    val intersectsWith: String,
    val postCode: String,
    val state: String
) {
    override fun toString(): String {
        return "CameraLocation(" +
                "latitude=$latitude, " +
                "longitude=$longitude, " +
                "latLongString='$latLongString', " +
                "locationName='$locationName', " +
                "cameraType='$cameraType', " +
                "siteType='$siteType', " +
                "status='$status', " +
                "cameraId='$cameraId', " +
                "street='$street', " +
                "intersectsWith='$intersectsWith', " +
                "postCode='$postCode', " +
                "state='$state'" +
                ")"

    }
}