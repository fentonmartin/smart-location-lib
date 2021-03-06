package io.nlopez.smartlocation.location.listener;

import android.content.Context;

import com.google.android.gms.location.DetectedActivity;

import io.nlopez.smartlocation.location.util.ActivityParams;
import io.nlopez.smartlocation.location.util.Logger;

/**
 * Created by mrm on 3/1/15.
 */
public interface ActivityListener {
    void init(Context context, Logger logger);

    void start(OnActivityUpdatedListener listener, ActivityParams params);

    void stop();

    DetectedActivity getLastActivity();
}
