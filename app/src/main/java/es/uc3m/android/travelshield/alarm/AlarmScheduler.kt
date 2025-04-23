package es.uc3m.android.travelshield.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

class AlarmScheduler(private val context: Context) {

    // Function to schedule the alarm with a given trigger time and country name
    fun scheduleAlarm(triggerAtMillis: Long, countryName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TripAlarmReceiver::class.java).apply {
            putExtra("COUNTRY_NAME", countryName)
        }

        // Create a pending intent for the alarm
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            countryName.hashCode(), // Generate a unique requestCode for each country
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_IMMUTABLE
                    } else {
                        0 // For lower SDK versions, no specific flag
                    }
        )

        // Schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,  // Use RTC_WAKEUP to trigger the alarm even if the device is asleep
            triggerAtMillis,         // The time in milliseconds when the alarm should go off
            pendingIntent            // The intent to trigger
        )
    }
}
