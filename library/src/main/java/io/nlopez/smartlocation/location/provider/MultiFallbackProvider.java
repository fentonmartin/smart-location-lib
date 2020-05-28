package io.nlopez.smartlocation.location.provider;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import io.nlopez.smartlocation.location.listener.LocationListener;
import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.listener.ServiceConnectionListener;
import io.nlopez.smartlocation.location.listener.ServiceLocationListener;
import io.nlopez.smartlocation.location.util.LocationParams;
import io.nlopez.smartlocation.location.util.Logger;

/**
 * A {@link LocationListener} that allows multiple location services to be used. <br/><br/> New
 * instances of <code>MultiFallbackProvider</code> must be initialized via the Builder class:
 * <pre>
 * LocationProvider provider = new MultiLocationProvider.Builder()
 *         .withGooglePlayServicesProvider()
 *         .withDefaultProvider()
 *         .build();
 * </pre>
 * <code>MultiFallbackProvider</code> will attempt to use the location services in the order they
 * were added to the builder.  If the provider fails to connect to the underlying service, the next
 * provider in the list is used. <br/><br/> If no providers are added to the builder, the {@link
 * LocationManagerProvider} is used by default.
 *
 * @author abkaplan07
 */
public class MultiFallbackProvider implements LocationListener {

    private Queue<LocationListener> providers;
    private LocationListener currentProvider;
    private Context context;
    private Logger logger;
    private OnLocationUpdatedListener locationListener;
    private LocationParams locationParams;
    private boolean singleUpdate;
    private boolean shouldStart;


    MultiFallbackProvider() {
        this.providers = new LinkedList<>();
    }

    @Override
    public void init(Context context, Logger logger) {
        this.context = context;
        this.logger = logger;
        LocationListener current = getCurrentProvider();
        if (current != null) {
            current.init(context, logger);
        }

    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean
            singleUpdate) {
        this.shouldStart = true;
        this.locationListener = listener;
        this.locationParams = params;
        this.singleUpdate = singleUpdate;
        LocationListener current = getCurrentProvider();
        if (current != null) {
            current.start(listener, params, singleUpdate);
        }
    }

    @Override
    public void stop() {
        LocationListener current = getCurrentProvider();
        if (current != null) {
            current.stop();
        }

    }

    @Override
    public Location getLastLocation() {
        LocationListener current = getCurrentProvider();
        if (current == null) {
            return null;
        }
        return current.getLastLocation();
    }

    boolean addProvider(LocationListener provider) {
        return providers.add(provider);
    }

    Collection<LocationListener> getProviders() {
        return providers;
    }

    /**
     * Gets the current <code>LocationProvider</code> instance in use.
     *
     * @return the underlying <code>LocationProvider</code> used for location services.
     */
    LocationListener getCurrentProvider() {
        if (currentProvider == null && !providers.isEmpty()) {
            currentProvider = providers.poll();
        }
        return currentProvider;
    }

    /**
     * Fetches the next location provider in the fallback list, and initializes it. If location
     * updates have already been started, this restarts location updates.<br/><br/>If there are no
     * location providers left, no action occurs.
     */
    void fallbackProvider() {
        if (!providers.isEmpty()) {
            // Stop the current provider if it is running
            currentProvider.stop();
            // Fetch the next provider in the list.
            currentProvider = providers.poll();
            currentProvider.init(context, logger);
            if (shouldStart) {
                currentProvider.start(locationListener, locationParams, singleUpdate);
            }
        }
    }

    /**
     * Builder class for the {@link MultiFallbackProvider}.
     */
    public static class Builder {

        private MultiFallbackProvider builtProvider;

        public Builder() {
            this.builtProvider = new MultiFallbackProvider();
        }

        /**
         * Adds Google Location Services as a provider.
         */
        public Builder withGooglePlayServicesProvider() {
            return withServiceProvider(new LocationGooglePlayServicesProvider());
        }

        /**
         * Adds the built-in Android Location Manager as a provider.
         */
        public Builder withDefaultProvider() {
            return withProvider(new LocationManagerProvider());
        }

        /**
         * Adds the given {@link ServiceLocationListener} as a location provider. If the given
         * location provider detects that its underlying service is not available, the built
         * <code>MultiFallbackProvider</code> will fall back to the next location provider in the
         * list.
         *
         * @param provider a <code>ServiceLocationProvider</code> that can detect if the underlying
         *                 location service is not available.
         */
        public Builder withServiceProvider(ServiceLocationListener provider) {
            FallbackListenerWrapper fallbackListener = new FallbackListenerWrapper(builtProvider,
                    provider);
            provider.setServiceListener(fallbackListener);
            return withProvider(provider);
        }

        /**
         * Adds the given {@link LocationListener} as a provider. Note that these providers
         * <strong>DO NOT</strong> support fallback behavior.
         *
         * @param provider a <code>LocationProvider</code> instance.
         */
        public Builder withProvider(LocationListener provider) {
            builtProvider.addProvider(provider);
            return this;
        }

        /**
         * Builds a {@link MultiFallbackProvider} instance. If no providers were added to the
         * builder, the built-in Android Location Manager is used.
         */
        public MultiFallbackProvider build() {
            // Always ensure we have the default provider
            if (builtProvider.providers.isEmpty()) {
                withDefaultProvider();
            }
            return builtProvider;
        }
    }

    static class FallbackListenerWrapper implements ServiceConnectionListener {

        private final ServiceConnectionListener listener;
        private final MultiFallbackProvider fallbackProvider;
        private final ServiceLocationListener childProvider;


        public FallbackListenerWrapper(@NonNull MultiFallbackProvider parentProvider,
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
}
