package us.alerted.alerted;

import android.location.Location;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.AndroidTestCase;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


@LargeTest
public class UtilsTest extends AndroidTestCase  {

    public void testFormat() {
        Location location = new Location("");//provider name is unecessary
        location.setLatitude(1.0d);//your coords of course
        location.setLongitude(1.0d);
        String result = String.valueOf(Utils.format(location));
        assertEquals(result, "[1,1]");
    }

    public void testGetQuery() {
        List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
        urlParams.add(new BasicNameValuePair("username", "test@alerted.us"));
        urlParams.add(new BasicNameValuePair("password", "password"));

        String result = null;
        try {
            result = Utils.getQuery(urlParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        assertEquals(result, "username=test%40alerted.us&password=password");
    }
}