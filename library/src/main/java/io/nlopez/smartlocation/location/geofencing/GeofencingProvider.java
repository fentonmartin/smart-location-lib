package io.nlopez.smartlocation.location.geofencing;

import android.content.Context;

import java.util.List;

import io.nlopez.smartlocation.location.geofencing.model.GeofenceModel;
import io.nlopez.smartlocation.location.listener.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.location.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeofencingProvider {
    void init(Context context, Logger logger);

    void start(OnGeofencingTransitionListener listener);

    void addGeofence(GeofenceModel geofence);

    void addGeofences(List<GeofenceModel> geofenceList);

    void removeGeofence(String geofenceId);

    void removeGeofences(List<String> geofenceIds);

    void stop();

}
