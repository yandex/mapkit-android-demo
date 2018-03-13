package com.yandex.mapkitdemo;

import android.app.Activity;
import android.os.Bundle;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.TileId;
import com.yandex.mapkit.Version;
import com.yandex.mapkit.layers.Layer;
import com.yandex.mapkit.layers.LayerOptions;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.tiles.UrlProvider;
import com.yandex.mapkit.resource_url_provider.DefaultUrlProvider;
import com.yandex.mapkit.geometry.geo.Projection;
import com.yandex.mapkit.geometry.geo.Projections;
import com.yandex.mapkit.mapview.MapView;

/**
 * This example shows how to add a user-defined layer to the map.
 * We use the UrlProvider class to format requests to a remote server that renders
 * tiles. For simplicity, we ignore map coordinates and zoom here, and
 * just provide a URL for the static image.
 */
public class CustomLayerActivity extends Activity {
    /**
     * Replace "your_api_key" with a valid developer key.
     * You can get it at the https://developer.tech.yandex.ru/ website.
     */
    private final String MAPKIT_API_KEY = "your_api_key";

    private UrlProvider urlProvider;
    private DefaultUrlProvider resourceUrlProvider;
    private Projection projection;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.custom_layer);
        super.onCreate(savedInstanceState);

        urlProvider = new UrlProvider() {
            @Override
            public String formatUrl(TileId tileId, Version version) {
                return "https://maps-ios-pods-public.s3.yandex.net/mapkit_logo.png";
            }
        };
        resourceUrlProvider = new DefaultUrlProvider();
        projection = Projections.createWgs84Mercator();

        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMap().setMapType(MapType.NONE);
        Layer l = mapView.getMap().addLayer(
                "mapkit_logo",
                "image/png",
                new LayerOptions(),
                urlProvider,
                resourceUrlProvider,
                projection);
        l.invalidate("0.0.0");
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
