package com.yandex.mapkitdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.LinearRing;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PatternRepeatMode;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.AnimatedImageProvider;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.ui_view.ViewProvider;

import java.util.ArrayList;
import java.util.Random;

/**
 * This example shows how to add simple objects such as polygons, circles and polylines to the map.
 * It also shows how to display images instead.
 */
public class MapObjectsActivity extends Activity {
    /**
     * Replace "your_api_key" with a valid developer key.
     * You can get it at the https://developer.tech.yandex.ru/ website.
     */
    private final String MAPKIT_API_KEY = "your_api_key";
    private final Point CAMERA_TARGET = new Point(59.952, 30.318);
    private final Point ANIMATED_RECTANGLE_CENTER = new Point(59.956, 30.313);
    private final Point TRIANGLE_CENTER = new Point(59.948, 30.313);
    private final Point POLYLINE_CENTER = CAMERA_TARGET;
    private final Point CIRCLE_CENTER = new Point(59.956, 30.323);
    private final Point DRAGGABLE_PLACEMARK_CENTER = new Point(59.948, 30.323);
    private final double OBJECT_SIZE = 0.0015;

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private Handler animationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.map_objects);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(CAMERA_TARGET, 15.0f, 0.0f, 0.0f));
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        animationHandler = new Handler();
        createMapObjects();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    private void createMapObjects() {
        AnimatedImageProvider animatedImage = AnimatedImageProvider.fromAsset(this, "animation.png");
        ArrayList<Point> rectPoints = new ArrayList<>();
        rectPoints.add(new Point(
                ANIMATED_RECTANGLE_CENTER.getLatitude() - OBJECT_SIZE,
                ANIMATED_RECTANGLE_CENTER.getLongitude() - OBJECT_SIZE));
        rectPoints.add(new Point(
                ANIMATED_RECTANGLE_CENTER.getLatitude() - OBJECT_SIZE,
                ANIMATED_RECTANGLE_CENTER.getLongitude() + OBJECT_SIZE));
        rectPoints.add(new Point(
                ANIMATED_RECTANGLE_CENTER.getLatitude() + OBJECT_SIZE,
                ANIMATED_RECTANGLE_CENTER.getLongitude() + OBJECT_SIZE));
        rectPoints.add(new Point(
                ANIMATED_RECTANGLE_CENTER.getLatitude() + OBJECT_SIZE,
                ANIMATED_RECTANGLE_CENTER.getLongitude() - OBJECT_SIZE));
        PolygonMapObject rect = mapObjects.addPolygon(
                new Polygon(new LinearRing(rectPoints), new ArrayList<LinearRing>()));
        rect.setStrokeColor(Color.TRANSPARENT);
        rect.setFillColor(Color.TRANSPARENT);
        rect.setAnimatedImage(animatedImage, 32.0f, PatternRepeatMode.REPEAT);

        ArrayList<Point> trianglePoints = new ArrayList<>();
        trianglePoints.add(new Point(
                TRIANGLE_CENTER.getLatitude() + OBJECT_SIZE,
                TRIANGLE_CENTER.getLongitude() - OBJECT_SIZE));
        trianglePoints.add(new Point(
                TRIANGLE_CENTER.getLatitude() - OBJECT_SIZE,
                TRIANGLE_CENTER.getLongitude() - OBJECT_SIZE));
        trianglePoints.add(new Point(
                TRIANGLE_CENTER.getLatitude(),
                TRIANGLE_CENTER.getLongitude() + OBJECT_SIZE));
        PolygonMapObject triangle = mapObjects.addPolygon(
                new Polygon(new LinearRing(trianglePoints), new ArrayList<LinearRing>()));
        triangle.setFillColor(Color.BLUE);
        triangle.setStrokeColor(Color.BLACK);
        triangle.setStrokeWidth(1.0f);
        triangle.setZIndex(100.0f);

        CircleMapObject circle = mapObjects.addCircle(
                new Circle(CIRCLE_CENTER, 100), Color.GREEN, 2, Color.RED);
        circle.setZIndex(100.0f);

        ArrayList<Point> polylinePoints = new ArrayList<>();
        polylinePoints.add(new Point(
                POLYLINE_CENTER.getLatitude() + OBJECT_SIZE,
                POLYLINE_CENTER.getLongitude()- OBJECT_SIZE));
        polylinePoints.add(new Point(
                POLYLINE_CENTER.getLatitude() - OBJECT_SIZE,
                POLYLINE_CENTER.getLongitude()- OBJECT_SIZE));
        polylinePoints.add(new Point(
                POLYLINE_CENTER.getLatitude(),
                POLYLINE_CENTER.getLongitude() + OBJECT_SIZE));

        PolylineMapObject polyline = mapObjects.addPolyline(new Polyline(polylinePoints));
        polyline.setStrokeColor(Color.BLACK);
        polyline.setZIndex(100.0f);

        PlacemarkMapObject mark = mapObjects.addPlacemark(DRAGGABLE_PLACEMARK_CENTER);
        mark.setOpacity(0.5f);
        mark.setIcon(ImageProvider.fromResource(this, R.drawable.mark));
        mark.setDraggable(true);

        createPlacemarkMapObjectWithViewProvider();
    }

    private void createPlacemarkMapObjectWithViewProvider() {
        final TextView textView = new TextView(this);
        final int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLACK };
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        textView.setTextColor(Color.RED);
        textView.setText("Hello, World!");

        final ViewProvider viewProvider = new ViewProvider(textView);
        final PlacemarkMapObject viewPlacemark =
                mapObjects.addPlacemark(new Point(59.946263, 30.315181), viewProvider);

        final Random random = new Random();
        final int delayToShowInitialText = 5000;  // milliseconds
        final int delayToShowRandomText = 500; // milliseconds;

        // Show initial text `delayToShowInitialText` milliseconds and then
        // randomly change text in textView every `delayToShowRandomText` milliseconds
        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int randomInt = random.nextInt(1000);
                textView.setText("Some text version " + randomInt);
                textView.setTextColor(colors[randomInt % colors.length]);
                viewProvider.snapshot();
                viewPlacemark.setView(viewProvider);
                animationHandler.postDelayed(this, delayToShowRandomText);
            }
        }, delayToShowInitialText);
    }
}
