package com.mitchwongho.example.darksky.net;

import com.mitchwongho.example.darksky.domain.WeatherData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Defines the Darknet Rest API as a Retrofit interface
 */
public interface DarknetApi {

    @Headers({"Content-Type: application/json","Cache-Control: max-stale=3600"})
    @GET("{key}/{lat},{long}")
    Single<WeatherData> getForecast(@Path("key") String apiKey
            , @Path("lat") double latitude
            , @Path("long") double longitude
            , @Query("exclude") String excludeBlocks
            , @Query("units") String units);

    @Headers("Content-Type: application/json")
    @GET("{key}/{lat},{long},{time}")
    Single<Object> getTimeMachine(@Path("key") String apiKey
            , @Path("lat") double latitude
            , @Path("long") double longitude
            , @Path("time") long time
            , @Query("exclude") String excludeBlocks
            , @Query("units") String units);
}
