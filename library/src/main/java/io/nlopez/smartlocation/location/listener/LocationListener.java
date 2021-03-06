package io.nlopez.smartlocation.location.listener;

import android.content.Context;
import android.location.Location;

import io.nlopez.smartlocation.location.util.LocationParams;
import io.nlopez.smartlocation.location.util.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface LocationListener {
    void init(Context context, Logger logger);

    void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate);

    void stop();

    Location getLastLocation();

}
