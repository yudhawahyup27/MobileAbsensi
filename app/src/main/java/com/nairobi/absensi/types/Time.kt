package com.nairobi.absensi.types

import java.util.*

// Time representation
class Time(
    unixInit: Long = Calendar.getInstance().timeInMillis
) {
    private var _cal: Calendar

    init {
        val cal = Calendar.getInstance()
        cal.timeInMillis = unixInit
        _cal = cal
    }

    var hour: Int
        get() = _cal.get(Calendar.HOUR_OF_DAY)
        set(value) {
            _cal.set(Calendar.HOUR_OF_DAY, value)
        }

    var minute: Int
        get() = _cal.get(Calendar.MINUTE)
        set(value) {
            _cal.set(Calendar.MINUTE, value)
        }

    var second: Int
        get() = _cal.get(Calendar.SECOND)
        set(value) {
            _cal.set(Calendar.SECOND, value)
        }

    // Get unix time representation of Time
    fun unix(): Long {
        return _cal.timeInMillis
    }

    // Get string representation of Time
    fun string(showSecond: Boolean = false): String {
        var str = "$hour:$minute"
        if (showSecond) {
            str += ":$second"
        }
        return str
    }

    // Check if time is before another time
    fun before(time: Time): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = cal1.clone() as Calendar
        cal1.set(Calendar.HOUR_OF_DAY,  hour)
        cal1.set(Calendar.MINUTE, minute)
        cal2.set(Calendar.HOUR_OF_DAY, time.hour)
        cal2.set(Calendar.MINUTE, time.minute)
        return cal1.before(cal2)
    }

    // Check if time is after another time
    fun after(time: Time): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = cal1.clone() as Calendar
        cal1.set(Calendar.HOUR_OF_DAY,  hour)
        cal1.set(Calendar.MINUTE, minute)
        cal2.set(Calendar.HOUR_OF_DAY, time.hour)
        cal2.set(Calendar.MINUTE, time.minute)
        return cal1.after(cal2)
    }

    // Get how many minutes between time and another time
    fun distance(time: Time): Long {
        val cal1 = Calendar.getInstance()
        val cal2 = cal1.clone() as Calendar
        cal1.set(Calendar.HOUR_OF_DAY,  hour)
        cal1.set(Calendar.MINUTE, minute)
        cal2.set(Calendar.HOUR_OF_DAY, time.hour)
        cal2.set(Calendar.MINUTE, time.minute)
        return (cal2.timeInMillis - cal1.timeInMillis) / 60000
    }
}