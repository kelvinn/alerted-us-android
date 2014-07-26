package us.alerted.alerted;

/**
 * Created by kelvinn on 16/07/2014.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, LocationService.class);
        context.startService(startServiceIntent);
    }
}