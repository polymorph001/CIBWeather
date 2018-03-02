package com.mitchwongho.example.darksky.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Encapsulates weather data returned from the backend
 * @see "https://darksky.net/dev/docs"
 */
public class WeatherData implements Serializable {

    public double latitude;
    public double longitude;
    public String timezone;
    public DataBlock daily;

    public WeatherData() {
    }

    /**
     * A data block object represents the various weather phenomena occurring over a period of time
     * @see "https://darksky.net/dev/docs#data-block"
     */
    public class DataBlock implements Serializable {

        public List<DataPoint> data;
        public String summary;
        public String icon;
    }

    /**
     * A data point object contains various properties, each representing the average
     * (unless otherwise specified) of a particular weather phenomenon occurring during a period
     * of time: an instant in the case of currently, a minute for minutely, an hour for hourly,
     * and a day for daily.
     * @see "https://darksky.net/dev/docs#data-point"
     */
    public class DataPoint implements Serializable {

        public float apparentTemperatureHigh;
        public long apparentTemperatureHighTime;
        public float apparentTemperatureLow;
        public long apparentTemperatureLowTime;
        public float cloudCover; // 0.0-1.0
        public float dewPoint;
        public float humidity; // 0.00 - 1.00
        public String icon;
        public float moonPhase;
        public float ozone;
        public float precipAccumulation;
        public float precipIntensity;
        public float precipIntensityMax;
        public long precipIntensityMaxTime;
        public float precipProbability; // 0.0 - 1.0
        public String precipType;
        public float pressure; // 0.0 - 1.0
        public String summary;
        public long sunriseTime;
        public long sunsetTime;
        public float temperatureHigh;
        public long temperatureHighTime;
        public float temperatureLow;
        public long temperatureLowTime;
        public long time;
        public int uvIndex;
        public long uvIndexTime;
        public float windBearing;
        public float windGust;
        public float windSpeed;
    }
}
