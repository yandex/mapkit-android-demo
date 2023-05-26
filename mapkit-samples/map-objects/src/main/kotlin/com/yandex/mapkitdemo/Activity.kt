package com.yandex.mapkitdemo

import android.graphics.PointF
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterTapListener
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectDragListener
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.map.SizeChangedListener
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.map.TextStyle.Placement
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkitdemo.common.CommonColors
import com.yandex.mapkitdemo.common.CommonDrawables
import com.yandex.mapkitdemo.common.showToast
import com.yandex.runtime.image.AnimatedImageProvider
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider

// Minimal distance in pixels between objects that remain separate.
private const val CLUSTER_RADIUS = 60.0
private const val CLUSTER_MIN_ZOOM = 15

class Activity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var collection: MapObjectCollection
    private lateinit var clasterizedCollection: ClusterizedPlacemarkCollection
    private var isShowGeometryOnMap = true

    private val mapWindowSizeChangedListener = SizeChangedListener { _, _, _ ->
        updateFocusRect()
    }

    private val polylineTapListener = MapObjectTapListener { mapObject, _ ->
        if (mapObject is PolylineMapObject) {
            val color = ColorUtils.polylineColor()
            mapObject.setStrokeColor(ContextCompat.getColor(this, color))
            showToast("Tapped the polyline, change color")
        }
        true
    }

    private val circleTapListener = MapObjectTapListener { mapObject, _ ->
        if (mapObject is CircleMapObject) {
            val circle = GeometryProvider.circle()
            mapObject.geometry = circle
            showToast("Tapped the circle, the new radius: ${circle.radius}")
        }
        true
    }

    private val placemarkTapListener = MapObjectTapListener { mapObject, _ ->
        showToast("Tapped the placemark: ${mapObject.userData}")
        true
    }

    private val pinDragListener = object : MapObjectDragListener {
        override fun onMapObjectDragStart(p0: MapObject) {
            showToast("Start drag event")
        }

        override fun onMapObjectDrag(p0: MapObject, p1: Point) = Unit

        override fun onMapObjectDragEnd(p0: MapObject) {
            showToast("End drag event")
            // Updates clusters position
            clasterizedCollection.clusterPlacemarks(CLUSTER_RADIUS, CLUSTER_MIN_ZOOM)
        }
    }

    private val clusterListener = ClusterListener { cluster ->
        val placemarkTypes = cluster.placemarks.map {
            (it.userData as PlacemarkUserData).type
        }
        // Sets each cluster appearance using the custom view
        // that shows a cluster's pins
        cluster.appearance.setView(
            ViewProvider(
                ClusterView(this).apply {
                    setData(placemarkTypes)
                }
            )
        )
        cluster.appearance.zIndex = 100f

        cluster.addClusterTapListener(clusterTapListener)
    }

    private val clusterTapListener = ClusterTapListener {
        showToast("Clicked on cluster with ${it.size} items")
        true
    }

    // Iterates through map objects and update polylines, circle, and polygons visibility.
    private val geometryVisibilityVisitor = object : MapObjectVisitor {
        override fun onPlacemarkVisited(placemark: PlacemarkMapObject) = Unit

        override fun onPolylineVisited(polyline: PolylineMapObject) {
            polyline.isVisible = isShowGeometryOnMap
        }

        override fun onPolygonVisited(polygon: PolygonMapObject) {
            polygon.isVisible = isShowGeometryOnMap
        }

        override fun onCircleVisited(circle: CircleMapObject) {
            circle.isVisible = isShowGeometryOnMap
        }

        override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean = true
        override fun onCollectionVisitEnd(p0: MapObjectCollection) = Unit
        override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean =
            true

        override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) = Unit
    }

    private val singlePlacemarkTapListener = MapObjectTapListener { _, _ ->
        showToast("Clicked the placemark with composite icon")
        true
    }

    private val animatedPlacemarkTapListener = MapObjectTapListener { _, _ ->
        showToast("Clicked the animated placemark")
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_layout)
        mapView = findViewById(R.id.mapview)
        val map = mapView.mapWindow.map

        mapView.mapWindow.addSizeChangedListener(mapWindowSizeChangedListener)
        updateFocusRect()

        map.move(CameraPosition(Point(25.194113, 55.274531), 14f, 0f, 0f))

        // Add a nested map objects collection
        collection = map.mapObjects.addCollection()

        // Add a polygon
        val polygonMapObject = collection.addPolygon(GeometryProvider.polygon).apply {
            strokeWidth = 1.5f
            strokeColor = ContextCompat.getColor(this@Activity, CommonColors.olive)
            fillColor = ContextCompat.getColor(this@Activity, CommonColors.green)
        }

        // Add a polyline
        val polylineMapObject = collection.addPolyline(GeometryProvider.polyline).apply {
            strokeWidth = 5f
            setStrokeColor(ContextCompat.getColor(this@Activity, CommonColors.gray))
            outlineWidth = 1f
            outlineColor = ContextCompat.getColor(this@Activity, CommonColors.black)
            addTapListener(polylineTapListener)
        }

        // Add a circle
        collection.addCircle(
            GeometryProvider.circle(),
            ContextCompat.getColor(this, CommonColors.red),
            2f,
            ContextCompat.getColor(this, CommonColors.red_alpha),
        ).apply {
            addTapListener(circleTapListener)
        }

        // Add a clusterized collection
        clasterizedCollection = collection.addClusterizedPlacemarkCollection(clusterListener)

        // Add pins to the clusterized collection

        val placemarkTypeToImageProvider = mapOf(
            PlacemarkType.GREEN to ImageProvider.fromResource(this, R.drawable.pin_green),
            PlacemarkType.YELLOW to ImageProvider.fromResource(this, R.drawable.pin_yellow),
            PlacemarkType.RED to ImageProvider.fromResource(this, R.drawable.pin_red),
        )

        GeometryProvider.clusterizedPoints.forEachIndexed { index, point ->
            val type = PlacemarkType.values().random()
            val imageProvider = placemarkTypeToImageProvider[type] ?: return
            clasterizedCollection.addPlacemark(
                point,
                imageProvider,
                IconStyle().apply {
                    anchor = PointF(0.5f, 1.0f)
                    scale = 0.6f
                }
            )
                .apply {
                    // If we want to make placemarks draggable, we should call
                    // clasterizedCollection.clusterPlacemarks on onMapObjectDragEnd
                    isDraggable = true
                    setDragListener(pinDragListener)
                    // Put any data in MapObject
                    userData = PlacemarkUserData("Data_$index", type)
                    this.addTapListener(placemarkTapListener)
                }
        }

        clasterizedCollection.clusterPlacemarks(CLUSTER_RADIUS, CLUSTER_MIN_ZOOM)

        // Composite placemark with text
        val placemark = collection.addPlacemark(Point(25.188413, 55.282270)).apply {
            addTapListener(singlePlacemarkTapListener)
            // Set text near the placemark with the custom TextStyle

            setText(
                "Special place",
                TextStyle().apply {
                    size = 10f
                    placement = Placement.RIGHT
                    offset = 5f
                },
            )
        }

        placemark.useCompositeIcon().apply {
            // Combine several icons in the single composite icon
            setIcon(
                "pin",
                ImageProvider.fromResource(this@Activity, CommonDrawables.ic_dollar_pin),
                IconStyle().apply {
                    anchor = PointF(0.5f, 1.0f)
                    scale = 0.9f
                }
            )
            setIcon(
                "point",
                ImageProvider.fromResource(this@Activity, R.drawable.ic_circle),
                IconStyle().apply {
                    anchor = PointF(0.5f, 0.5f)
                    flat = true
                    scale = 0.05f
                }
            )
        }

        // Add an animated icon
        collection.addPlacemark(
            Point(25.185632, 55.280006),
            AnimatedImageProvider.fromAsset(this, "animation.png"),
            IconStyle().apply { scale = 0.6f }
        ).apply {
            useAnimation().play()
            addTapListener(animatedPlacemarkTapListener)
        }

        // Setup buttons
        findViewById<Button>(R.id.button_change_visibility).apply {
            setOnClickListener {
                showToast("${if (collection.isVisible) "Hide" else "Show"} all objects")
                collection.isVisible = !collection.isVisible
            }
        }

        findViewById<Button>(R.id.button_focus_polyline).apply {
            setOnClickListener {
                val geometry = Geometry.fromPolyline(polylineMapObject.geometry)
                // Focus camera on polyline
                map.move(map.cameraPosition(geometry, null, null, null))
            }
        }

        findViewById<Button>(R.id.button_focus_polygon).apply {
            setOnClickListener {
                val geometry = Geometry.fromPolygon(polygonMapObject.geometry)
                // Focus camera on polygon
                map.move(map.cameraPosition(geometry, null, null, null))
            }
        }

        findViewById<Button>(R.id.button_geometry).apply {
            setOnClickListener {
                // Turned off/on visibility
                isShowGeometryOnMap = !isShowGeometryOnMap
                text = "${if (isShowGeometryOnMap) "Off" else "On"} geometry"
                collection.traverse(geometryVisibilityVisitor)
            }
        }
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

    /**
     * Updates focus rect value using horizontal and vertical margins.
     */
    private fun updateFocusRect() {
        val horizontalMargin = 40f
        val verticalMargin = 60f
        mapView.mapWindow.focusRect = ScreenRect(
            ScreenPoint(horizontalMargin, verticalMargin),
            ScreenPoint(
                mapView.mapWindow.width() - horizontalMargin,
                mapView.mapWindow.height() - verticalMargin
            )
        )
    }
}
