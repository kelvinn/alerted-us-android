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
                // TODO also refresh app on this one?
                NotificationService.saveToDB(extras, context);
                NotificationService.sendToApp("NEW_CARD");
            }
            else{
                NotificationService.saveToDB(extras, context);
                NotificationService.postNotification(new Intent(context, MainActivity.class), context);
            }
        }
    }
}

