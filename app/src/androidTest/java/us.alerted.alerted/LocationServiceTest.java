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
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

@LargeTest
public class LocationServiceTest extends ServiceTestCase<LocationService> {

    public static SharedPreferences sharedPref;
    private LocationService.SubmitCrdTask mSubmitCrdTask = null;
    final CountDownLatch signal = new CountDownLatch(1);

    public LocationServiceTest() {
        super(LocationService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
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

    public void atestRunAsyncSubmit() {
        LocationService locationService = new LocationService();
        //LocationService.SubmitCrdTask mSubmitCrdTask = locationService.new SubmitCrdTask();
        //mSubmitCrdTask.execute((Void) null);
        Location location = new Location("");//provider name is unecessary
        location.setLatitude(1.0d);//your coords of course
        location.setLongitude(1.0d);
        locationService.onLocationChanged(location);
        assertTrue(true);
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


    public void testSubmitLocation() {

        LocationService locationService = new LocationService();

        JSONObject postData = new JSONObject();
        JSONObject geom = new JSONObject();
        Location location = new Location("");//provider name is unecessary
        location.setLatitude(1.0d);//your coords of course
        location.setLongitude(1.0d);
        try {
            geom.put("type", "Point");
            geom.put("coordinates", Utils.format(location));
            postData.put("name","Current Location");
            postData.put("source","current");
            postData.put("geom", geom);
        }  catch(JSONException e) {
            e.printStackTrace();
        }

        String mPostData = postData.toString();
        String httpAuthToken = "abcdefgh";
        String apiUrl = "http://192.168.56.1:8882/api/v1/users/locations/";
        Boolean result = locationService.submitLocation(mPostData, httpAuthToken, apiUrl);
        assertTrue(result);
    }

}