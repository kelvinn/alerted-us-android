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

}