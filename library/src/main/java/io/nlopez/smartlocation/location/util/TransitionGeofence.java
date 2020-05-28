package io.nlopez.smartlocation.location.util;

/**
 * Wraps Geofences and Transitions
 */
public class TransitionGeofence {
    private GeofenceModel geofenceModel;
    private int transitionType;

    public TransitionGeofence(GeofenceModel geofence, int transitionType) {
        this.geofenceModel = geofence;
        this.transitionType = transitionType;
    }

    public GeofenceModel getGeofenceModel() {
        return geofenceModel;
    }

    public int getTransitionType() {
        return transitionType;
    }
}
