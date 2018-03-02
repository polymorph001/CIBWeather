package com.mitchwongho.example.darksky.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mitchwongho.example.darksky.BuildConfig;
import com.mitchwongho.example.darksky.domain.Location;

/**
 * Wrapper around Shared Preferences
 */

public class AppPreference {

    private final Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private final static String KEY_LATITUDE = "KEY_LATITUDE";
    private final static String KEY_LONGITUDE = "KEY_LONGITUDE";
    private final static String KEY_LOCATION_NAME = "KEY_LOCATION_NAME";

    /**
     * Constructor
     * @param context
     */
    public AppPreference(@NonNull final Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void writeLocation(final double latitude, final double longitude, @NonNull final String locationName) {
        editor.putString(KEY_LATITUDE, Double.toString(latitude));
        editor.putString(KEY_LONGITUDE, Double.toString(longitude));
        editor.putString(KEY_LOCATION_NAME, locationName);
        editor.commit();
    }

    public @Nullable Location readLocation() throws Exception {
        if (hasLocation()) {
            final String lat = prefs.getString(KEY_LATITUDE, "0.0");
            final String lng = prefs.getString(KEY_LONGITUDE, "0.0");
            final String locationName = prefs.getString(KEY_LOCATION_NAME, "Unknown");
            return Location.create(Double.parseDouble(lat), Double.parseDouble(lng), locationName);
        } else {
            return null;
        }
    }

    /**
     * @return {@code true} if this preference store has location values
     */
    public boolean hasLocation() {
        return prefs.contains( KEY_LATITUDE )
                && prefs.contains( KEY_LONGITUDE )
                && prefs.contains( KEY_LOCATION_NAME );
    }

    public void clearLocation() {
        editor.remove(KEY_LONGITUDE)
                .remove(KEY_LATITUDE)
                .remove(KEY_LOCATION_NAME).commit();
    }
}
