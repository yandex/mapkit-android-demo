package com.yandex.mapkitdemo;

import static com.yandex.mapkitdemo.ConstantsUtils.LOGO_URL;

import android.app.Activity;
import android.os.Bundle;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.TileId;
import com.yandex.mapkit.Version;
import com.yandex.mapkit.layers.Layer;
import com.yandex.mapkit.layers.LayerOptions;
import com.yandex.mapkit.layers.TileFormat;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.tiles.UrlProvider;
import com.yandex.mapkit.images.DefaultImageUrlProvider;
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
    private UrlProvider urlProvider;
    private DefaultImageUrlProvider imageUrlProvider;
    private Projection projection;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.initialize(this);
        setContentView(R.layout.custom_layer);
        super.onCreate(savedInstanceState);

        urlProvider = (tileId, version, features) -> LOGO_URL;
        imageUrlProvider = new DefaultImageUrlProvider();
        projection = Projections.getWgs84Mercator();

        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMap().setMapType(MapType.NONE);
        Layer l = mapView.getMap().addLayer(
                "mapkit_logo",
                TileFormat.PNG,
                new LayerOptions(),
                urlProvider,
                imageUrlProvider,
                projection);
        l.dataSourceLayer().invalidate("0.0.0");
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
