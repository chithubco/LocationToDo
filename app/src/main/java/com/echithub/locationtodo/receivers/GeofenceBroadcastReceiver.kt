package com.echithub.locationtodo.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.echithub.locationtodo.MapsFragment.Companion.ACTION_GEOFENCE_EVENT
import com.echithub.locationtodo.R
import com.echithub.locationtodo.utils.NotificationHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "GeofenceReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.e(TAG, intent.action.toString())
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                Log.e(TAG, "THERE IS AN ERROR")
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                Log.v(TAG, "Geo Fence DWELLED")
                NotificationHelper(context).createNotification()

            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.v(TAG, "Geo Fence Enter")
                NotificationHelper(context).createNotification()

            }
            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.v(TAG, "Geo Fence Exit")
                NotificationHelper(context).createNotification()

            }
        }
    }
}