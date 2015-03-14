package us.alerted.alerted;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StarterService extends IntentService {
    public SharedPreferences sharedPref;

    public StarterService() {
        super("StarterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        handleScheduleService();
    }

    private void handleScheduleService() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Integer pollingFrequency = Integer.valueOf(sharedPref.getString("pollingFrequency", "5"));

        // Construct an intent that will execute the AlarmReceiver
        Intent newIntent = new Intent(this, AlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate

        int intervalMillis = pollingFrequency * 60 * 1000; // by poll frequency
        //int intervalMillis = 30000; // Temp
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
    }

}
