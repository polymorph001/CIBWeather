package com.mitchwongho.example.darksky.di;

import android.support.annotation.NonNull;

import com.mitchwongho.example.darksky.app.MainActivity;
import com.mitchwongho.example.darksky.widget.DailyView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {GeneralModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(@NonNull final MainActivity component);
    void inject(@NonNull final DailyView component);
}
