package com.yandex.mapkitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.places.PlacesFactory;
import com.yandex.mapkit.places.panorama.NotFoundError;
import com.yandex.mapkit.places.panorama.PanoramaService;
import com.yandex.mapkit.places.panorama.PanoramaView;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

/**
 * This example shows how to find a panorama that is nearest to a given point and display it
 * in the PanoramaView object. User is not limited to viewing the panorama found and can
 * use arrows to navigate.
 * Note: Nearest panorama search API calls count towards MapKit daily usage limits.
 * Learn more at https://tech.yandex.ru/mapkit/doc/3.x/concepts/conditions-docpage/#conditions__limits
 */
public class PanoramaActivity extends Activity implements PanoramaService.SearchListener {
    /**
     * Replace "your_api_key" with a valid developer key.
     * You can get it at the https://developer.tech.yandex.ru/ website.
     */
    private final String MAPKIT_API_KEY = "your_api_key";
    private final Point SEARCH_LOCATION = new Point(55.733330, 37.587649);

    private PanoramaView panoramaView;
    private PanoramaService panoramaService;
    private PanoramaService.SearchSession searchSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        PlacesFactory.initialize(this);
        setContentView(R.layout.panorama);
        super.onCreate(savedInstanceState);
        panoramaView = (PanoramaView)findViewById(R.id.panoview);

        panoramaService = PlacesFactory.getInstance().createPanoramaService();
        searchSession = panoramaService.findNearest(SEARCH_LOCATION, this);
    }

    @Override
    protected void onStop() {
        panoramaView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        panoramaView.onStart();
    }

    @Override
    public void onPanoramaSearchResult(String panoramaId) {
        panoramaView.getPlayer().openPanorama(panoramaId);
        panoramaView.getPlayer().enableMove();
        panoramaView.getPlayer().enableRotation();
        panoramaView.getPlayer().enableZoom();
        panoramaView.getPlayer().enableMarkers();
    }

    @Override
    public void onPanoramaSearchError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof NotFoundError) {
            errorMessage = getString(R.string.not_found_error_message);
        } else if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
