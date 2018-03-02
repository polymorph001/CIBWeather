package com.mitchwongho.example.darksky.net;


import com.mitchwongho.example.darksky.domain.Location;

import io.reactivex.Single;

/**
 * Defines the interface to query for weather data
 */
public interface WeatherEndpoint<T> {

    /**
     * Retrieve the 7-day forecast weather for the specified {@code location}
     * @param location the location of interest
     * @return an observable emitting the forecast weather
     */
    Single<T> getForecastData(final Location location);
}
