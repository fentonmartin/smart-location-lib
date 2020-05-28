package io.nlopez.smartlocation.util;

import android.content.Context;
import android.location.Location;

import io.nlopez.smartlocation.location.listener.LocationListener;
import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.util.LocationParams;
import io.nlopez.smartlocation.location.util.Logger;

/**
 * Created by mrm on 8/1/15.
 */
public class MockLocationListener implements LocationListener {

    private OnLocationUpdatedListener listener;

    @Override
    public void init(Context context, Logger logger) {

    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate) {
        this.listener = listener;
    }

    @Override
    public void stop() {

    }

    @Override
    public Location getLastLocation() {
        return null;
    }


    public void fakeEmitLocation(Location location) {
        listener.onLocationUpdated(location);
    }
}
