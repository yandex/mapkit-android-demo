package com.yandex.mapkitdemo.routing

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkitdemo.common.CommonColors
import com.yandex.mapkitdemo.common.CommonId
import com.yandex.mapkitdemo.common.showToast
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError

class Activity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var map: Map

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            routePoints = routePoints + point
        }
    }

    private val drivingRouteListener = object : DrivingRouteListener {
        override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
            routes = drivingRoutes
        }

        override fun onDrivingRoutesError(error: Error) {
            when (error) {
                is NetworkError -> showToast("Routes request error due network issues")
                else -> showToast("Routes request unknown error")
            }
        }
    }

    private var routePoints = emptyList<Point>()
        set(value) {
            field = value
            onRoutePointsUpdated()
        }

    private var routes = emptyList<DrivingRoute>()
        set(value) {
            field = value
            onRoutesUpdated()
        }

    private lateinit var drivingRouter: DrivingRouter
    private var drivingSession: DrivingSession? = null
    private lateinit var placemarksCollection: MapObjectCollection
    private lateinit var routesCollection: MapObjectCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        DirectionsFactory.initialize(this)
        setContentView(R.layout.activity_layout)
        mapView = findViewById(R.id.mapview)
        map = mapView.mapWindow.map
        map.addInputListener(inputListener)

        val mapkitVersionView = findViewById<LinearLayout>(R.id.mapkit_version)
        val mapkitVersionTextView = mapkitVersionView.findViewById<TextView>(CommonId.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        placemarksCollection = map.mapObjects.addCollection()
        routesCollection = map.mapObjects.addCollection()

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()

        findViewById<Button>(R.id.button_clear_route).setOnClickListener {
            routePoints = emptyList()
        }

        val map = mapView.mapWindow.map
        map.move(START_POSITION)

        routePoints = DEFAULT_POINTS
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

    private fun onRoutePointsUpdated() {
        placemarksCollection.clear()

        if (routePoints.isEmpty()) {
            drivingSession?.cancel()
            routes = emptyList()
            return
        }

        val imageProvider = ImageProvider.fromResource(this, R.drawable.bullet)
        routePoints.forEach {
            placemarksCollection.addPlacemark(
                it,
                imageProvider,
                IconStyle().apply {
                    scale = 0.5f
                    zIndex = 20f
                }
            )
        }

        if (routePoints.size < 2) return

        val requestPoints = buildList {
            add(RequestPoint(routePoints.first(), RequestPointType.WAYPOINT, null, null))
            addAll(
                routePoints.subList(1, routePoints.size - 1)
                    .map { RequestPoint(it, RequestPointType.VIAPOINT, null, null) })
            add(RequestPoint(routePoints.last(), RequestPointType.WAYPOINT, null, null))
        }

        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()

        drivingSession = drivingRouter.requestRoutes(
            requestPoints,
            drivingOptions,
            vehicleOptions,
            drivingRouteListener,
        )
    }

    private fun onRoutesUpdated() {
        routesCollection.clear()
        if (routes.isEmpty()) return

        routes.forEachIndexed { index, route ->
            routesCollection.addPolyline(route.geometry).apply {
                if (index == 0) styleMainRoute() else styleAlternativeRoute()
            }
        }
    }

    private fun PolylineMapObject.styleMainRoute() {
        zIndex = 10f
        setStrokeColor(ContextCompat.getColor(this@Activity, CommonColors.gray))
        strokeWidth = 5f
        outlineColor = ContextCompat.getColor(this@Activity, CommonColors.black)
        outlineWidth = 3f
    }

    private fun PolylineMapObject.styleAlternativeRoute() {
        zIndex = 5f
        setStrokeColor(ContextCompat.getColor(this@Activity, CommonColors.light_blue))
        strokeWidth = 4f
        outlineColor = ContextCompat.getColor(this@Activity, CommonColors.black)
        outlineWidth = 2f
    }

    companion object {

        private val START_POSITION = CameraPosition(Point(59.941282, 30.308046), 13.0f, 0f, 0f)

        private val DEFAULT_POINTS = listOf(
            Point(59.929576, 30.291737),
            Point(59.954093, 30.305770),
        )
    }
}
