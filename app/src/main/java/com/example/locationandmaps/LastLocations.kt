package com.example.locationandmaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LastLocations : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var locationViews: Array<TextView>
    private lateinit var  btnGetLastLocation: Button
    private lateinit var btnDisplayLastFive: Button
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locations = ArrayList<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_locations)

        locationViews = arrayOf(
            findViewById(R.id.tv_location1),
            findViewById(R.id.tv_location2),
            findViewById(R.id.tv_location3),
            findViewById(R.id.tv_location4),
            findViewById(R.id.tv_location5)
        )

        for (views in locationViews) {
            views.visibility = View.INVISIBLE
        }
        btnGetLastLocation = findViewById(R.id.btn_getLastLocation)
        btnDisplayLastFive = findViewById(R.id.btn_displayLastFive)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btnGetLastLocation.setOnClickListener {
            fetchLastLocations()
        }
        btnDisplayLastFive.setOnClickListener {
            fetchLastFive()
        }
    }

    private fun fetchLastFive() {
        for ((pos, location) in locations.withIndex()) {
            val view = locationViews[pos]
            view.visibility = View.VISIBLE
            view.text = location
        }
    }

    private fun fetchLastLocations() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            val coder = Geocoder(this)
            val addresses = coder.getFromLocation(it.latitude, it.longitude, 1)
            val address = addresses[0].getAddressLine(0)
            locationViews[0].text = address
            locationViews[0].visibility = View.VISIBLE
            if (locations.size >= 5) {
                locations.removeFirst()
            }
            locations.add(address)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed on getting current location", Toast.LENGTH_SHORT).show()
        }
    }
}