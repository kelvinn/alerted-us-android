package us.alerted.alerted;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * This service is designed to run in the background and receive messages from gcm. If the app is in the foreground
 * when a message is received, it will immediately be posted. If the app is not in the foreground, the message will be saved
 * and a notification is posted to the NotificationManager.
 */
public class NotificationService extends Service{

    private GoogleCloudMessaging gcm;

    public static SharedPreferences sharedPref;
    private String TAG = this.getClass().getSimpleName();


    public void onCreate(){
        super.onCreate();
        final String preferences = getString(R.string.preferences);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        gcm = GoogleCloudMessaging.getInstance(getBaseContext());

        if(sharedPref.getBoolean(getString(R.string.first_launch), true)){
            register();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.first_launch), false);
            editor.commit();
        }

        if(sharedPref.getBoolean(getString(R.string.was_sns_submitted), true)){
            submitSNSToken();
        }


        // Let AndroidMobilePushApp know we have just initialized and there may be stored messages
        //sendToApp(new Bundle(), this);
    }

    public static void sendToApp(Bundle extras, Context context){
        Intent newIntent = new Intent();
        newIntent.setClass(context, MainActivity.class);
        newIntent.putExtras(extras);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    public static void saveToDB(Bundle extras, Context context){
        JSONObject recData;
        String msg = extras.getString("default");


        try {
            if (msg != null) {
                recData = new JSONObject(msg);

                String cap_headline = recData.get("cap_headline").toString();
                String cap_urgency = recData.get("cap_urgency").toString();
                String cap_severity = recData.get("cap_severity").toString();
                String cap_certainty = recData.get("cap_certainty").toString();
                String cap_effective = recData.get("cap_effective").toString();

                Alert alert = new Alert();

                alert.headline = cap_headline;
                alert.urgency = cap_urgency;
                alert.severity = cap_severity;
                alert.certainty = cap_certainty;
                alert.effective = cap_effective;
                alert.save();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected static void saveToLog(Bundle extras, Context context){
        SharedPreferences.Editor editor = sharedPref.edit();
        String numOfMissedMessages = context.getString(R.string.num_of_missed_messages);
        int linesOfMessageCount = 0;
        for(String key : extras.keySet()){
            String line = String.format("%s=%s", key, extras.getString(key));
            editor.putString("MessageLine" + linesOfMessageCount, line);
            linesOfMessageCount++;
        }
        editor.putInt(context.getString(R.string.lines_of_message_count), linesOfMessageCount);
        editor.putInt(context.getString(R.string.lines_of_message_count), linesOfMessageCount);
        editor.putInt(numOfMissedMessages, sharedPref.getInt(numOfMissedMessages, 0) + 1);
        editor.commit();
        //postNotification(new Intent(context, MainActivity.class), context);
    }

    protected static void postNotification(Intent intentAction, Context context){

        List<Alert> alerts = Alert.find(Alert.class, null, null, null, "effective DESC", "1");

        String msg = alerts.get(0).headline;

        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_CANCEL_CURRENT);
        final Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(msg)
                .setContentText("")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .getNotification();

        mNotificationManager.notify(R.string.notification_number, notification);
    }

    private void submitSNSToken() {

        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                String mPostData;
                try {

                    token = sharedPref.getString("GCM_TOKEN", "");

                    JSONObject postData = new JSONObject();

                    postData.put("sns_reg_id", token);

                    mPostData = postData.toString();

                    String httpAuthToken = sharedPref.getString("AlertedToken", null);

                    URL url = new URL("https://alerted.us/api/v1/users/snstoken/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    String tokenAuth = "Token " + httpAuthToken;
                    conn.setRequestProperty ("Authorization", tokenAuth);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(mPostData);
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    // create JSON object from content
                    InputStream inputStream = conn.getInputStream();

                    int code = conn.getResponseCode();
                    if (code == 201){
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.was_sns_submitted), false);
                        editor.commit();
                    }
                }
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }.execute(null, null, null);
    }

    private void register() {
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(getString(R.string.project_number));
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("GCM_TOKEN", token);
                    editor.commit();
                }
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

}