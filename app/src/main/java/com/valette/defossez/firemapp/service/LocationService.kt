package com.valette.defossez.firemapp.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

class LocationService {

    var myLongitude: Double = 0.0
    var myLatitude: Double = 0.0
    var addressTrouvee = false

    private var locationManager: LocationManager? = null

    fun lancerLaRechercheDeLaLocalisation(ctx : Context) {
        locationManager = ctx.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        Log.d("TAG", "" + myLatitude + " azerty" + myLongitude)
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ctx as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        } else {
            val myLocation = locationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (myLocation != null) {
                myLatitude = myLocation!!.latitude
                myLongitude = myLocation!!.longitude
            }
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener);
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(myLocation: Location) {
            myLatitude = myLocation.latitude
            myLongitude = myLocation.longitude
            if (!addressTrouvee) {
                Log.d("TAG", "Position : " + myLatitude + " ; " + myLongitude)
                addressTrouvee = true
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}