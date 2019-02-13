package com.yandex.mapkitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RawTile;
import com.yandex.mapkit.TileId;
import com.yandex.mapkit.Version;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.geo.Projection;
import com.yandex.mapkit.geometry.geo.Projections;
import com.yandex.mapkit.layers.Layer;
import com.yandex.mapkit.layers.LayerOptions;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.resource_url_provider.ResourceUrlProvider;
import com.yandex.mapkit.tiles.TileProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

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

    private Logger LOGGER = Logger.getLogger("mapkitdemo.geojson");
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.geo_json);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        mapView.getMap().move(
                new CameraPosition(CAMERA_TARGET, 15.0f, 0.0f, 0.0f));
        mapView.getMap().setMapType(MapType.VECTOR_MAP);

        createGeoJsonLayer();
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

    private void createGeoJsonLayer() {

        final ResourceUrlProvider urlProvider = new ResourceUrlProvider() {
            @NonNull
            @Override
            public String formatUrl(@NonNull String s) {
                return String.format("https://raw.githubusercontent.com/yandex/mapkit-android-demo/master/src/main/%s", s);
            }
        };

        TileProvider tileProvider;
        try {
            tileProvider = createTileProvider();
        }
        catch (IOException ex) {
            LOGGER.severe("Tile provider not created: cancel creation of geo json layer");
            return;
        }

        final Projection projection = Projections.createWgs84Mercator();

        Layer layer = mapView.getMap().addLayer(
                "geo_json_layer",
                "application/geo-json",
                new LayerOptions(),
                tileProvider,
                urlProvider,
                projection);

        layer.invalidate("0.0.0");
    }

    private TileProvider createTileProvider() throws IOException
    {
        final StringBuilder builder = new StringBuilder();
        final int resourceIdentifier =
                getResources().getIdentifier("geo_json_example","raw", getPackageName());
        InputStream is = getResources().openRawResource(resourceIdentifier);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception ex) {
            reader.close();
            LOGGER.severe("Cannot read GeoJSON file");
            throw ex;
        }

        final String rawJson = builder.toString();
        return new TileProvider() {
            @NonNull
            @Override
            public RawTile load(@NonNull TileId tileId, @NonNull Version version, @NonNull String etag) {
                return new RawTile(version, etag, RawTile.State.OK, rawJson.getBytes());
            }
        };
    }
}
