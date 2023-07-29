package com.yandex.mapkitdemo.placemark

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkitdemo.common.CommonDrawables
import com.yandex.mapkitdemo.common.CommonId
import com.yandex.mapkitdemo.common.showToast
import com.yandex.runtime.image.ImageProvider

class Activity : AppCompatActivity() {
    private lateinit var mapView: MapView

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        showToast("Tapped the point (${point.longitude}, ${point.latitude})")
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_layout)
        mapView = findViewById(R.id.mapview)

        val mapkitVersionView = findViewById<LinearLayout>(R.id.mapkit_version)
        val mapkitVersionTextView = mapkitVersionView.findViewById<TextView>(CommonId.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        val map = mapView.mapWindow.map
        map.move(POSITION)

        val imageProvider = ImageProvider.fromResource(this, CommonDrawables.ic_dollar_pin)
        val placemarkObject = map.mapObjects.addPlacemark(POINT, imageProvider)
        placemarkObject.addTapListener(placemarkTapListener)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    companion object {
        private val POINT = Point(55.751280, 37.629720)
        private val POSITION = CameraPosition(POINT, 17.0f, 150.0f, 30.0f)
    }
}
