package com.manchasdelivery_associates.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.manchasdelivery_associates.R



class LocationUpdateService(): Service() {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private var baseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    private lateinit var fullReference: DatabaseReference
    private lateinit var locationCallback: LocationCallback

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(applicationContext, applicationContext.getString(R.string.stopping_service), Toast.LENGTH_LONG).show()
        stopForeground(true)
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    override fun onCreate() {
        super.onCreate()
        initData()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fullReference= getFullReference(intent)

        val notification = NotificationUtils.getNotification(applicationContext,
            NotificationUtils.N_ID_F_ScreenShot)
        startForeground(notification.first, notification.second)
        startLocationUpdates()
        return START_STICKY
    }

    private fun getFullReference(intent: Intent?): DatabaseReference {
        val ownerOfRequest = intent?.getStringExtra("userId") ?: ""
        val requestId = intent?.getStringExtra("requestId") ?: ""
        return baseReference.child(ownerOfRequest).child("requests").child(requestId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient?.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }


    private fun initData() {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return

                val lat = locationResult.lastLocation.latitude
                val long = locationResult.lastLocation.longitude
                val latLong = LatLng(lat,long)
                updateLocationOfDelivery(fullReference, latLong)
            }
        }

        locationRequest = LocationRequest.create()
        locationRequest.interval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private fun updateLocationOfDelivery(reference:DatabaseReference, latLng: LatLng) {
        val latReference = reference.child("trackingLat")
        val longReference = reference.child("trackingLong")
        Log.i("My reference", reference.toString())
        latReference.setValue(latLng.latitude)
        longReference.setValue(latLng.longitude)
    }

}