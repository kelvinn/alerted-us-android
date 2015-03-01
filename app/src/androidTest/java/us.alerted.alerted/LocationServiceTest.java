package us.alerted.alerted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONException;
import org.json.JSONObject;

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

        AlertNew result = getService().getAlertTest();
        //assertEquals(result.category, "bob");
        assertEquals(result.getCap_sender(), "w-nws.webmaster@noaa.gov");


        assertEquals(result.getCap_slug(), "712c963f91646a212f071571d611d8");

        Info info = result.getInfo().get(0);
        assertEquals(info.getCap_category(), "Met");

    }

    public void testSubmitLocation() {

        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);

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
        Boolean result = getService().submitLocation(mPostData, httpAuthToken);
        assertTrue(result);
    }

}