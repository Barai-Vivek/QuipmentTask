package com.vivek.quipmenttask.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException

class GeoCoderHelper(private val context: Context) {

    fun getLatLngFromAddress(addressStr: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context)
        var latLng: Pair<Double, Double>? = null
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(addressStr, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    latLng = Pair(address.latitude, address.longitude)
                }
            }
        } catch (e: IOException) {
            Log.e("GeoCoderHelper", "Error getting LatLng from address: ${e.message}")
        }
        return latLng
    }
}
