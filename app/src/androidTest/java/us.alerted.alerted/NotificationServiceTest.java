package us.alerted.alerted;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.json.JSONObject;


@LargeTest
public class NotificationServiceTest extends ServiceTestCase<NotificationService> {

    public static SharedPreferences sharedPref;

    public NotificationServiceTest() {
        super(NotificationService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public void testSubmitGCMToken() {
        NotificationService.SubmitGcmTokenTask asyncOperation = new NotificationService.SubmitGcmTokenTask();
        asyncOperation.execute();

        // Test that the GCM was submitted OK
        Boolean gcm_result = sharedPref.getBoolean(String.valueOf(R.string.post_new_gmc_token), true);
        assertFalse(gcm_result);
    }

    public void testRegisterGCMToken() {
        NotificationService.RegisterGcmTokenTask asyncOperation = new NotificationService.RegisterGcmTokenTask();
        asyncOperation.execute();

        // Test that the GCM was submitted OK
        String gcm_result = sharedPref.getString("GCM_TOKEN", "");
        assertNotSame(gcm_result, "");
    }
    public void testSaveToDB() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("cap_headline", "Sample Weather Alert");
            postData.put("cap_urgency", "Immediate");
            postData.put("cap_severity", "Minor");
            postData.put("cap_certainty", "Observed");
            postData.put("cap_effective", "2015-01-10T05:05:05");
            postData.put("cap_expires", "2015-01-11T05:05:05");
            postData.put("cap_description", "Test Description.");
            postData.put("cap_instruction", "This is a sample instruction");
            postData.put("cap_category", "Met");
            postData.put("cap_event", "Test Weather Statement");
        }  catch(JSONException e) {
            e.printStackTrace();
        }


        Bundle extras = new Bundle();
        extras.putString("message", postData.toString());

        NotificationService.saveToDB(extras);

    }
}