package com.vivek.quipmenttask.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.vivek.quipmenttask.data.model.Trip
import java.io.IOException

class GeoCoderHelper(private val context: Context) {

    private fun getLatLngFromAddress(addressStr: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context)
        var latLng: Pair<Double, Double>? = null
        try {
            var addresses: MutableList<Address> = mutableListOf()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //This code doesn't work sometimes
//                geocoder.getFromLocationName(addressStr, 1) {
//                   addresses.addAll(it)
//                }
//            } else {
                @Suppress("DEPRECATION")
                addresses = geocoder.getFromLocationName(addressStr, 1)?: mutableListOf()
            //}
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                latLng = Pair(address.latitude, address.longitude)
            }
        } catch (e: IOException) {
            Log.e("GeoCoderHelper", "Error getting LatLng from address: ${e.message}")
        }
        return latLng
    }

    fun generateGoogleMapsDirectionsLatLngURL(addressObjects: List<Trip>): String {
        if (addressObjects.isEmpty()) {
            return ""
        }

        val baseURL = "https://www.google.com/maps/dir/"
        val startAddress = addressObjects.firstOrNull()?.pickUpAddress
        val destinationAddress = addressObjects.lastOrNull()?.dropOffAddress
        val waypoints = addressObjects.subList(1, addressObjects.size)

        if (addressObjects.size == 1) {
            val sourceLatLng =
                startAddress?.let { GeoCoderHelper(context).getLatLngFromAddress(it) }
            val destinationLatLng =
                destinationAddress?.let { GeoCoderHelper(context).getLatLngFromAddress(it) }

            if (sourceLatLng == null || destinationLatLng == null) {
                return ""
            }

            return "$baseURL${sourceLatLng.first},${sourceLatLng.second}/${destinationLatLng.first},${destinationLatLng.second}"
        }

        // For size two or more
        val sourceLatLng = startAddress?.let { GeoCoderHelper(context).getLatLngFromAddress(it) }
        val destinationLatLng =
            destinationAddress?.let { GeoCoderHelper(context).getLatLngFromAddress(it) }

        if (sourceLatLng == null || destinationLatLng == null) {
            return ""
        }


        val urlBuilder = StringBuilder(baseURL)
        urlBuilder.append("${sourceLatLng.first},${sourceLatLng.second}/")

        val wayPoint =
            GeoCoderHelper(context).getLatLngFromAddress(addressObjects.firstOrNull()?.dropOffAddress!!)
        urlBuilder.append("${wayPoint?.first},${wayPoint?.second}/")

        // Add waypoints
        waypoints.forEachIndexed { index, address ->
            val startWayTolatLng =
                GeoCoderHelper(context).getLatLngFromAddress(address.pickUpAddress)
            val endWayTolatLng =
                GeoCoderHelper(context).getLatLngFromAddress(address.dropOffAddress)
            if (index != waypoints.size - 1 && startWayTolatLng != null && endWayTolatLng != null) { //middle trips as way point
                urlBuilder.append("${startWayTolatLng.first},${startWayTolatLng.second}/${endWayTolatLng.first},${startWayTolatLng.second}/")
            } else if (startWayTolatLng != null) {  //last trip pickup address as way point
                urlBuilder.append("${startWayTolatLng.first},${startWayTolatLng.second}/")
            }
        }

        // Add destination
        urlBuilder.append("${destinationLatLng.first},${destinationLatLng.second}")
        return urlBuilder.toString()
    }

    fun generateGoogleMapsDirectionsAddressURL(addressObjects: List<Trip>): String {
        if (addressObjects.isEmpty()) {
            return ""
        }

        val baseURL = "https://www.google.com/maps/dir/"
        val startAddress = addressObjects.firstOrNull()?.pickUpAddress
        val destinationAddress = addressObjects.lastOrNull()?.dropOffAddress
        val waypoints = addressObjects.subList(1, addressObjects.size)

        if (addressObjects.size == 1) {
            return "$baseURL$startAddress/$destinationAddress"
        }

        // For size two or more
        val urlBuilder = StringBuilder(baseURL)
        urlBuilder.append("$startAddress/")

        val firstDropOffAddress = addressObjects.firstOrNull()?.dropOffAddress
        urlBuilder.append("$firstDropOffAddress/")

        // Add waypoints
        waypoints.forEachIndexed { index, address ->
            val startWayToAddress = address.pickUpAddress
            val endWayToAddress = address.dropOffAddress
            if (index != waypoints.size - 1) { //middle trips as way point
                urlBuilder.append("$startWayToAddress/$endWayToAddress/")
            } else {  //last trip pickup address as way point
                urlBuilder.append("$startAddress/")
            }
        }

        // Add destination
        urlBuilder.append("$destinationAddress")
        return urlBuilder.toString()
    }
}
