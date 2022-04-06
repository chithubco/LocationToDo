package com.echithub.locationtodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.echithub.locationtodo.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val barumak = LatLng(9.052596841535514, 7.452365927641011)
        mMap.addMarker(MarkerOptions().position(barumak).title("Barumak"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barumak,16f))
        mMap.uiSettings.apply {

        }
        setMapStyle(mMap)

        onMapClicked()
        onMapLongClick()
    }

    private fun setMapStyle(googleMap: GoogleMap){
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.style
                )
            )
            if (!success){
                Log.d("Maps","Failed to add Styles to map")
            }
        }catch (e: Exception){
            Log.d("Maps",e.toString())
        }
    }

    private fun onMapClicked(){
        mMap.setOnMapClickListener {
            Toast.makeText(this,"Single Click ${it.longitude} ${it.latitude}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun onMapLongClick() {
        mMap.setOnMapLongClickListener {
            Toast.makeText(this,"Long Click ${it.longitude} ${it.latitude}",Toast.LENGTH_SHORT).show()
            mMap.addMarker(MarkerOptions().position(it).title("New Marker"))
        }
    }
}