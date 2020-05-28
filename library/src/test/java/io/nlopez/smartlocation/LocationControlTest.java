package io.nlopez.smartlocation;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.utils.LocationParams;
import io.nlopez.smartlocation.location.utils.Logger;
import io.nlopez.smartlocation.util.MockLocationProvider;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class LocationControlTest {

    private static final LocationParams DEFAULT_PARAMS = LocationParams.BEST_EFFORT;
    private static final boolean DEFAULT_SINGLE_UPDATE = false;

    private MockLocationProvider mockProvider;
    private OnLocationUpdatedListener locationUpdatedListener;

    @Before
    public void setup() {
        mockProvider = mock(MockLocationProvider.class);
        locationUpdatedListener = mock(OnLocationUpdatedListener.class);
    }

    @Test
    public void test_location_control_init() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        LocationZ locationZ = new LocationZ.Builder(context).logging(false).preInitialize(false).build();
        LocationZ.LocationControl locationControl = locationZ.location(mockProvider);
        verifyZeroInteractions(mockProvider);

        locationZ = new LocationZ.Builder(context).logging(false).build();
        locationControl = locationZ.location(mockProvider);
        verify(mockProvider).init(eq(context), any(Logger.class));
    }

    @Test
    public void test_location_control_start_defaults() {
        LocationZ.LocationControl locationControl = createLocationControl();

        locationControl.start(locationUpdatedListener);
        verify(mockProvider).start(locationUpdatedListener, DEFAULT_PARAMS, DEFAULT_SINGLE_UPDATE);
    }

    @Test
    public void test_location_control_start_only_once() {
        LocationZ.LocationControl locationControl = createLocationControl();
        locationControl.oneFix();

        locationControl.start(locationUpdatedListener);
        verify(mockProvider).start(locationUpdatedListener, DEFAULT_PARAMS, true);
    }

    @Test
    public void test_location_control_start_continuous() {
        LocationZ.LocationControl locationControl = createLocationControl();
        locationControl.oneFix();
        locationControl.continuous();
        locationControl.start(locationUpdatedListener);
        verify(mockProvider).start(locationUpdatedListener, DEFAULT_PARAMS, false);
    }

    @Test
    public void test_location_control_start_navigation() {
        LocationZ.LocationControl locationControl = createLocationControl();
        locationControl.config(LocationParams.NAVIGATION);

        locationControl.start(locationUpdatedListener);
        verify(mockProvider).start(eq(locationUpdatedListener), eq(LocationParams.NAVIGATION), anyBoolean());
    }

    @Test
    public void test_location_control_get_last_location() {
        LocationZ.LocationControl locationControl = createLocationControl();
        locationControl.getLastLocation();

        verify(mockProvider).getLastLocation();
    }

    @Test
    public void test_location_control_stop() {
        LocationZ.LocationControl locationControl = createLocationControl();
        locationControl.stop();

        verify(mockProvider).stop();
    }

    private LocationZ.LocationControl createLocationControl() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        LocationZ locationZ = new LocationZ.Builder(context).logging(false).preInitialize(false).build();
        LocationZ.LocationControl locationControl = locationZ.location(mockProvider);
        return locationControl;
    }

}
