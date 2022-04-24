package com.udacity.project4

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.databinding.AddReminderDialogBinding
import com.udacity.project4.databinding.FragmentMapsBinding
import com.udacity.project4.receivers.GeofenceBroadcastReceiver
import com.udacity.project4.ui.viewmodel.ListViewModel
import com.udacity.project4.utils.Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS
import com.udacity.project4.utils.Constants.GEOFENCE_LOITERING_DELAY_IN_MILLISECONDS
import com.udacity.project4.utils.Constants.GEOFENCE_RADIUS_IN_METERS
import com.udacity.project4.utils.Constants.GEOFENCE_REQUEST_ID
import com.udacity.project4.utils.Constants.REQUEST_TURN_DEVICE_LOCATION_ON
import com.udacity.project4.utils.Permissions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.utils.Constants.PERMISSION_BACKGROND_LOCATION_REQUEST_CODE
import com.udacity.project4.utils.Permissions.hasLocationPermission
import com.udacity.project4.utils.Status
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MapsFragment : Fragment(R.layout.fragment_maps), GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnPoiClickListener,
    EasyPermissions.PermissionCallbacks {

    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var intendedLatLong: LatLng
    private lateinit var selectedReminder: Reminder

//    private lateinit var geofencePendingIntent: PendingIntent

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ListFragment"
    private val mListViewModel by viewModels<ListViewModel>() {
        ListViewModel.ListViewModelFactory((requireContext().applicationContext as ToDoApplication).reminderRepo)
    }

    private var locationList = mutableListOf<LatLng>()
    private var markerList = mutableListOf<Marker>()

    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        if (!hasLocationPermission(requireContext())) {
            Permissions.requestLocationPermission(this)
        } else {
            mMap.isMyLocationEnabled = true
        }

        // Create a Geofence instance
        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        val barumak = LatLng(9.052596841535514, 7.452365927641011)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barumak, 16f))

        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = false
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }
        setMapStyle(mMap)
        addObservers()
        mMap.setOnMarkerClickListener(this)
        mMap.setOnPoiClickListener(this)
        mMap.setOnMyLocationButtonClickListener(this)
        onMapClicked()
        onMapLongClick()
        addListeners()
        displayInfoMessage()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapsBinding.bind(view)
        setHasOptionsMenu(true)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        setHasOptionsMenu(true)
    }

    private fun addObservers() {
        mListViewModel.reminderList.observe(viewLifecycleOwner, Observer {
            mMap.clear()
            for (reminder in it) {
                val location = LatLng(reminder.latitude.toDouble(), reminder.longitude.toDouble())
                addMarker(location, reminder.title, reminder.description)
            }
        })
        mListViewModel.reminders.observe(viewLifecycleOwner, Observer { reminders ->
            for (reminder in reminders.data!!) {
                val location = LatLng(reminder.latitude.toDouble(), reminder.longitude.toDouble())
                addMarker(location, reminder.title, reminder.description)
            }
        })

        mListViewModel.insertReminderMessage.observe(viewLifecycleOwner, Observer { response ->
            when (response.status) {
                Status.ERROR -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG)
                }
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG)
                }
            }
        })
    }

    fun addListeners() {
        binding.btnBackToList.setOnClickListener {
            findNavController().navigate(
                MapsFragmentDirections.actionMapsFragmentToListFragment(
                    null
                )
            )
        }
    }

    // Function to display TextView and Fade it away
    private fun displayInfoMessage() {
        lifecycleScope.launch {
            binding.tvActionDescription.visibility = View.VISIBLE
            delay(2000)
            binding.tvActionDescription.animate().alpha(0f).duration = 1000
            delay(1000)
            binding.tvActionDescription.visibility = View.GONE
        }
    }

    private fun onMapClicked() {
        mMap.setOnMapClickListener {

        }
    }

    private fun onMapLongClick() {
        mMap.setOnMapLongClickListener {
            intendedLatLong = it
            if (Permissions.hasBackgroundLocationPermission(requireContext())) {
                createAddReminderDialog(intendedLatLong)
            } else {
                Permissions.requestBackgroundLocationPermission(this)
            }

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
                var createdDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                mListViewModel.makeReminder(
                    title,
                    description,
                    position.latitude.toString(),
                    position.longitude.toString(), createdDate
                )
            }.setNegativeButton("Cancel") { dialog, which -> }
            .show()
    }

    private fun addMarker(position: LatLng, title: String, snippet: String) {

        // Add Geofence for each marker
        if (Permissions.hasBackgroundLocationPermission(requireContext())) {
            val marker = mMap.addMarker(MarkerOptions().position(position).title(title))
            marker.snippet = snippet
            //Set bundle for receiver

            selectedReminder = Reminder(
                1,
                title,
                snippet,
                position.latitude.toString(),
                position.longitude.toString(),
                ""
            )
//            geofencePendingIntent = pendingIntentGet(selectedReminder)
            createGeofence(selectedReminder) // Great a Geofence
            addCircle(position)
        } else {
            Permissions.requestBackgroundLocationPermission(this)
        }
    }

    private fun pendingIntentGet(reminder: Reminder): PendingIntent {
        val bundle = Bundle()
        bundle.putParcelable("reminder", reminder)

        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        intent.putExtra("bundle",bundle)
        return PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun addCircle(position: LatLng) {
        mMap.addCircle(
            CircleOptions().apply {
                center(position)
                radius(500.0)
                fillColor(R.color.purple_200)
                strokeColor(R.color.purple_200)
            }
        )
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
        val reminder = Reminder(
            1,
            marker.title,
            marker.snippet,
            marker.position.latitude.toString(),
            marker.position.longitude.toString(),
            ""
        )
        displayDetail(reminder)
        return true
    }

    override fun onMyLocationButtonClick(): Boolean {
        if (!hasLocationPermission(requireContext())) {
            Permissions.requestLocationPermission(this)
            return false
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun createGeofence(reminder: Reminder) {
        val position = LatLng(reminder.latitude.toDouble(), reminder.longitude.toDouble())
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_REQUEST_ID)
            .setCircularRegion(position.latitude, position.longitude, GEOFENCE_RADIUS_IN_METERS)
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

        geofencingClient.removeGeofences(pendingIntentGet(reminder)).run {
            addOnCompleteListener {
                geofencingClient.addGeofences(request, pendingIntentGet(reminder)).run {
                    addOnSuccessListener {
                        // Geofences added.
                        Toast.makeText(
                            requireActivity(), "Geofence Added",
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {

            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
//                createGeofence(LatLng(123.990, 123.909))

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndStartGeofence(false)
        }

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(
                requireActivity(),
            ).build().show()
        } else {
            Permissions.requestLocationPermission(this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        mMap.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_BACKGROND_LOCATION_REQUEST_CODE) {
            createAddReminderDialog(intendedLatLong)
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.map_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_silver -> {
                setMapStyle(R.raw.map_silver)
            }
            R.id.map_retro -> {
                setMapStyle(R.raw.map_retro)
            }
            R.id.map_dark -> {
                setMapStyle(R.raw.map_dark)
            }
            R.id.map_night -> {
                setMapStyle(R.raw.map_night)
            }
            R.id.map_aubergine -> {
                setMapStyle(R.raw.map_aubergine)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMapStyle(resource: Int) {
        try {
            mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    resource
                )
            )
        } catch (e: Exception) {
            Log.d("Map Style", e.toString())
        }
    }


    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "HuntMainActivity.treasureHunt.action.ACTION_GEOFENCE_EVENT"
    }


}