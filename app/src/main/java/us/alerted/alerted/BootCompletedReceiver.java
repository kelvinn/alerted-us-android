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
        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent msgIntent = new Intent(context, StarterService.class);
        context.startService(msgIntent);

    }
}