package io.nlopez.smartlocation.location.provider;

import android.content.Context;
import android.location.Location;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.Collection;
import java.util.Iterator;

import io.nlopez.smartlocation.CustomTestRunner;
import io.nlopez.smartlocation.location.listener.LocationListener;
import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.listener.ServiceConnectionListener;
import io.nlopez.smartlocation.location.util.LocationParams;
import io.nlopez.smartlocation.location.util.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link MultiFallbackProvider}
 *
 * @author abkaplan07
 */
@RunWith(CustomTestRunner.class)
@Config(manifest=Config.NONE)
public class MultiFallbackProviderTest {

    @Test
    public void testDefaultBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().build();
        checkExpectedProviders(subject, LocationManagerProvider.class);
    }

    @Test
    public void testGoogleBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder()
                .withGooglePlayServicesProvider().build();
        checkExpectedProviders(subject, LocationProvider.class);
    }

    @Test
    public void testMultiProviderBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder()
                .withGooglePlayServicesProvider().withDefaultProvider().build();
        checkExpectedProviders(subject, LocationProvider.class,
                LocationManagerProvider.class);
    }

    @Test
    public void testMultiProviderRun() {
        TestServiceListener testServiceProvider = new TestServiceListener();
        ServiceConnectionListener mockListener = mock(ServiceConnectionListener.class);
        testServiceProvider.setServiceListener(mockListener);
        LocationListener backupProvider = mock(LocationListener.class);
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().withServiceProvider
                (testServiceProvider).withProvider(backupProvider).build();

        // Test initialization passes through to first provider
        subject.init(mock(Context.class), mock(Logger.class));
        assertEquals(1, testServiceProvider.getInitCount());

        // Test starting location updates passes through to first provider
        OnLocationUpdatedListener listenerMock = mock(OnLocationUpdatedListener.class);
        LocationParams paramsMock = mock(LocationParams.class);
        subject.start(listenerMock, paramsMock, false);
        assertEquals(1, testServiceProvider.getStartCount());

        // Test that falling back initializes and starts the backup provider
        testServiceProvider.simulateFailure();
        // Ensure that our 1st listener from the test service provider was invoked.
        verify(mockListener).onConnectionFailed();
        assertEquals(1, testServiceProvider.getStopCount());
        // Verify that the backup provider is initialized and started.
        verify(backupProvider).init(any(Context.class), any(Logger.class));
        verify(backupProvider).start(listenerMock, paramsMock, false);

        // Test that we're now using the fallback provider to stop.
        subject.stop();
        verify(backupProvider).stop();
        assertEquals(1, testServiceProvider.getStopCount());

        // Test that we're now using the fallback provider to get the last location
        Location mockLocation = mock(Location.class);
        when(backupProvider.getLastLocation()).thenReturn(mockLocation);
        assertEquals(mockLocation, subject.getLastLocation());
        assertEquals(0, testServiceProvider.getLastLocCount());
    }

    @SafeVarargs
    private final void checkExpectedProviders(MultiFallbackProvider subject, Class<? extends
            LocationListener>... expectedProviders) {
        Collection<LocationListener> providers = subject.getProviders();
        assertEquals(expectedProviders.length, providers.size());
        Iterator<LocationListener> providerIt = providers.iterator();
        for (int i = 0; i < expectedProviders.length; i++) {
            Class<? extends LocationListener> expected = expectedProviders[i];
            if (!providerIt.hasNext()) {
                fail("providers list did not have expected value " + expected.getName());
            }
            LocationListener actual = providerIt.next();
            assertTrue("provider instance class " + actual.getClass().getName() + " does not " +
                    "match expected value " + expected.getName(), actual.getClass()
                    .isAssignableFrom(expected));

        }
    }

}
