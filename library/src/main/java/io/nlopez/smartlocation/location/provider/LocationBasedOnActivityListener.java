package io.nlopez.smartlocation.location.provider;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.DetectedActivity;

import io.nlopez.smartlocation.location.listener.LocationListener;
import io.nlopez.smartlocation.location.listener.OnActivityUpdatedListener;
import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.util.ActivityParams;
import io.nlopez.smartlocation.location.util.LocationParams;
import io.nlopez.smartlocation.location.util.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public class LocationBasedOnActivityListener implements LocationListener, OnActivityUpdatedListener {
    private final ActivityGooglePlayServicesProvider activityProvider;
    private final LocationGooglePlayServicesListener locationProvider;
    private final LocationBasedOnActivityListener locationBasedOnActivityListener;
    private OnLocationUpdatedListener locationUpdatedListener;
    private LocationParams locationParams;

    public LocationBasedOnActivityListener(@NonNull LocationBasedOnActivityListener locationBasedOnActivityListener) {
        activityProvider = new ActivityGooglePlayServicesProvider();
        locationProvider = new LocationGooglePlayServicesListener();
        this.locationBasedOnActivityListener = locationBasedOnActivityListener;
    }

    @Override
    public void init(Context context, Logger logger) {
        locationProvider.init(context, logger);
        activityProvider.init(context, logger);
    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate) {
        if (singleUpdate) {
            throw new IllegalArgumentException("singleUpdate cannot be set to true");
        }
        locationProvider.start(listener, params, false);
        activityProvider.start(this, ActivityParams.NORMAL);
        this.locationParams = params;
        this.locationUpdatedListener = listener;
    }

    @Override
    public void stop() {
        locationProvider.stop();
        activityProvider.stop();
    }

    @Override
    public Location getLastLocation() {
        return locationProvider.getLastLocation();
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        LocationParams params = locationBasedOnActivityListener.locationParamsForActivity(detectedActivity);
        if (params != null && locationParams != null && !locationParams.equals(params)) {
            start(locationUpdatedListener, params, false);
        }
    }

    public interface LocationBasedOnActivityListener {
        public LocationParams locationParamsForActivity(DetectedActivity detectedActivity);
    }
}
