package com.mitchwongho.example.darksky.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mitchwongho.example.darksky.R;
import com.mitchwongho.example.darksky.domain.WeatherData;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Maintains a list for a RecyclerView
 */

public final class DataPointListAdapter extends Adapter<DataPointListAdapter.ItemViewHolder> {

    static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMM YYYY");

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView date, minTemp, maxTemp, summary;
        final TextView windspeed, humidity, sunrise, sunset;
        final ViewGroup detailsGroup;

        ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.text_date);
            summary = itemView.findViewById(R.id.text_summary);
            minTemp = itemView.findViewById(R.id.text_min_temp);
            maxTemp = itemView.findViewById(R.id.text_max_temp);
            windspeed = itemView.findViewById(R.id.details_windspeed);
            humidity = itemView.findViewById(R.id.details_humidity);
            sunrise = itemView.findViewById(R.id.details_sunrise);
            sunset = itemView.findViewById(R.id.details_sunset);
            detailsGroup = (ConstraintLayout) itemView.findViewById(R.id.constraint_details);
        }

        void apply(@NonNull final WeatherData.DataPoint dp, @NonNull final DateTimeZone dtz) {
            Timber.d("Apply ViewHolder {time=%d,min=%.2f,max=%.2f}", dp.time, dp.temperatureLow, dp.temperatureHigh);
            date.setText(dtf.print(dp.time * 1000L));
            summary.setText(dp.summary);
            // FIXME: 2018/03/01 add to strings.xml
            minTemp.setText(String.format(Locale.getDefault(), "min. %.0fº", dp.temperatureLow));
            maxTemp.setText(String.format(Locale.getDefault(), "max. %.0fº", dp.temperatureHigh));
            windspeed.setText(String.format(Locale.getDefault(), "Windspeed %.0fkm/h", dp.windSpeed));
            humidity.setText(String.format(Locale.getDefault(), "Humidity %.0f%%", dp.humidity));
            // Format time
            final String fmtSunrise = new LocalTime(dp.sunriseTime * 1000L, dtz).toString("HH:mm");
            final String fmtSunset = new LocalTime(dp.sunsetTime * 1000L, dtz).toString("HH:mm");
            sunset.setText(String.format(Locale.getDefault(), "Sunset %s", fmtSunset));
            sunrise.setText(String.format(Locale.getDefault(), "Sunrise %s", fmtSunrise));
        }
    }

    private int indexExpandedVH = -1; //maintains the index of the expanded ViewHolder
    private WeakReference<Context> weakContext;
    private DateTimeZone dtz = null;
    private List<WeatherData.DataPoint> data = new ArrayList<>();

    public DataPointListAdapter(@NonNull final Context context) {
        weakContext = new WeakReference<>(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final Context ctx = weakContext.get();
        if (ctx == null) {
            return null;
        } else {
            final View layout = LayoutInflater.from(ctx).inflate(R.layout.view_daily_item_card, parent, false);
            return new ItemViewHolder(layout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final WeatherData.DataPoint dp = data.get(position);
        final boolean isExpanded = indexExpandedVH == position;
        holder.itemView.setActivated(isExpanded);
        holder.detailsGroup.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.apply(dp, dtz);
        holder.itemView.setOnClickListener(view -> {
            indexExpandedVH = isExpanded ? -1 : position; //set the index of the expanded VH else reset
            DataPointListAdapter.this.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Apply the specified {@code data} to this adapter
     *
     * @param {@code WeatherData}
     */
    public final void apply(@NonNull final WeatherData data) {
        this.data.clear();
        this.data.addAll(data.daily.data);
        indexExpandedVH = -1;

        TimeZone tz = TimeZone.getTimeZone(data.timezone);
        dtz = DateTimeZone.forTimeZone(tz);
        super.notifyDataSetChanged();
    }
}
