package com.echithub.locationtodo

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.databinding.AddReminderDialogBinding
import com.echithub.locationtodo.databinding.FragmentMapsBinding
import com.echithub.locationtodo.receivers.GeofenceBroadcastReceiver
import com.echithub.locationtodo.ui.viewmodel.ListViewModel
import com.echithub.locationtodo.utils.Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS
import com.echithub.locationtodo.utils.Constants.GEOFENCE_LOITERING_DELAY_IN_MILLISECONDS
import com.echithub.locationtodo.utils.Constants.GEOFENCE_RADIUS_IN_METERS
import com.echithub.locationtodo.utils.Constants.GEOFENCE_REQUEST_ID
import com.echithub.locationtodo.utils.Constants.REQUEST_TURN_DEVICE_LOCATION_ON
import com.echithub.locationtodo.utils.Permissions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnPoiClickListener, EasyPermissions.PermissionCallbacks {

    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ListFragment"
    private lateinit var mListViewModel: ListViewModel

    private var locationList = mutableListOf<LatLng>()
    private var markerList = mutableListOf<Marker>()

    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        // Create a Geofence instance
        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        val barumak = LatLng(9.052596841535514, 7.452365927641011)
        mMap.addMarker(MarkerOptions().position(barumak).title("Barumak"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barumak, 16f))
        mMap.uiSettings.apply {

        }
        setMapStyle(mMap)
        mListViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        mListViewModel.readAllData.observe(viewLifecycleOwner, Observer { reminders ->
            for (reminder in reminders) {
                val location = LatLng(reminder.latitude.toDouble(), reminder.longitude.toDouble())
                addMarker(location, reminder.title, reminder.description)
            }
        })
        mMap.setOnMarkerClickListener(this)
        mMap.setOnPoiClickListener(this)
        mMap.setOnMyLocationButtonClickListener(this)
        onMapClicked()
        onMapLongClick()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_maps, container, false)
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun onMapClicked() {
        mMap.setOnMapClickListener {

            if (Permissions.hasBackgroundLocationPermission(requireContext())){
                createGeofence(it) // Great a Geofence
            }else{
                Permissions.requestBackgroundLocationPermission(this)
            }

        }
    }

    private fun onMapLongClick() {
        mMap.setOnMapLongClickListener {
            Toast.makeText(
                requireContext(),
                "Long Click ${it.longitude} ${it.latitude}",
                Toast.LENGTH_SHORT
            ).show()
            createAddReminderDialog(it)
        }
    }

    private fun createAddReminderDialog(position: LatLng) {
        val dialogBinding = DataBindingUtil.inflate<AddReminderDialogBinding>(
            LayoutInflater.from(requireContext()),
            R.layout.add_reminder_dialog,
            null,
            false
        )

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Add Reminder") { dialog, which ->
                var title = dialogBinding.etTitle.text.toString()
                var description = dialogBinding.etDescription.text.toString()
                val reminderToAdd = Reminder(
                    0,
                    title,
                    description,
                    position.latitude.toString(),
                    position.longitude.toString(),
                    ""
                )
                mListViewModel.addReminder(reminderToAdd)
                Log.i(TAG, "Success adding record")
                mListViewModel.refresh()
                addMarker(position, title, description)
            }.setNegativeButton("Cancel") { dialog, which -> }
            .show()
    }

    private fun addMarker(position: LatLng, title: String, snippet: String) {
        val marker = mMap.addMarker(MarkerOptions().position(position).title(title))
        marker.snippet = snippet
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.style
                )
            )
            if (!success) {
                Log.d("Maps", "Failed to add Styles to map")
            }
        } catch (e: Exception) {
            Log.d("Maps", e.toString())
        }
    }

    private fun displayDetail(reminder: Reminder) {

        val directions = MapsFragmentDirections.actionMapsFragmentToDetailFragment(reminder)
        findNavController().navigate(directions)

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.i("Market", marker.title)
        // Fet Reminder with this title
        val reminder = Reminder(1,marker.title,marker.snippet,marker.position.latitude.toString(),marker.position.longitude.toString(),"")
//        lifecycleScope.launch(Dispatchers.IO) {
//            val reminder = mListViewModel.getReminderWithId(marker.title)
//            displayDetail(reminder)
//            Log.i("Reminder",reminder.toString())
//        }
        displayDetail(reminder)
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17f), null)
        return true
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    @SuppressLint("MissingPermission")
    private fun createGeofence(position: LatLng){
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_REQUEST_ID)
            .setCircularRegion(position.latitude,position.longitude,GEOFENCE_RADIUS_IN_METERS)
            .setLoiteringDelay(GEOFENCE_LOITERING_DELAY_IN_MILLISECONDS)
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
//            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
            .build()

        val request = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL)
//            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofence(geofence)
        }.build()

        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnCompleteListener {
                geofencingClient.addGeofences(request, geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        // Geofences added.
                        Toast.makeText(requireActivity(), "Geofence Added",
                            Toast.LENGTH_LONG)
                            .show()
                        Log.e("Add Geofence", geofence.requestId)
                    }
                    addOnFailureListener {
                        if ((it.message != null)) {
                            Log.w(TAG, it.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onPoiClick(poi: PointOfInterest) {
        createAddReminderDialog(poi.latLng)
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true){
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    exception.startResolutionForResult(requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {

            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                createGeofence(LatLng(123.990,123.909))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            SettingsDialog.Builder(
                requireActivity(),
            ).build().show()
        }else{
            Permissions.requestLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        TODO("Not yet implemented")
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "HuntMainActivity.treasureHunt.action.ACTION_GEOFENCE_EVENT"
    }


}