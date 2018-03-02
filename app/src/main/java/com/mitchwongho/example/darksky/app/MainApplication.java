package com.mitchwongho.example.darksky.app;

import android.app.Application;

import com.mitchwongho.example.darksky.BuildConfig;
import com.mitchwongho.example.darksky.di.AppComponent;
import com.mitchwongho.example.darksky.di.DaggerAppComponent;
import com.mitchwongho.example.darksky.di.GeneralModule;
import com.mitchwongho.example.darksky.di.NetworkModule;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

import timber.log.Timber;

public class MainApplication extends Application {

    // The DI DAG...remember to define your injection nodes
    public static AppComponent appComponent;
    public static File cacheDir;

    @Override
    public void onCreate() {
        super.onCreate();

        cacheDir = getCacheDir();

        appComponent = DaggerAppComponent.builder()
                .generalModule(new GeneralModule(this))
                .networkModule(new NetworkModule())
                .build();

        JodaTimeAndroid.init(this); //init for TZ

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO: 2018/03/01 Only log crashes
        }
    }
}
