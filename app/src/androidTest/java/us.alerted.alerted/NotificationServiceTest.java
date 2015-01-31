package us.alerted.alerted;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


@LargeTest
public class NotificationServiceTest extends ServiceTestCase<NotificationService> {

    public static SharedPreferences sharedPref;
    public String postData;
    public NotificationServiceTest() {
        super(NotificationService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        postData = "{\"cap_sender_name\": null, \"cap_certainty\": \"Likely\", " +
                "\"cap_effective\": \"2015-01-21T01:16:17Z\", \"cap_urgency\": \"Expected\", " +
                "\"cap_response_type\": \"Monitor\", \"cap_event_code\": null, " +
                "\"cap_expires\": \"2015-01-22T01:16:17Z\", \"cap_category\": \"Health\"," +
                " \"cap_description\": \"The air quality for the Sydney basin is forecast to be POOR.\"," +
                " \"cap_audience\": null, \"cap_headline\": \"Air Quality Forecast (POOR)\"," +
                " \"cap_alert\": 49, \"cap_language\": \"en-AU\", \"cap_contact\": null," +
                " \"cap_onset\": null, \"cap_severity\": \"Moderate\", \"cap_link\": \"\"," +
                " \"cap_event\": \"Air Quality\", \"cap_instruction\": \"People with heart or lung disease should limit exercising outdoors.\"}";

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

        // First, delete all entries
        Alert.deleteAll(Alert.class);

        Bundle extras = new Bundle();
        extras.putString("message", postData);

        // Second, save one entry to the DB
        Boolean result = NotificationService.saveToDB(extras);
        assertTrue(result);

        // Third, try to save another entry
        Boolean result2 = NotificationService.saveToDB(extras);
        assertFalse(result2);

        // Finally, count the number of entries.
        List<Alert> alerts = Alert.listAll(Alert.class);
        assertEquals(1, alerts.size());

    }
}