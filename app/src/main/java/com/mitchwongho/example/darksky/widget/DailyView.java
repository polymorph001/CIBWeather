package com.mitchwongho.example.darksky.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mitchwongho.example.darksky.R;
import com.mitchwongho.example.darksky.app.MainApplication;
import com.mitchwongho.example.darksky.content.AppPreference;
import com.mitchwongho.example.darksky.domain.Location;
import com.mitchwongho.example.darksky.domain.WeatherData;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

/**
 * Maintains the View concerned with the Daily weather
 */

public final class DailyView extends FrameLayout {

    @Inject
    AppPreference prefs;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final PlaceAutocompleteFragment placeAutocompleteFragment;
    private final RecyclerView recyclerView;
    private final DataPointListAdapter adapter;
    private final BehaviorSubject<WeatherData> source;
    private final PublishSubject<ViewIntent> sink;
    private Disposable dispSource;

    /**
     * Constructor
     * @param context
     * @param location
     */
    public DailyView(@NonNull final Context context, @Nullable Location location) {
        super(context);
        MainApplication.appComponent.inject(this);

        final View layout = LayoutInflater.from(context).inflate(R.layout.activity_main, this, true);
        swipeRefreshLayout = (SwipeRefreshLayout)layout.findViewById(R.id.pullrefresh);
        recyclerView = layout.findViewById(R.id.recyclerView);
        placeAutocompleteFragment = (PlaceAutocompleteFragment)
                ((AppCompatActivity)context).getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if (location != null) {
            placeAutocompleteFragment.setText(location.name());
            final LatLng latLng = new LatLng(location.latitude(), location.longitude());
            placeAutocompleteFragment.setBoundsBias(LatLngBounds.builder().include(latLng).build());
        } else {
            placeAutocompleteFragment.setHint( context.getString(R.string.search_places) );
        }

        source = BehaviorSubject.create();
        sink = PublishSubject.create();

        adapter = new DataPointListAdapter(context);

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        //onNext
        dispSource = source.observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherData -> {
                    swipeRefreshLayout.setRefreshing(false); //may have been refreshing
                    // Only apply to adapter if there is data
                    if ( weatherData.daily != null
                            && weatherData.daily.data != null
                            && !weatherData.daily.data.isEmpty() ) {
                        adapter.apply(weatherData);
                    }
                }, throwable -> {
                    //onError
                    Snackbar.make(DailyView.this, R.string.oops, Snackbar.LENGTH_LONG).show();
                }, () -> {
                    //onCompleted - NOP
                });

        //
        // Listen for Place selection....emit that to the Activity to query for weather
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {


            @Override
            public void onPlaceSelected(@NonNull final Place place) {
                final LatLng latLng = place.getLatLng();
                final Location location = Location.create(latLng.latitude, latLng.longitude, place.getName().toString());
                swipeRefreshLayout.setRefreshing(true);
                sink.onNext(LocationSelectedIntent.create(location));
                placeAutocompleteFragment.setText( place.getName() );
            }

            @Override
            public void onError(@NonNull final Status status) {
                // handle error
                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(DailyView.this, status.getStatusMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        //
        // SwipeRefreshListener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final WeatherData lastWD = source.getValue();
            if (lastWD == null) {
                swipeRefreshLayout.setRefreshing(false);
            } else {
                sink.onNext(new RefreshLocationDataIntent());
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (dispSource != null
                && !dispSource.isDisposed()) {
            dispSource.dispose();
            dispSource = null;
        }
    }

    /**
     * Returns this component's event {@code Source} observer
     * @return
     */
    public final Observer<WeatherData> asObserver() {
        return source;
    }

    /**
     * Returns this component's event {@code Sink}
     * @return
     */
    public final Observable<ViewIntent> asObservable() {
        return sink.hide();
    }

    public interface ViewIntent{}

    /**
     * Intent from Selecting Location
     */
    public static class LocationSelectedIntent implements ViewIntent {
        public final Location location;

        private LocationSelectedIntent(@NonNull final Location location) {
            this.location = location;
        }

        public static LocationSelectedIntent create(@NonNull final Location location) {
            return new LocationSelectedIntent(location);
        }
    }

    /**
     * Intent from refresh Location Data
     */
    public static class RefreshLocationDataIntent implements ViewIntent {}


}
