package io.nlopez.smartlocation.location.provider;

import android.support.annotation.NonNull;

import io.nlopez.smartlocation.location.listener.LocationListener;
import io.nlopez.smartlocation.location.listener.ServiceConnectionListener;
import io.nlopez.smartlocation.location.listener.ServiceLocationListener;

/**
 * A decorator for a {@link ServiceConnectionListener} used to execute the {@link
 * MultiFallbackListener}'s failover.
 *
 * @author abkaplan07
 */
class FallbackListenerWrapper implements ServiceConnectionListener {

    private final ServiceConnectionListener listener;
    private final MultiFallbackListener fallbackProvider;
    private final ServiceLocationListener childProvider;


    public FallbackListenerWrapper(@NonNull MultiFallbackListener parentProvider,
                                   ServiceLocationListener childProvider) {
        this.fallbackProvider = parentProvider;
        this.childProvider = childProvider;
        this.listener = childProvider.getServiceListener();
    }

    @Override
    public void onConnected() {
        if (listener != null) {
            listener.onConnected();
        }
    }

    @Override
    public void onConnectionSuspended() {
        if (listener != null) {
            listener.onConnectionSuspended();
        }
        runFallback();

    }

    @Override
    public void onConnectionFailed() {
        if (listener != null) {
            listener.onConnectionFailed();
        }
        runFallback();
    }

    private void runFallback() {
        LocationListener current = fallbackProvider.getCurrentProvider();
        if (current != null && current.equals(childProvider)) {
            fallbackProvider.fallbackProvider();
        }
    }
}
