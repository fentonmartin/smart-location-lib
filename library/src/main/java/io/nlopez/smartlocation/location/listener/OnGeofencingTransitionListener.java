package io.nlopez.smartlocation.location.listener;

import io.nlopez.smartlocation.location.util.TransitionGeofence;

/**
 * Created by mrm on 4/1/15.
 */
public interface OnGeofencingTransitionListener {
    void onGeofenceTransition(TransitionGeofence transitionGeofence);
}