package com.udacity.project4.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.udacity.project4.MapsFragment.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.project4.utils.NotificationHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.data.model.Reminder

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "GeofenceReceiver"
    private lateinit var reminder: Reminder
    override fun onReceive(context: Context, intent: Intent) {

        val bundle = intent.getBundleExtra("bundle")
        if (bundle != null) {
            Log.i("Parcelable 3",bundle.getParcelable<Reminder>("reminder").toString())
            reminder = bundle.getParcelable<Reminder>("reminder")!!
        }


        Log.e(TAG, intent.action.toString())
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                Log.e(TAG, "THERE IS AN ERROR")
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                Log.v(TAG, "Geo Fence DWELLED")
                NotificationHelper(context).createNotification(reminder)

            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.v(TAG, "Geo Fence Enter")
                NotificationHelper(context).createNotification(reminder)

            }
            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.v(TAG, "Geo Fence Exit")
                NotificationHelper(context).createNotification(reminder)

            }
        }
    }
}