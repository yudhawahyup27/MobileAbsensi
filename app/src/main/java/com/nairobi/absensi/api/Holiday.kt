package com.nairobi.absensi.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.Date

// Api data
// json data: {"holiday_date":"2023-12-25","holiday_name":"Hari Raya Natal","is_national_holiday":true}
data class Holiday(
    val holiday_date: String,
    val holiday_name: String,
    val is_national_holiday: Boolean,
) {
    // Check if holiday is today
    fun isToday(): Boolean {
        val date = Date()
        val yearNow = date.year + 1900
        val monthNow = date.month + 1
        val dayNow = date.date
        val holidayDate = holiday_date.split("-")
        return yearNow == holidayDate[0].toInt() && monthNow == holidayDate[1].toInt() && dayNow == holidayDate[2].toInt()
    }

    companion object {
        // Create Holiday from JSONObject
        fun fromJSON(json: JSONObject): Holiday {
            return Holiday(
                json.getString("holiday_date"),
                json.getString("holiday_name"),
                json.getBoolean("is_national_holiday"),
            )
        }

        // Create Holiday from JSON string
        fun fromJSONString(json: String): Holiday {
            return fromJSON(JSONObject(json))
        }
    }
}

// Get holiday data from API
// the method will run on new thread and return data to callback on main thread
fun getHolidayData(callback: (ArrayList<Holiday>) -> Unit) {
    val url = URL("https://api-harilibur.vercel.app/api")

    // Create new handler
    val handler = Handler(Looper.getMainLooper())
    val thread = Thread {
        try {
            // Get data from API
            val data = url.readText()
            val holidayData = JSONArray(data)


            // Create array list of holiday
            val holidayList = ArrayList<Holiday>()
            for (i in 0 until holidayData.length()) {
                holidayList.add(Holiday.fromJSON(holidayData.getJSONObject(i)))
            }

            // Run callback on main thread
            handler.post {
                callback(holidayList)
            }
        } catch (e: Exception) {
            Log.e("getHolidayData", e.toString())
        }
    }
    thread.join()
}