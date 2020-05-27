package io.nlopez.smartlocation.location;

import android.content.Context;
import android.location.Location;

import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface LocationProvider {
    void init(Context context, Logger logger);

    void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate);

    void stop();

    Location getLastLocation();

}
