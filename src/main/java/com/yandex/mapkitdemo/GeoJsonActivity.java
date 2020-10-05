package com.yandex.mapkitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RawTile;
import com.yandex.mapkit.TileId;
import com.yandex.mapkit.Version;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.geo.Projection;
import com.yandex.mapkit.geometry.geo.Projections;
import com.yandex.mapkit.geometry.geo.XYPoint;
import com.yandex.mapkit.layers.Layer;
import com.yandex.mapkit.layers.LayerOptions;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.resource_url_provider.ResourceUrlProvider;
import com.yandex.mapkit.tiles.TileProvider;
import com.yandex.mapkit.ZoomRange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This example shows how to add layer with simple objects such as points, polylines, polygons
 * to the map using GeoJSON format.
 */
public class GeoJsonActivity extends Activity {
    /**
     * Replace "your_api_key" with a valid developer key.
     * You can get it at the https://developer.tech.yandex.ru/ website.
     */
    private final String MAPKIT_API_KEY = "your_api_key";
    private final Point CAMERA_TARGET = new Point(59.952, 30.318);
    private final int MAX_ZOOM = 30;

    private static final String TAG = "GeoJsonActivity";

    private Projection projection;
    private ResourceUrlProvider urlProvider;
    private TileProvider tileProvider;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.geo_json);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        mapView.getMap().move(new CameraPosition(CAMERA_TARGET, 15.f, 0.f, 0.f));
        mapView.getMap().setMapType(MapType.VECTOR_MAP);

        // Client code must retain strong references to providers and projection
        projection = Projections.getWgs84Mercator();
        urlProvider = new ResourceUrlProvider() {
            @NonNull
            @Override
            public String formatUrl(@NonNull String s) {
                return String.format("https://raw.githubusercontent.com/yandex/mapkit-android-demo/master/src/main/%s", s);
            }
        };
        try {
            tileProvider = createTileProvider();
            createGeoJsonLayer();
        }
        catch (IOException ex) {
            Log.e(TAG, "Tile provider or GeoJSON layer not created", ex);
            return;
        }
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

    private void createGeoJsonLayer() throws IOException {
        Layer layer = mapView.getMap().addGeoJSONLayer(
                "geo_json_layer",
                style(),
                new LayerOptions().setNightModeAvailable(true),
                tileProvider,
                urlProvider,
                projection,
                new ArrayList<ZoomRange>());

        layer.invalidate("0");
    }

    private String getJsonResource(String name) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final int resourceIdentifier =
                getResources().getIdentifier(name,"raw", getPackageName());
        InputStream is = getResources().openRawResource(resourceIdentifier);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException ex) {
            reader.close();
            Log.e(TAG, "Cannot read JSON resource " + name);
            throw ex;
        }

        return builder.toString();
    }

    private String style() throws IOException {
        return getJsonResource("geo_json_style_example");
    }

    private TileProvider createTileProvider() throws IOException
    {
        final String jsonTemplate = getJsonResource("geo_json_example_template");

        return new TileProvider() {
            @NonNull
            @Override
            public RawTile load(@NonNull TileId tileId, @NonNull Version version, @NonNull String etag) {
                int tileSize = 1 << (MAX_ZOOM - tileId.getZ());

                int left = tileId.getX() * tileSize;
                int right = left + tileSize;
                int bottom = tileId.getY() * tileSize;
                int top = bottom + tileSize;

                Point leftBottom = projection.xyToWorld(new XYPoint(left, bottom), MAX_ZOOM);
                Point rightTop = projection.xyToWorld(new XYPoint(right, top), MAX_ZOOM);

                double tileLeft = leftBottom.getLongitude();
                double tileRight = rightTop.getLongitude();
                double tileBottom = leftBottom.getLatitude();
                double tileTop = rightTop.getLatitude();

                HashMap<String, Double> map = new HashMap<String, Double>();

                map.put("@POINT_X@", 0.7 * tileLeft   + 0.3 * tileRight);
                map.put("@POINT_Y@", 0.7 * tileBottom + 0.3 * tileTop);

                map.put("@LINE_X0@", 0.9 * tileLeft   + 0.1 * tileRight);
                map.put("@LINE_Y0@", 0.9 * tileBottom + 0.1 * tileTop);
                map.put("@LINE_X1@", 0.9 * tileLeft   + 0.1 * tileRight);
                map.put("@LINE_Y1@", 0.1 * tileBottom + 0.9 * tileTop);
                map.put("@LINE_X2@", 0.1 * tileLeft   + 0.9 * tileRight);
                map.put("@LINE_Y2@", 0.1 * tileBottom + 0.9 * tileTop);
                map.put("@LINE_X3@", 0.1 * tileLeft   + 0.9 * tileRight);
                map.put("@LINE_Y3@", 0.9 * tileBottom + 0.1 * tileTop);

                map.put("@POLYGON_X0@", 0.2 * tileLeft   + 0.8 * tileRight);
                map.put("@POLYGON_Y0@", 0.8 * tileBottom + 0.2 * tileTop);
                map.put("@POLYGON_X1@", 0.5 * tileLeft   + 0.5 * tileRight);
                map.put("@POLYGON_Y1@", 0.5 * tileBottom + 0.5 * tileTop);
                map.put("@POLYGON_X2@", 0.2 * tileLeft   + 0.8 * tileRight);
                map.put("@POLYGON_Y2@", 0.2 * tileBottom + 0.8 * tileTop);

                map.put("@TEXTURED_POLYGON_X0@", 0.8 * tileLeft   + 0.2 * tileRight);
                map.put("@TEXTURED_POLYGON_Y0@", 0.2 * tileBottom + 0.8 * tileTop);
                map.put("@TEXTURED_POLYGON_X1@", 0.2 * tileLeft   + 0.8 * tileRight);
                map.put("@TEXTURED_POLYGON_Y1@", 0.2 * tileBottom + 0.8 * tileTop);
                map.put("@TEXTURED_POLYGON_X2@", 0.5 * tileLeft   + 0.5 * tileRight);
                map.put("@TEXTURED_POLYGON_Y2@", 0.5 * tileBottom + 0.5 * tileTop);

                String json = jsonTemplate;
                for (Map.Entry<String, Double> entry : map.entrySet()) {
                    json = json.replace(entry.getKey(), String.valueOf(entry.getValue()));
                }

                return new RawTile(version, etag, RawTile.State.OK, json.getBytes());
            }
        };
    }
}
