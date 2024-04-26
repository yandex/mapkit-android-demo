package com.yandex.mapkitdemo;

import static com.yandex.mapkitdemo.ConstantsUtils.ANIMATED_PLACEMARK_CENTER;
import static com.yandex.mapkitdemo.ConstantsUtils.ANIMATED_RECTANGLE_CENTER;
import static com.yandex.mapkitdemo.ConstantsUtils.CIRCLE_CENTER;
import static com.yandex.mapkitdemo.ConstantsUtils.DEFAULT_POINT;
import static com.yandex.mapkitdemo.ConstantsUtils.DRAGGABLE_PLACEMARK_CENTER;
import static com.yandex.mapkitdemo.ConstantsUtils.POLYLINE_CENTER;
import static com.yandex.mapkitdemo.ConstantsUtils.TRIANGLE_CENTER;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.LinearRing;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkAnimation;
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
    private final double OBJECT_SIZE = 0.0015;

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private Handler animationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.map_objects);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);
        mapView.getMapWindow().getMap().move(
                new CameraPosition(DEFAULT_POINT, 15.0f, 0.0f, 0.0f));
        mapObjects = mapView.getMapWindow().getMap().getMapObjects().addCollection();
        animationHandler = new Handler(Looper.myLooper());
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
        rect.setPattern(animatedImage, 1.f);

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

        createTappableCircle();

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

        PlacemarkMapObject mark = mapObjects.addPlacemark();
        mark.setGeometry(DRAGGABLE_PLACEMARK_CENTER);
        mark.setOpacity(0.5f);
        mark.setIcon(ImageProvider.fromResource(this, R.drawable.mark));
        mark.setDraggable(true);

        createPlacemarkMapObjectWithViewProvider();
        createAnimatedPlacemark();
    }

    // Strong reference to the listener.
    private MapObjectTapListener circleMapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            if (mapObject instanceof CircleMapObject) {
                CircleMapObject circle = (CircleMapObject)mapObject;

                float randomRadius = 100.0f + 50.0f * new Random().nextFloat();

                Circle curGeometry = circle.getGeometry();
                Circle newGeometry = new Circle(curGeometry.getCenter(), randomRadius);
                circle.setGeometry(newGeometry);

                Object userData = circle.getUserData();
                if (userData instanceof CircleMapObjectUserData) {
                    CircleMapObjectUserData circleUserData = (CircleMapObjectUserData)userData;

                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "Circle with id " + circleUserData.id + " and description '"
                                    + circleUserData.description + "' tapped",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            return true;
        }
    };

    private class CircleMapObjectUserData {
        final int id;
        final String description;

        CircleMapObjectUserData(int id, String description) {
            this.id = id;
            this.description = description;
        }
    }

    private void createTappableCircle() {
        CircleMapObject circle = mapObjects.addCircle(new Circle(CIRCLE_CENTER, 100));
        circle.setStrokeColor(Color.GREEN);
        circle.setStrokeWidth(2.f);
        circle.setFillColor(Color.RED);
        circle.setZIndex(100.f);
        circle.setUserData(new CircleMapObjectUserData(42, "Tappable circle"));

        // Client code must retain strong reference to the listener.
        circle.addTapListener(circleMapObjectTapListener);
    }

    private void createPlacemarkMapObjectWithViewProvider() {
        final TextView textView = new TextView(this);
        final int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLACK };
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        textView.setTextColor(Color.RED);
        textView.setText("Hello, World!");

        final ViewProvider viewProvider = new ViewProvider(textView);
        final PlacemarkMapObject viewPlacemark = mapObjects.addPlacemark();
        viewPlacemark.setGeometry(new Point(59.946263, 30.315181));
        viewPlacemark.setView(viewProvider);

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

    private void createAnimatedPlacemark() {
        final AnimatedImageProvider imageProvider =
                AnimatedImageProvider.fromAsset(this,"animation.png");

        mapObjects.addPlacemark(placemark -> {
            placemark.setGeometry(ANIMATED_PLACEMARK_CENTER);
            final PlacemarkAnimation animatedIcon = placemark.useAnimation();
            animatedIcon.setIcon(imageProvider, new IconStyle(), animatedIcon::play);
        });
    }
}
