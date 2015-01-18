package us.alerted.alerted;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import java.util.concurrent.CountDownLatch;

@LargeTest
public class NotificationServiceTest extends ServiceTestCase<NotificationService> {

    public static SharedPreferences sharedPref;
    final CountDownLatch signal = new CountDownLatch(1);

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

        //Boolean result = sharedPref.getBoolean(String.valueOf(R.string.post_new_gmc_token), true);
        //assertFalse(result);
        // Test that the GCM was submitted OK
        String gcm_result = sharedPref.getString("GCM_TOKEN", "");
        assertNotSame(gcm_result, "");
    }

}