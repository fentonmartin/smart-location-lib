package io.nlopez.smartlocation.location.provider;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import io.nlopez.smartlocation.location.listener.GooglePlayServicesListener;
import io.nlopez.smartlocation.location.listener.LocationListener;
import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.util.LocationParams;
import io.nlopez.smartlocation.location.util.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public class LocationFallbackProvider implements LocationListener, GooglePlayServicesListener {

    private Logger logger;
    private OnLocationUpdatedListener listener;
    private boolean shouldStart = false;
    private Context context;
    private LocationParams params;
    private boolean singleUpdate = false;

    private LocationListener provider;

    public LocationFallbackProvider(Context context) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            provider = new LocationGooglePlayServicesProvider(this);
        } else {
            provider = new LocationManagerProvider();
        }
    }

    @Override
    public void init(Context context, Logger logger) {
        this.logger = logger;
        this.context = context;

        logger.d("Currently selected provider = " + provider.getClass().getSimpleName());

        provider.init(context, logger);
    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate) {
        shouldStart = true;
        this.listener = listener;
        this.params = params;
        this.singleUpdate = singleUpdate;
        provider.start(listener, params, singleUpdate);
    }

    @Override
    public void stop() {
        provider.stop();
        shouldStart = false;
    }

    @Override
    public Location getLastLocation() {
        return provider.getLastLocation();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Nothing to do here
    }

    @Override
    public void onConnectionSuspended(int i) {
        fallbackToLocationManager();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        fallbackToLocationManager();
    }

    private void fallbackToLocationManager() {
        logger.d("FusedLocationProvider not working, falling back and using LocationManager");
        provider = new LocationManagerProvider();
        provider.init(context, logger);
        if (shouldStart) {
            provider.start(listener, params, singleUpdate);
        }
    }
}
