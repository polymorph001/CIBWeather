package com.mitchwongho.example.darksky.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.mitchwongho.example.darksky.BuildConfig;
import com.mitchwongho.example.darksky.domain.Location;
import com.mitchwongho.example.darksky.domain.WeatherData;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class DarknetEndpoint implements WeatherEndpoint<WeatherData> {


    private final Retrofit retrofit;
    private final Context context;
    private final DarknetApi api;
    private ConnectivityManager cm;
    private String apiKey;

    private final static String EXCLUDE_DATA_BLOCKS = "currently,minutely,hourly,alerts,flags";
    private final static String UNITS_PREFERENCE = "auto"; //selected based on location

    /**
     * Constructor
     * @param retrofit
     * @param context
     */
    public DarknetEndpoint(@NonNull final  Retrofit retrofit, @NonNull final Context context) {
        this.context = context;
        this.retrofit = retrofit;
        this.api = retrofit.create(DarknetApi.class);
        cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        apiKey = BuildConfig.ApiKey;

    }

    /**
     * Completes with {@code True} if connected to the internet, else complets with an error.
     * @return a Single disposable
     */
    private Single<Boolean> checkNetworkStatus() {
        return Single.<Boolean>create(emitter -> {
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null
                    || !networkInfo.isConnected()) {
                emitter.onError(new NetworkUnavailableException());
            } else {
                emitter.onSuccess(Boolean.TRUE);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    @Override
    public Single<WeatherData> getForecastData(@NonNull final Location location) {

        // TODO: 2018/03/01 implement some LRU caching
//        return checkNetworkStatus()
        return Single.just(true)
                .observeOn(Schedulers.io())
                .flatMap(aBoolean -> api.getForecast(apiKey, location.latitude(), location.longitude(), EXCLUDE_DATA_BLOCKS, "ca"));
    }
}
