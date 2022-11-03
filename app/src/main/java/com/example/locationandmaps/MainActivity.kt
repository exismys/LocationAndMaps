package com.example.locationandmaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btGetLocation: Button
    private lateinit var btnGetLocationFromAddress: Button
    private lateinit var btOpenMap: Button
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvProvider: TextView
    private lateinit var tvCountry: TextView
    private lateinit var tvAddress: TextView
    private lateinit var etAddress: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btGetLocation = findViewById(R.id.btGetLocation)
        btnGetLocationFromAddress = findViewById(R.id.btn_getLocationFromAddress)
        btOpenMap = findViewById(R.id.btOpenMap)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvProvider = findViewById(R.id.tvProvider)
        tvCountry = findViewById(R.id.tvCountry)
        tvAddress = findViewById(R.id.tvAddress)
        etAddress = findViewById(R.id.et_address)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btGetLocation.setOnClickListener {
            getCurrentLocation()
        }

        btnGetLocationFromAddress.setOnClickListener {
            getLocationFromAddress(etAddress.text.toString())
        }

        btOpenMap.setOnClickListener {
            openMap()
        }
    }

    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val geocoder = Geocoder(this, Locale.getDefault())

            val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            latitude = list[0].latitude
            longitude = list[0].longitude

            tvLatitude.text = "Latitude: $latitude"
            tvLongitude.text = "Longitude: $longitude"
            tvProvider.text = "Provider: ${location.provider}"
            tvCountry.text = "Country: ${list[0].countryName}"
            tvAddress.text = "Address: ${list[0].getAddressLine(0)}"

            btOpenMap.visibility = View.VISIBLE
        }.addOnFailureListener {
            Toast.makeText(this, "Failed on getting current location", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    Toast.makeText(this, "You need to grant", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openMap() {
        val uri = Uri.parse("geo: $latitude, $longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun getLocationFromAddress(addressStr: String) {
        val coder = Geocoder(this)
        try {
            val address: List<Address> = coder.getFromLocationName(addressStr, 5)
            val location = address[0]
            latitude = location.latitude
            longitude = location.longitude
            tvLatitude.text = "Latitude: $latitude"
            tvLongitude.text = "Longitude: $longitude"
            tvCountry.text = "Country: ${address[0].countryName}"
            tvAddress.text = "Address: ${address[0].getAddressLine(0)}"
            btOpenMap.visibility = View.VISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}