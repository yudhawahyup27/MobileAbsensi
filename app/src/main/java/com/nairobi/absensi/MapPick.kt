package com.nairobi.absensi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.nairobi.absensi.ui.theme.Purple
import com.nairobi.absensi.utils.bitmapFromDrawableRes

class MapPick : ComponentActivity() {
    private lateinit var mapView: MapView
    private lateinit var pointAnotationManager: PointAnnotationManager
    private lateinit var pointAnotationOptions: PointAnnotationOptions
    var lat = 0.0
    var lon = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if we received a location from the previous activity
        lat = intent.getDoubleExtra("lat", 0.0)
        lon = intent.getDoubleExtra("long", 0.0)
        ResourceOptionsManager.getDefault(this, getString(R.string.api_key))
        mapView = MapView(this, MapInitOptions(this))
        pointAnotationManager = mapView.annotations.createPointAnnotationManager(mapView)
        pointAnotationOptions = PointAnnotationOptions()
        bitmapFromDrawableRes(this, R.drawable.red_marker)?.let {
            pointAnotationOptions.withIconImage(it)
        }

        // set initial location
        setCamera(lat, lon)
        setMarker(lat, lon)
        // set click listener
        setClickListener()

        setContent {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                // Top bar
                ConstraintLayout(
                    Modifier
                        .fillMaxWidth()
                        .background(Purple)
                        .padding(16.dp)
                ) {
                    val (title, button) = createRefs()

                    // Title
                    Text(
                        text = "Pilih Lokasi",
                        color = Color.White,
                        modifier = Modifier
                            .constrainAs(title) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                    )
                    // Save button
                    Button(
                        onClick = {
                            val intent = Intent()
                            intent.putExtra("lat", lat)
                            intent.putExtra("long", lon)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        },
                        modifier = Modifier
                            .constrainAs(button) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                    ) {
                        Text(text = "Simpan")
                    }
                }
                ConstraintLayout(
                    Modifier
                        .fillMaxSize()
                ) {
                    val (map, fab) = createRefs()
                    AndroidView(
                        factory = { mapView },
                        modifier = Modifier
                            .constrainAs(map) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                    FloatingActionButton(
                        onClick = {
                            getCurrentLocation { (lat, lon) ->
                                setCamera(lat, lon)
                                setMarker(lat, lon)
                            }
                        },
                        modifier = Modifier
                            .constrainAs(fab) {
                                bottom.linkTo(parent.bottom, 16.dp)
                                end.linkTo(parent.end, 16.dp)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "My location"
                        )
                    }
                }
            }
        }
    }

    // Set camera to given location
    private fun setCamera(lat: Double, lon: Double) {
        mapView.getMapboxMap().setCamera(
            cameraOptions {
                center(Point.fromLngLat(lon, lat))
                zoom(15.0)
            }
        )
    }

    // Set marker to given location
    private fun setMarker(lat: Double, lon: Double) {
        // Remove previous marker if exists
        pointAnotationManager.annotations.forEach {
            pointAnotationManager.delete(it)
        }
        // Add new marker
        pointAnotationManager.create(
            pointAnotationOptions
                .withPoint(Point.fromLngLat(lon, lat))
        )
    }

    // Click listener for mapView
    private fun setClickListener() {
        mapView.getMapboxMap().addOnMapClickListener { point ->
            lat = point.latitude()
            lon = point.longitude()
            setCamera(point.latitude(), point.longitude())
            setMarker(point.latitude(), point.longitude())
            true
        }
    }

    // Get current location
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(callback: (Pair<Double, Double>) -> Unit) {
        val locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val location = locationProviderClient.lastLocation
        location.addOnSuccessListener {
            lat = it.latitude
            lon = it.longitude
            callback(it.latitude to it.longitude)
        }
    }

    // Finish activity and send location to previous activity
    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("lat", lat)
            putExtra("long", lon)
        })
        super.onBackPressed()
    }

    companion object {
        fun intent(activity: ComponentActivity, lat: Double, lon: Double): Intent {
            return Intent(activity, MapPick::class.java).apply {
                putExtra("lat", lat)
                putExtra("long", lon)
            }
        }
    }
}