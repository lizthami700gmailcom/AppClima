package com.project.miclima.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

class LocationViewModel {


    companion object{

        fun getLocation(context: Context): ArrayList<Double>?{

            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null;
            }

            val data = ArrayList<Double>()
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    data.add(location.latitude)
                    data.add(location.longitude)
                }
            }
            if(data.isEmpty()){
                return null
            }
            return data
        }
    }
}