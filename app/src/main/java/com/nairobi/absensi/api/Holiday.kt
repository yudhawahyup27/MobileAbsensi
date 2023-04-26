package com.nairobi.absensi.api

import android.os.Handler
import android.os.Looper
import com.nairobi.absensi.types.Date
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

// Get holiday data from API
fun getHolidayData(callback: (ArrayList<Date>) -> Unit) {
    val url = URL("https://api-harilibur.vercel.app/api")

    // Create new handler
    val handler = Handler(Looper.getMainLooper())
    val thread = Thread {
        try {
            // Get data from API
            val data = url.readText()
            val holidayData = JSONArray(data)


            // Create array list of holiday
            val holidayList = ArrayList<Date>()
            for (i in 0 until holidayData.length()) {
                val obj = JSONObject(holidayData[i].toString())
                val dateSplit = obj.getString("holiday_date").split("-")
                val date = Date()
                date.year = dateSplit[0].toInt()
                date.month = dateSplit[1].toInt()
                date.day = dateSplit[2].toInt()
                holidayList.add(date)
            }

            // Run callback on main thread
            handler.post {
                callback(holidayList)
            }
        } catch (e: Exception) {
            handler.post {
                callback(ArrayList())
            }
        }
    }
    thread.start()
}