package com.yandex.mapkitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import java.util.concurrent.ExecutionException;

import com.yandex.mapkit.MapKitFactory;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView mapkitVersionTextView = (TextView)findViewById(R.id.mapkit_version);
        mapkitVersionTextView.setText(MapKitFactory.getInstance().getVersion());
    }
}
