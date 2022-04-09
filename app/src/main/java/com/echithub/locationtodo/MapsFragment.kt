package com.echithub.locationtodo

import android.annotation.SuppressLint
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
import com.echithub.locationtodo.ui.viewmodel.ListViewModel
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ListFragment"
    private lateinit var mListViewModel: ListViewModel

    private var locationList = mutableListOf<LatLng>()
    private var markerList = mutableListOf<Marker>()

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
            Toast.makeText(
                requireContext(),
                "Single Click ${it.longitude} ${it.latitude}",
                Toast.LENGTH_SHORT
            ).show()
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
//            mMap.addMarker(MarkerOptions().position(it).title("New Marker"))
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

}