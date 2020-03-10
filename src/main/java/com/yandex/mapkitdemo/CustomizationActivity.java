package com.yandex.mapkitdemo;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.mapview.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This is a basic example that displays a map and sets camera focus on the target location.
 * Note: When working on your projects, remember to request the required permissions.
 */
public class CustomizationActivity extends Activity {
    /**
     * Replace "your_api_key" with a valid developer key.
     * You can get it at the https://developer.tech.yandex.ru/ website.
     */
    private final String MAPKIT_API_KEY = "your_api_key";
    private final Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    private static final String TAG = "CustomizationActivity";

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Set the api key before calling initialize on MapKitFactory.
         * It is recommended to set api key in the Application.onCreate method,
         * but here we do it in each activity to make examples isolated.
         */
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        /**
         * Initialize the library to load required native libraries.
         * It is recommended to initialize the MapKit library in the Activity.onCreate method
         * Initializing in the Application.onCreate method may lead to extra calls and increased battery use.
         */
        MapKitFactory.initialize(this);
        // Now MapView can be created.
        setContentView(R.layout.map);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        Map map = mapView.getMap();
        map.setMapType(MapType.VECTOR_MAP);

        // Apply customization
        try {
            map.setMapStyle(style());
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to read customization style", e);
        }

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
        map.move(new CameraPosition(TARGET_LOCATION, 15.0f, 0.0f, 0.0f));
    }

    private String readRawResource(String name) throws IOException {
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
            Log.e(TAG, "Cannot read raw resource " + name);
            throw ex;
        }
        finally {
            reader.close();
        }

        return builder.toString();
    }

    private String style() throws IOException {
        return readRawResource("customization_example");
    }

    @Override
    protected void onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}
