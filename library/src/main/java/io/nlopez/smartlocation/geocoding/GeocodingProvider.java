package io.nlopez.smartlocation.geocoding;

import android.content.Context;
import android.location.Location;

import io.nlopez.smartlocation.listener.OnGeocodingListener;
import io.nlopez.smartlocation.listener.OnReverseGeocodingListener;
import io.nlopez.smartlocation.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeocodingProvider {
    void init(Context context, Logger logger);

    void addName(String name, int maxResults);

    void addLocation(Location location, int maxResults);

    void start(OnGeocodingListener geocodingListener, OnReverseGeocodingListener reverseGeocodingListener);

    void stop();

}
