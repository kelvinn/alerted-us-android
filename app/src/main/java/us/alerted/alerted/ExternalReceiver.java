package us.alerted.alerted;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ExternalReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            Bundle extras = intent.getExtras();
            if(!MainActivity.inBackground){
                NotificationService.sendToApp(extras, context);
                NotificationService.saveToDB(extras, context);
            }
            else{
                NotificationService.saveToLog(extras, context);
                NotificationService.saveToDB(extras, context);
            }
        }
    }
}

