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

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static Date now = new Date();
    private GoogleApiClient mGoogleApiClient;
    private SubmitCrdTask mSubmitCrdTask = null;
    LocationRequest mLocationRequest;
    public static SharedPreferences sharedPref;
    public static Bundle data;
    static public LocalBroadcastManager broadcaster;
    static final public String NOTIF_RESULT = "us.alerted.alerted.NotificationService.NEW_REQUEST";
    static final public String NOTIF_MESSAGE = "us.alerted.alerted.NotificationService.NEW_MESSAGE";

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // This 'data' object is able to query the meta data from the Manifest
        try {
            data = getApplicationContext().getPackageManager().getApplicationInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mSubmitCrdTask = new SubmitCrdTask();
        mSubmitCrdTask.execute((Void) null);
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
            mSubmitCrdTask = new SubmitCrdTask();
            mSubmitCrdTask.execute((Void) null);
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(30 * 60 * 1000); // Update location every 30 minutes
        mLocationRequest.setFastestInterval(5 * 60 * 1000); // 60 seconds, in milliseconds

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public static List<AlertGson> getAlertFromApi(String lat, String lng){
        String apiUrl;
        if (BuildConfig.DEBUG) {
            apiUrl = data.getString("api.url.test.url");

        } else {
            apiUrl = data.getString("api.url.prod.url");
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiUrl)
                .build();

        AlertsApi alertsApi = restAdapter.create(AlertsApi.class);
        List<AlertGson> result = alertsApi.getMyThing(lat, lng);
        return result;
    }

    public static void sendToApp(String message) {
        Intent intent = new Intent(NOTIF_RESULT);
        if(message != null)
            intent.putExtra(NOTIF_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    public static boolean saveAlertToDB(AlertGson alertGson) {
        Boolean result = false;

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

    public boolean submitLocation(String mPostData, String httpAuthToken){
        try {
            String apiUrl;
            if (BuildConfig.DEBUG) {
                apiUrl = data.getString("api.url.test.locations");

            } else {
                apiUrl = data.getString("api.url.prod.locations");
            }

            URL url = new URL(apiUrl);
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
            conn.getInputStream();
            int code = conn.getResponseCode();

            return (code == 201);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class SubmitCrdTask extends AsyncTask<Object, Void, Object> {

        public String generatePostData(){
            JSONObject postData = new JSONObject();
            JSONObject geom = new JSONObject();
            try {
                geom.put("type", "Point");
                geom.put("coordinates", Utils.format(mLastLocation));
                postData.put("name","Current Location");
                postData.put("source","current");
                postData.put("geom", geom);

            }  catch(JSONException e) {
                e.printStackTrace();
            }
            return postData.toString();
        }

        @Override
        protected Object doInBackground(final Object... params) {

            String mPostData = generatePostData();
            String httpAuthToken = sharedPref.getString("AlertedToken", null);

            // Do not hit api if no json data

            return !mPostData.isEmpty() && submitLocation(mPostData, httpAuthToken);
        }
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

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }
}
