package com.mitchwongho.example.darksky.domain;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Location {

    public static Location create(final double latitude, final double longitude, @NonNull String name) {
        return new AutoValue_Location(latitude, longitude, name);
    }

    public abstract double latitude();
    public abstract double longitude();
    public abstract String name();
}
