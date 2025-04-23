package es.uc3m.android.travelshield.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class TripAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val country = intent.getStringExtra("COUNTRY_NAME")
        Toast.makeText(context, "Your trip to $country starts today!", Toast.LENGTH_LONG).show()
    }
}
