package us.alerted.alerted;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/*
 * This service is designed to run in the background and receive messages from gcm. If the app is in the foreground
 * when a message is received, it will immediately be posted. If the app is not in the foreground, the message will be saved
 * and a notification is posted to the NotificationManager.
 */
public class NotificationService extends Service{

    public static SharedPreferences sharedPref;
    public String TAG = this.getClass().getSimpleName();
    static public LocalBroadcastManager broadcaster;
    static final public String NOTIF_RESULT = "us.alerted.alerted.NotificationService.NEW_REQUEST";
    static final public String NOTIF_MESSAGE = "us.alerted.alerted.NotificationService.NEW_MESSAGE";
    public static Bundle data;
    public static GoogleCloudMessaging gcm;
    public static Context baseContext;


    public void onCreate(){
        super.onCreate();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        broadcaster = LocalBroadcastManager.getInstance(this);
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());

        // This 'data' object is able to query the meta data from the Manifest
        try {
            data = getApplicationContext().getPackageManager().getApplicationInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(sharedPref.getBoolean(getString(R.string.first_launch), true)){
            register();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.first_launch), false);
            editor.apply();
        }

        if(sharedPref.getBoolean(getString(R.string.was_sns_submitted), true)){
            submitGCMToken();
        }
    }

    public static void sendToApp(String message) {
        Intent intent = new Intent(NOTIF_RESULT);
        if(message != null)
            intent.putExtra(NOTIF_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    public static void saveToDB(Bundle extras, Context context){
        JSONObject recData;
        String msg = extras.getString("message");

        try {
            if (msg != null) {

                String cap_headline = "";
                String cap_urgency = "";
                String cap_severity = "";
                String cap_certainty = "";
                String cap_effective = "";
                String cap_expires = "";
                String cap_description = "";
                String cap_instruction = "";
                String cap_category = "";
                String cap_event = "";

                if (msg.equals("Test single notification")){
                    cap_headline = "Sample Weather Alert";
                    cap_urgency = "Immediate";
                    cap_severity = "Minor";
                    cap_certainty = "Observed";
                    cap_effective = "2015-01-10T05:05:05";
                    cap_expires = "2015-01-11T05:05:05";

                    cap_description = "...SIGNIFICANT WEATHER ADVISORY FOR...\n" +
                        "SOUTHERN HOPKINS COUNTY\n" +
                        "EASTERN RAINS COUNTY\n" +
                        "AT 447 AM CDT...NATIONAL WEATHER SERVICE METEOROLOGISTS DETECTED A\n" +
                        "STRONG THUNDERSTORM 2 MILES EAST OF SULPHUR SPRINGS...MOVING SOUTH AT\n" +
                        "30 MPH.\n" +
                        "CITIES IN THE PATH OF THIS STORM INCLUDE COMO...CUMBY...SULPHUR\n" +
                        "SPRINGS AND EMORY.\n" +
                        "FREQUENT CLOUD TO GROUND LIGHTNING...VERY HEAVY RAINFALL...PEA-SIZED\n" +
                        "HAIL...AND WIND GUSTS TO 50 MPH CAN BE EXPECTED FROM THIS STORM.\n" +
                        "IF THIS STORM INTENSIFIES...A SEVERE WEATHER WARNING MAY BE NEEDED.";
                    cap_instruction = "This is a sample instruction";
                    cap_category = "Met";
                    cap_event = "Test Weather Statement";
                } else {
                    recData = new JSONObject(msg);

                    cap_headline = recData.get("cap_headline").toString();
                    cap_urgency = recData.get("cap_urgency").toString();
                    cap_severity = recData.get("cap_severity").toString();
                    cap_certainty = recData.get("cap_certainty").toString();
                    cap_effective = recData.get("cap_effective").toString();
                    cap_expires = recData.get("cap_expires").toString();
                    cap_description = recData.get("cap_description").toString();
                    cap_instruction = recData.get("cap_instruction").toString();
                    cap_category = recData.get("cap_category").toString();
                    cap_event = recData.get("cap_event").toString();
                }

                Alert alert = new Alert();

                alert.headline = cap_headline;
                alert.urgency = cap_urgency;
                alert.severity = cap_severity;
                alert.certainty = cap_certainty;
                alert.effective = cap_effective;
                alert.expires = cap_expires;
                alert.description = cap_description;
                alert.instruction = cap_instruction;
                alert.category = cap_category;
                alert.event = cap_event;

                alert.save();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected static void postNotification(Intent intentAction, Context context){

        List<Alert> alerts = Alert.find(Alert.class, null, null, null, "effective DESC", "1");


        Map<String,Integer> categoryLookup = new HashMap<String, Integer>();
        {
            categoryLookup.put("Geo", R.drawable.geo);
            categoryLookup.put("Met",R.drawable.met);
            categoryLookup.put("Safety",R.drawable.safety);
            categoryLookup.put("Security",R.drawable.security);
            categoryLookup.put("Rescue",R.drawable.rescue);
            categoryLookup.put("Fire",R.drawable.fire);
            categoryLookup.put("Health",R.drawable.health);
            categoryLookup.put("Env",R.drawable.env);
            categoryLookup.put("Transport",R.drawable.transport);
            categoryLookup.put("Infra",R.drawable.infra);
            categoryLookup.put("CBRNE",R.drawable.cbrne);
            categoryLookup.put("Other",R.drawable.other);
        }

        Map<String,Integer> severityLookup = new HashMap<String, Integer>();
        {
            severityLookup.put("Extreme", Notification.PRIORITY_HIGH);
            severityLookup.put("Severe", Notification.PRIORITY_LOW);
            severityLookup.put("Moderate", Notification.PRIORITY_MIN);
            severityLookup.put("Minor", Notification.PRIORITY_MIN);
            severityLookup.put("Unknown", Notification.PRIORITY_MIN);
        }

        if (alerts.size() > 0) {
            String msg = alerts.get(0).event;

            final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            final Integer desc_cap_category = categoryLookup.get(alerts.get(0).category);
            final Integer notif_priority = severityLookup.get(alerts.get(0).severity);
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_CANCEL_CURRENT);
            final Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(desc_cap_category)
                    .setContentTitle(msg)
                    .setContentText("")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(notif_priority)
                    .getNotification();

            mNotificationManager.notify(R.string.notification_number, notification);
        }

    }

    public void submitGCMToken() {
        SubmitGcmTokenTask task = new SubmitGcmTokenTask();
        task.execute(null, null, null);
    }

    public static class SubmitGcmTokenTask extends AsyncTask<Object, Void, Object> {
        protected Object doInBackground(final Object... params) {
            String token;
            String mPostData;
            try {
                token = sharedPref.getString("GCM_TOKEN", "");

                JSONObject postData = new JSONObject();

                postData.put("registration_id", token);

                mPostData = postData.toString();

                String httpAuthToken = sharedPref.getString("AlertedToken", null);
                String reqMethod;
                URL url;

                reqMethod = "POST";

                String apiUrl;
                if (BuildConfig.DEBUG) {
                    apiUrl = data.getString("api.url.test.gcmtoken");

                } else {
                    apiUrl = data.getString("api.url.prod.gcmtoken");
                }

                Log.i("GCM_TOKEN", mPostData);

                url = new URL(apiUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String tokenAuth = "Token " + httpAuthToken;
                conn.setRequestProperty ("Authorization", tokenAuth);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(reqMethod);
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
                    editor.putBoolean(String.valueOf(R.string.was_sns_submitted), false);
                    editor.putBoolean(String.valueOf(R.string.post_new_gmc_token), false);
                    editor.apply();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public void register() {

        RegisterGcmTokenTask task = new RegisterGcmTokenTask();
        task.execute(gcm, null, null);
    }


    public static class RegisterGcmTokenTask extends AsyncTask<Object, Void, Object> {

        protected Object doInBackground(final Object... params) {

            String token;
            try {
                token = gcm.register("840452629019");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("GCM_TOKEN", token);
                editor.apply();
                Log.i("GCM Reg Success RegID", token);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

}