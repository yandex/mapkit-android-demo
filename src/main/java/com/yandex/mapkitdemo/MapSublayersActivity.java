package com.yandex.mapkitdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yandex.mapkit.ConflictResolutionMode;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.LinearRing;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.LayerNames;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.Sublayer;
import com.yandex.mapkit.map.SublayerFeatureType;
import com.yandex.mapkit.map.SublayerManager;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.AnimatedImageProvider;

import java.util.ArrayList;

/**
 * This example shows how to add simple objects such as polygons, circles and polylines to the map.
 * It also shows how to display images instead.
 */
public class MapSublayersActivity extends Activity {
    /**
     * Replace "your_api_key" with a valid developer key.
     * You can get it at the https://developer.tech.yandex.ru/ website.
     */
    private final String MAPKIT_API_KEY = "your_api_key";
    private final Point CAMERA_TARGET = new Point(59.951029, 30.317181);

    private MapView mapView;
    private SublayerManager sublayerManager;
    private MapObjectCollection mapObjects;

    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
            AnimatedImageProvider provider =
                    AnimatedImageProvider.fromAsset(getApplicationContext(), "animation.png");
            IconStyle iconStyle = new IconStyle().setScale(4.f);
            mapObjects.addPlacemark(point, provider, iconStyle);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.map_sublayers);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(CAMERA_TARGET, 16.0f, 0.0f, 45.0f));

        sublayerManager = mapView.getMap().getSublayerManager();
        mapObjects = mapView.getMap().getMapObjects();

        Circle circle = new Circle(CAMERA_TARGET, 100.f);
        mapObjects.addCircle(circle, Color.RED, 2.f, Color.WHITE);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(59.949911, 30.316560));
        points.add(new Point(59.949121, 30.316008));
        points.add(new Point(59.949441, 30.318132));
        points.add(new Point(59.950075, 30.316915));
        points.add(new Point(59.949911, 30.316560));
        Polygon polygon = new Polygon(new LinearRing(points), new ArrayList<LinearRing>());
        final PolygonMapObject polygonMapObject = mapObjects.addPolygon(polygon);
        polygonMapObject.setFillColor(0x3300FF00);
        polygonMapObject.setStrokeWidth(3.0f);
        polygonMapObject.setStrokeColor(Color.GREEN);

        // Example of changing the order of sublayers
        final Button switchSublayersOrder = findViewById(R.id.switch_sublayers_order);
        switchSublayersOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer buildingsSublayerIndex =
                        sublayerManager.findFirstOf(LayerNames.getBuildingsLayerName(), SublayerFeatureType.MODELS);
                if (buildingsSublayerIndex == null) {
                    Toast.makeText(getApplicationContext(),
                            "Buildings sublayer not found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Integer mapObjectGeometrySublayerIndex =
                        sublayerManager.findFirstOf(LayerNames.getMapObjectsLayerName(), SublayerFeatureType.GROUND);
                if (mapObjectGeometrySublayerIndex == null) {
                    Toast.makeText(getApplicationContext(),
                            "MapObject ground sublayer not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (buildingsSublayerIndex < mapObjectGeometrySublayerIndex) {
                    sublayerManager.moveAfter(buildingsSublayerIndex, mapObjectGeometrySublayerIndex);
                    switchSublayersOrder.setText(getString(R.string.sublayer_buildings_after_mo_geometry));
                } else {
                    sublayerManager.moveAfter(mapObjectGeometrySublayerIndex, buildingsSublayerIndex);
                    switchSublayersOrder.setText(getString(R.string.sublayer_buildings_before_mo_geometry));
                }
            }
        });

        // Example of conflict resolving
        Integer mapObjectPlacemarkSublayerIndex =
                sublayerManager.findFirstOf(LayerNames.getMapObjectsLayerName(), SublayerFeatureType.PLACEMARKS);
        if (mapObjectPlacemarkSublayerIndex != null) {
            Sublayer sublayer = sublayerManager.get(mapObjectPlacemarkSublayerIndex);

            // The placemarks from lower sublayers will be displaced in case of conflict
            sublayer.setModeAgainstPlacemarks(ConflictResolutionMode.MAJOR);

            // The labels from lower sublayers will be displaced in case of conflict
            sublayer.setModeAgainstLabels(ConflictResolutionMode.MAJOR);
        }

        // Client code must retain strong reference to the listener.
        mapView.getMap().addInputListener(inputListener);
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
}
