package com.mitchwongho.example.darksky.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.mitchwongho.example.darksky.content.AppPreference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GeneralModule {

    private Application app;

    public GeneralModule(@NonNull final Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    @NonNull
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    @NonNull
    Context provideApplicationContext() {
        return app;
    }

    @Provides
    @Singleton
    AppPreference providesAppPreference(final Context context) {
        return new AppPreference(context);
    }

}
