package us.alerted.alerted;
/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit.RestAdapter;

/**
 * Location sample.
 *
 * Demonstrates use of the Location API to retrieve the last known location for a device.
 * This sample uses Google Play services (GoogleApiClient) but does not need to authenticate a user.
 * See https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart if you are
 * also using APIs that need authentication.
 */
public class LocationService extends Service implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    public String TAG = this.getClass().getSimpleName();
    public static TimeZone tz = TimeZone.getTimeZone("UTC");

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);


    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    public static SharedPreferences sharedPref;
    public static Bundle data;
    static public LocalBroadcastManager broadcaster;
    static final public String NOTIF_RESULT = "us.alerted.alerted.LocationService.NEW_REQUEST";
    static final public String NOTIF_MESSAGE = "us.alerted.alerted.LocationService.NEW_MESSAGE";
    private Context context;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        broadcaster = LocalBroadcastManager.getInstance(this);

        buildGoogleApiClient();
    }


    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.e(TAG, "Error connecting with Google Play Services");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        new GetAndSaveAlertsCmd(context).execute();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            mLastLocation = location;

            new GetAndSaveAlertsCmd(context).execute();

            Intent stopServiceIntent = new Intent(context, LocationService.class);
            context.stopService(stopServiceIntent);

        } else {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(5 * 60 * 1000); // Update location every 30 minutes
            mLocationRequest.setFastestInterval(1 * 60 * 1000); // 60 seconds, in milliseconds
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void sendToApp(String message) {

        Intent intent = new Intent(NOTIF_RESULT);
        if(message != null)
            intent.putExtra(NOTIF_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }


    public boolean saveAlertsToDB(List<AlertGson> alerts) {
        boolean result = false;
        if(alerts != null) {
            for(AlertGson alert : alerts) {
                result = saveAlertToDB(alert);
            }
        }
        return result;
    }


    /**
     * This can probably be refactored. The point of this is to first get the last check date, and if now
     * is after that, store now for reference. It then returns the last check date to compare
     * against cap_date_received in the Alerted API.
     */
    public String getLastCheckDate() {
        // Return lastCheckDate so we can query with cap_date_received
        Date date = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
        String nowAsIso = sdf.format(date);
        return sharedPref.getString(String.valueOf(R.string.last_check), nowAsIso);

    }

    public String setLastCheckDate() {
        Date now = new Date();
        String nowAsIso = sdf.format(now);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(String.valueOf(R.string.last_check), nowAsIso);
        editor.apply();

        return nowAsIso;
    }

    public List<AlertGson> getAlertFromApi(String lat, String lng, String cap_date_received){


        String apiUrl;

        apiUrl = BuildConfig.SERVER_URL;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiUrl)
                .build();

        AlertsApi alertsApi = restAdapter.create(AlertsApi.class);

        List<AlertGson> result;

        try {
            result = alertsApi.getMyThing(lat, lng, cap_date_received);
        } catch(retrofit.RetrofitError e) {
            result = null;
        }

        return result;
    }

    protected static void postNotification(Intent intentAction, Context context){

        List<Alert> alerts = Alert.find(Alert.class, null, null, null, "effective DESC", "1");


        Map<String,Integer> categoryLookup = new HashMap<>();
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

        Map<String,Integer> severityLookup = new HashMap<>();
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

    public boolean saveAlertToDB(AlertGson alertGson) {
        Boolean result;
        Date now = new Date();

        List<Alert> alerts = Alert.find(Alert.class, "effective = ?", alertGson.getInfo().get(0).getCap_effective());

        if (alerts.isEmpty()){
            Alert alert = new Alert();

            alert.headline = alertGson.getInfo().get(0).getCap_headline();
            alert.urgency = alertGson.getInfo().get(0).getCap_urgency();
            alert.severity = alertGson.getInfo().get(0).getCap_severity();
            alert.certainty = alertGson.getInfo().get(0).getCap_certainty();
            alert.effective = alertGson.getInfo().get(0).getCap_effective();
            alert.expires = alertGson.getInfo().get(0).getCap_expires();
            alert.received = sdf.format(now);
            alert.description = alertGson.getInfo().get(0).getCap_description();
            alert.instruction = alertGson.getInfo().get(0).getCap_instruction();
            alert.category = alertGson.getInfo().get(0).getCap_category();
            alert.event = alertGson.getInfo().get(0).getCap_event();

            alert.save();
            result = true;
        } else {
            result = false;
        }

        return result;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*
    * class for binding
    */
    private final IBinder mBinder = new myBinder();
    public class myBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }



    private class GetAndSaveAlertsCmd extends AsyncTask<Location, Void, Boolean> {

        Context c;

        public GetAndSaveAlertsCmd(Context c) {
            this.c = c;
        }


        @Override
        protected Boolean doInBackground(Location... l) {
            Boolean result;

            if(mLastLocation != null) {
                String lat = String.valueOf(mLastLocation.getLatitude());
                String lng = String.valueOf(mLastLocation.getLongitude());
                String cap_date_received = getLastCheckDate();

                List<AlertGson> alerts = getAlertFromApi(lat, lng, cap_date_received);
                result = saveAlertsToDB(alerts);
            } else {
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(!MainActivity.inBackground && result){
                sendToApp("NEW_CARD");
            }
            else if(result){
                postNotification(new Intent(c,
                        MainActivity.class), c);
            }
            setLastCheckDate();

            // Now we stop the service and wait for the next run
            Intent stopServiceIntent = new Intent(context, LocationService.class);
            context.stopService(stopServiceIntent);
        }
    }

}
