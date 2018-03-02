package com.mitchwongho.example.darksky.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.mitchwongho.example.darksky.R;
import com.mitchwongho.example.darksky.content.AppPreference;
import com.mitchwongho.example.darksky.domain.Location;

import javax.inject.Inject;

import timber.log.Timber;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final int playState = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (playState) {
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(this, playState, 0)
                        .show();
                break;
            case ConnectionResult.SUCCESS:

                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                break;
            default:
                // TODO: 2018/03/01 handle error
                break;

        }
    }
}
