package io.nlopez.smartlocation.location.listener;

/**
 * An extension of the {@link LocationListener} interface for location providers that utilize 3rd
 * party services. Implementations must invoke the appropriate {@link ServiceConnectionListener}
 * events when the connection to the 3rd party service succeeds, fails, or is suspended.
 *
 * @author abkaplan07
 */
public interface ServiceLocationListener extends LocationListener {

    /**
     * Gets the {@link ServiceConnectionListener} callback for this location provider.
     */
    ServiceConnectionListener getServiceListener();

    /**
     * Set the {@link ServiceConnectionListener} used for callbacks from the 3rd party service.
     *
     * @param listener a <code>ServiceConnectionListener</code> to respond to connection events from
     *                 the underlying 3rd party location service.
     */
    void setServiceListener(ServiceConnectionListener listener);
}
