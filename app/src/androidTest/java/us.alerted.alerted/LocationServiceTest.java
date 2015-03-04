package us.alerted.alerted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@LargeTest
public class LocationServiceTest extends ServiceTestCase<LocationService> {

    public static SharedPreferences sharedPref;

    public LocationServiceTest() {
        super(LocationService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
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

    @MediumTest
    public void testLocationOnChanged() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);
        assertNotNull(getService());
        Location location = new Location("");//provider name is unecessary
        location.setLatitude(1.0d);//your coords of course
        location.setLongitude(1.0d);
        getService().onLocationChanged(location);
    }

    public void testGetAlertTest() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);

        List<AlertGson> result = getService().getAlertFromApi("0.0", "1.0");

        //List<AlertGson> alerts = AlertGson.find(AlertGson.class, null, null, null, "cap_slug DESC", "1");
        //assertEquals(alerts.size(), 1);

        assertEquals(result.get(1).getCap_sender(), "w-nws.webmaster@noaa.gov");
        assertEquals(result.get(1).getCap_slug(), "712c963f91646a212f071571d611d8");

        InfoGson info = result.get(1).getInfo().get(0);
        assertEquals(info.getCap_category(), "Met");

        // First, delete all entries


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