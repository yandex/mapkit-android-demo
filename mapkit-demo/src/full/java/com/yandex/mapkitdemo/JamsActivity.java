package com.yandex.mapkitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.traffic.TrafficLayer;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageButton;

import com.yandex.mapkit.traffic.TrafficLevel;
import com.yandex.mapkit.traffic.TrafficListener;

public class JamsActivity extends Activity implements TrafficListener {
    private TextView levelText;
    private ImageButton levelIcon;
    private TrafficLevel trafficLevel = null;
    private enum TrafficFreshness {Loading, OK, Expired};
    private TrafficFreshness trafficFreshness;
    private MapView mapView;
    private TrafficLayer traffic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.jams);
        super.onCreate(savedInstanceState);

        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(new Point(59.945933, 30.320045), 14.0f, 0.0f, 0.0f));

        levelText = findViewById(R.id.traffic_light_text);
        levelIcon = findViewById(R.id.traffic_light);
        traffic = MapKitFactory.getInstance().createTrafficLayer(mapView.getMapWindow());
        traffic.setTrafficVisible(true);
        traffic.addTrafficListener(this);
        updateLevel();
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

    private void updateLevel() {
        int iconId;
        String level = "";
        if (!traffic.isTrafficVisible()) {
            iconId = R.drawable.icon_traffic_light_dark;
        } else if (trafficFreshness == TrafficFreshness.Loading) {
            iconId = R.drawable.icon_traffic_light_violet;
        } else if (trafficFreshness == TrafficFreshness.Expired) {
            iconId = R.drawable.icon_traffic_light_blue;
        } else if (trafficLevel == null) {  // state is fresh but region has no data
            iconId = R.drawable.icon_traffic_light_grey;
        } else {
            switch (trafficLevel.getColor()) {
                case RED: iconId = R.drawable.icon_traffic_light_red; break;
                case GREEN: iconId = R.drawable.icon_traffic_light_green; break;
                case YELLOW: iconId = R.drawable.icon_traffic_light_yellow; break;
                default: iconId = R.drawable.icon_traffic_light_grey; break;
            }
            level = Integer.toString(trafficLevel.getLevel());
        }
        levelIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), iconId));
        levelText.setText(level);
    }

    public void onLightClick(View view) {
        traffic.setTrafficVisible(!traffic.isTrafficVisible());
        updateLevel();
    }

    public void onClickBack(View view) {
        finish();
    }

    @Override
    public void onTrafficChanged(TrafficLevel trafficLevel) {
        this.trafficLevel = trafficLevel;
        this.trafficFreshness = TrafficFreshness.OK;
        updateLevel();
    }

    @Override
    public void onTrafficLoading() {
        this.trafficLevel = null;
        this.trafficFreshness = TrafficFreshness.Loading;
        updateLevel();
    }

    @Override
    public void onTrafficExpired() {
        this.trafficLevel = null;
        this.trafficFreshness = TrafficFreshness.Expired;
        updateLevel();
    }
}
