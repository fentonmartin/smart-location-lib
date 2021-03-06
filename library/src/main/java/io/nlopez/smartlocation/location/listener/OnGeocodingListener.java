package io.nlopez.smartlocation.location.listener;

import java.util.List;

import io.nlopez.smartlocation.location.util.LocationAddress;

/**
 * Created by mrm on 4/1/15.
 */
public interface OnGeocodingListener {
    void onLocationResolved(String name, List<LocationAddress> results);
}