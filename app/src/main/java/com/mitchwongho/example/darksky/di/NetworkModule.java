package com.mitchwongho.example.darksky.di;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mitchwongho.example.darksky.BuildConfig;
import com.mitchwongho.example.darksky.app.MainApplication;
import com.mitchwongho.example.darksky.domain.WeatherData;
import com.mitchwongho.example.darksky.net.DarknetEndpoint;
import com.mitchwongho.example.darksky.net.NullOnEmptyConverterFactory;
import com.mitchwongho.example.darksky.net.WeatherEndpoint;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module(includes = GeneralModule.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {

        final HttpLoggingInterceptor httpLoggingInterceptor =new HttpLoggingInterceptor(
                message -> Timber.d("OkHttp: %s", message)
        );
        httpLoggingInterceptor.setLevel( BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        final ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .allEnabledTlsVersions()
                .allEnabledCipherSuites()
                .build();

        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .cache(new Cache(MainApplication.cacheDir, 10 * 1024 * 1024)) //10MiB
                .readTimeout( 20, TimeUnit.SECONDS)
                .connectTimeout( 10, TimeUnit.SECONDS)
                .connectionSpecs(Collections.singletonList(spec)) //specify TLS spec
                .addInterceptor(httpLoggingInterceptor)
                .build();

        //ensures calls are synchronous
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        return new Retrofit.Builder()
                .baseUrl( BuildConfig.ApiUrl )
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(NullOnEmptyConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.trampoline()))
                .callbackExecutor(executorService)
                .build();


    }

    @Provides
    @Singleton
    public WeatherEndpoint<WeatherData> providesWeatherEndpoint(@NonNull final Retrofit retrofit, @NonNull final Context context) {
        return new DarknetEndpoint(retrofit, context);
    }
}
