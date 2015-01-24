package us.alerted.alerted;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
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

    }

    public void testSubmitLocation() {
        /*
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("AlertedToken", "blahtoken");
        editor.putBoolean("userLoggedInState", true);
        editor.apply();
        JSONObject postData = new JSONObject();
        JSONObject geom = new JSONObject();
        Location location = new Location("");//provider name is unecessary
        location.setLatitude(0.0d);//your coords of course
        location.setLongitude(0.0d);
        try {
            geom.put("type", "Point");
            geom.put("coordinates", Utils.format(location));
            postData.put("name","Current Location");
            postData.put("source","current");
            postData.put("geom", geom);
        }  catch(JSONException e) {
            e.printStackTrace();
        }
        */

        LocationService locationService = new LocationService();
        locationService.onCreate();
        Location location = new Location("");//provider name is unecessary
        location.setLatitude(0.0d);//your coords of course
        location.setLongitude(0.0d);
        //locationService.onLocationChanged(location);
        //Boolean result = locationService.submitLocation();
        Boolean result = locationService.triggerChange();
        assertTrue(result);

        //Boolean result = sharedPref.getBoolean(String.valueOf(R.string.post_new_gmc_token), true);
        //assertFalse(result);
        // Test that the GCM was submitted OK
        //String gcm_result = sharedPref.getString("GCM_TOKEN", "");
        //assertNotSame(gcm_result, "");
    }

}