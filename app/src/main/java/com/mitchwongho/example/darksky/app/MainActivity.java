package com.mitchwongho.example.darksky.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.mitchwongho.example.darksky.R;
import com.mitchwongho.example.darksky.content.AppPreference;
import com.mitchwongho.example.darksky.domain.Location;
import com.mitchwongho.example.darksky.domain.WeatherData;
import com.mitchwongho.example.darksky.net.WeatherEndpoint;
import com.mitchwongho.example.darksky.widget.DailyView;

import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private DailyView dailyView;

    @Inject
    WeatherEndpoint<WeatherData> endpoint;
    @Inject
    AppPreference prefs;

    private Disposable disposableGetWeather;
    private Disposable disposableDailyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.appComponent.inject(this); // inject this component
        // init view
        Location location;
        ;
        try {
            // init view
            location = prefs.readLocation();
        } catch (Throwable t) {
            location = null;
        }
        dailyView = new DailyView(this, location);
        setContentView(dailyView);

        if (location != null) {
            queryWeather(location);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        disposableDailyView = dailyView.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewIntent -> {
                    if (viewIntent instanceof DailyView.LocationSelectedIntent) {
                        final DailyView.LocationSelectedIntent intent = (DailyView.LocationSelectedIntent) viewIntent;
                        final Location location = intent.location;
                        prefs.writeLocation(location.latitude(), location.longitude(), location.name());
                        queryWeather(location);
                    } else if (viewIntent instanceof DailyView.RefreshLocationDataIntent) {
                        final Location location = prefs.readLocation();
                        queryWeather(location);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposableDailyView != null
                && !disposableDailyView.isDisposed()) {
            disposableDailyView.dispose();
            disposableDailyView = null;
        }

        if (disposableGetWeather != null
                && !disposableGetWeather.isDisposed()) {
            disposableGetWeather.dispose();
            disposableGetWeather = null;
        }
    }

    /**
     * Query the Weather Service
     *
     * @param location
     */
    private void queryWeather(@NonNull final Location location) {
        disposableGetWeather = endpoint.getForecastData(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((weatherData, throwable) -> {
                    if (throwable == null) {
                        //Success
                        Timber.d("Successfully retrieved Forecast data {hasData=%B,size=%d}", weatherData.daily != null, (weatherData.daily != null) ? weatherData.daily.data.size() : 0);
                        dailyView.asObserver().onNext(weatherData);
                    } else {
                        // Unsuccessful - do something here
                        throwable.printStackTrace();
                        Snackbar.make(dailyView, R.string.network_error_weather, Snackbar.LENGTH_INDEFINITE).show();
                        dailyView.asObserver().onNext( new WeatherData()) ;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
