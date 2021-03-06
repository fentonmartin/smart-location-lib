package io.nlopez.smartlocation;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.nlopez.smartlocation.location.listener.OnActivityUpdatedListener;
import io.nlopez.smartlocation.location.util.ActivityParams;
import io.nlopez.smartlocation.location.util.Logger;
import io.nlopez.smartlocation.util.MockActivityRecognitionListener;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class ActivityRecognitionControlTest {

    private static final ActivityParams DEFAULT_PARAMS = ActivityParams.NORMAL;

    private MockActivityRecognitionListener mockProvider;
    private OnActivityUpdatedListener activityUpdatedListener;

    @Before
    public void setup() {
        mockProvider = mock(MockActivityRecognitionListener.class);
        activityUpdatedListener = mock(OnActivityUpdatedListener.class);
    }

    @Test
    public void test_activity_recognition_control_init() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        LocationZ locationZ = new LocationZ.Builder(context).preInitialize(false).build();
        LocationZ.ActivityRecognitionControl activityRecognitionControl = locationZ.activity(mockProvider);

        verifyZeroInteractions(mockProvider);

        locationZ = new LocationZ.Builder(context).build();
        activityRecognitionControl = locationZ.activity(mockProvider);
        verify(mockProvider).init(eq(context), any(Logger.class));
    }

    @Test
    public void test_activity_recognition_control_start_defaults() {
        LocationZ.ActivityRecognitionControl activityRecognitionControl = createActivityRecognitionControl();

        activityRecognitionControl.start(activityUpdatedListener);
        verify(mockProvider).start(activityUpdatedListener, DEFAULT_PARAMS);
    }

    @Test
    public void test_activity_recognition_get_last_location() {
        LocationZ.ActivityRecognitionControl activityRecognitionControl = createActivityRecognitionControl();
        activityRecognitionControl.getLastActivity();

        verify(mockProvider).getLastActivity();
    }

    @Test
    public void test_activity_recognition_stop() {
        LocationZ.ActivityRecognitionControl activityRecognitionControl = createActivityRecognitionControl();
        activityRecognitionControl.stop();

        verify(mockProvider).stop();
    }

    private LocationZ.ActivityRecognitionControl createActivityRecognitionControl() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        LocationZ locationZ = new LocationZ.Builder(context).preInitialize(false).build();
        LocationZ.ActivityRecognitionControl activityRecognitionControl = locationZ.activity(mockProvider);
        return activityRecognitionControl;
    }

}
