package us.alerted.alerted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import java.util.List;

@LargeTest
public class LocationServiceTest extends ServiceTestCase<LocationService> {

    public static SharedPreferences sharedPref;
    private LocationService mReceiver = new LocationService();

    public LocationServiceTest() {
        super(LocationService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Clear shared preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();

        // Delete all items in the Alert DB
        Alert.deleteAll(Alert.class);
    }

    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);
        assertNotNull(getService());
    }

    @MediumTest
    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        IBinder service = bindService(startIntent);
        assertNotNull(service);
    }

    public void testOnConnected() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);

        assertNotNull(getService());
        Location location = new Location("");//provider name is unecessary
        location.setLatitude(1.0d);//your coords of course
        location.setLongitude(1.0d);

        //getService().onConnected(null);

    }

    @MediumTest
    public void testOnLocationChanged() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);
        assertNotNull(getService());

        Location location = new Location("");//provider name is unecessary
        location.setLatitude(0.0d);//your coords of course
        location.setLongitude(1.0d);
        getService().onLocationChanged(location);


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Execute some code after 3 seconds have passed (for async task to complete)
        List<Alert> alerts = Alert.listAll(Alert.class);
        assertEquals(2, alerts.size());
    }

    public void testSaveAlertsToDB() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);
        assertNotNull(getService());

        List<AlertGson> result = getService().getAlertFromApi("0.0", "1.0", "2014-02-17T08:03:21.156421");

        // Save multiple alerts
        Boolean saveResult = getService().saveAlertsToDB(result);
        assertTrue(saveResult);
    }



    public void testGetLastCheckDate() {
        String d = mReceiver.getLastCheckDate();
        assertNotNull(d);
    }

    public void testGetSetLastCheckDate() {
        String a = mReceiver.setLastCheckDate();
        String d = mReceiver.getLastCheckDate();
        assertEquals(a, d);
    }

    public void testGetAlertTest() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);
        assertNotNull(getService());

        List<AlertGson> result = getService().getAlertFromApi("0.0", "1.0", "2014-02-17T08:03:21.156421");

        //List<AlertGson> alerts = AlertGson.find(AlertGson.class, null, null, null, "cap_slug DESC", "1");
        //assertEquals(alerts.size(), 1);

        assertEquals(result.get(1).getCap_sender(), "w-nws.webmaster@noaa.gov");
        assertEquals(result.get(1).getCap_slug(), "712c963f91646a212f071571d611d8");

        InfoGson info = result.get(1).getInfo().get(0);
        assertEquals(info.getCap_category(), "Met");

        // Save result #1
        Boolean saveResult = getService().saveAlertToDB(result.get(0));
        assertTrue(saveResult);

        // Save result #2 (shouldn't save)
        Boolean saveResult2 = getService().saveAlertToDB(result.get(0));
        assertFalse(saveResult2);

        // Finally, count the number of entries.
        List<Alert> alerts = Alert.listAll(Alert.class);
        assertEquals(1, alerts.size());
    }
}