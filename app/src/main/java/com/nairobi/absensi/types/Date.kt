package com.nairobi.absensi.types

import java.util.Calendar

// Date representation
class Date(
    unixInit: Long = Calendar.getInstance().timeInMillis
) {
    private var _cal: Calendar

    init {
        val cal = Calendar.getInstance()
        cal.timeInMillis = unixInit
        _cal = cal
    }

    var year: Int
        get() = _cal.get(Calendar.YEAR)
        set(value) {
            _cal.set(Calendar.YEAR, value)
        }

    var month: Int
        get() = _cal.get(Calendar.MONTH) + 1
        set(value) {
            _cal.set(Calendar.MONTH, value)
        }

    var day: Int
        get() = _cal.get(Calendar.DATE)
        set(value) {
            _cal.set(Calendar.DATE, value)
        }

    var time: Time
        get() = Time(_cal.timeInMillis)
        set(value) {
            _cal.set(Calendar.HOUR, value.hour)
            _cal.set(Calendar.MINUTE, value.minute)
            _cal.set(Calendar.SECOND, value.second)
        }

    // Get unix time representation of Date
    fun unix(): Long {
        return _cal.timeInMillis
    }

    // Return string representation of Date
    fun string(showTime: Boolean = false): String {
        var str = "$year/$month/$day"
        if (showTime) {
            str += " ${time.string(true)}"
        }
        return str
    }

    // Check date is before another date
    fun before(date: Date): Boolean {
        return _cal.before(date._cal)
    }

    // Check date is after another date
    fun after(date: Date): Boolean {
        return _cal.after(date._cal)
    }

    // Override '==' to only match year, month and day
    override fun equals(other: Any?): Boolean {
        return if (other is Date) {
            _cal.get(Calendar.YEAR) == other._cal.get(Calendar.YEAR) &&
                    _cal.get(Calendar.MONTH) == other._cal.get(Calendar.MONTH) &&
                    _cal.get(Calendar.DATE) == other._cal.get(Calendar.DATE)
        } else {
            false
        }
    }

    // Check if date is today
    fun isToday(): Boolean {
        val cal = Calendar.getInstance()
        return _cal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                _cal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                _cal.get(Calendar.DATE) == cal.get(Calendar.DATE)
    }

    // Check if date is sunday
    fun isSunday(): Boolean {
        return _cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

    // Check if date is within a range
    fun inRange(from: Date, to: Date): Boolean {
        return _cal.after(from._cal) && _cal.before(to._cal)
    }

    // Get hours between two dates
    fun hoursBetween(to: Date): Int {
        val diff = to._cal.timeInMillis - _cal.timeInMillis
        return (diff / 1000 / 60 / 60).toInt()
    }

    // Get days between two dates
    fun daysBetween(to: Date): Int {
        val diff = to._cal.timeInMillis - _cal.timeInMillis
        return (diff / 1000 / 60 / 60 / 24).toInt()
    }

    companion object {
        fun range(from: Date, to: Date): ArrayList<Date> {
            val dates = ArrayList<Date>()
            val oneDayInUnix = 86400000
            var current = from
            while (current.before(to)) {
                dates.add(current)
                current = Date(current.unix() + oneDayInUnix)
            }
            return dates
        }
    }
}