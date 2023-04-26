package com.nairobi.absensi.neuralnet

import android.graphics.RectF

class Recognition(
    var label: String,
    var distance: Float,
    var location: RectF
) {
    var extra: Array<FloatArray>? = null
}