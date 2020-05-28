package io.nlopez.smartlocation.location.util;

import android.content.Context;

import com.google.android.gms.location.DetectedActivity;

import io.nlopez.smartlocation.location.listener.OnActivityUpdatedListener;

/**
 * Created by mrm on 3/1/15.
 */
public interface ActivityProvider {
    void init(Context context, Logger logger);

    void start(OnActivityUpdatedListener listener, ActivityParams params);

    void stop();

    DetectedActivity getLastActivity();
}
