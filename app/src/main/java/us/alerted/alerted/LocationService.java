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
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    private GoogleApiClient mGoogleApiClient;
    private SubmitCrdTask mSubmitCrdTask = null;
    LocationRequest mLocationRequest;
    SharedPreferences sharedPref;
    public static Bundle data;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // This 'data' object is able to query the meta data from the Manifest
        try {
            data = getApplicationContext().getPackageManager().getApplicationInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "created");
        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "mGoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.toString());
        try {
            JSONObject postData = new JSONObject();
            JSONObject geom = new JSONObject();

            geom.put("type", "Point");
            geom.put("coordinates", Utils.format(location));
            postData.put("name","Current Location");
            postData.put("source","current");
            postData.put("geom", geom);

            Log.i(TAG, "Location changed!");
            mSubmitCrdTask = new SubmitCrdTask(postData.toString());
            mSubmitCrdTask.execute((Void) null);

        }  catch(JSONException e) {
            Log.e(TAG, e.toString());
        }
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
        if (location == null) {
            Log.i(TAG, "null");
        }
        else {
            Log.i(TAG, "handling new location");
            //handleNewLocation(location);
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(5 * 60 * 1000); // Update location every five minutes
        mLocationRequest.setFastestInterval(1 * 1000); // 60 seconds, in milliseconds

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class SubmitCrdTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPostData;

        SubmitCrdTask(String postData) {
            mPostData = postData;
            Log.i(TAG, mPostData);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                String apiUrl;
                if (BuildConfig.DEBUG) {
                    apiUrl = data.getString("api.url.test.gcmtoken");

                } else {
                    apiUrl = data.getString("api.url.prod.gcmtoken");
                }

                String httpAuthToken = sharedPref.getString("AlertedToken", null);
                //Log.i(TAG, "Submitting data: " + mPostData);
                //URL url = new URL("https://alerted.us/api/v1/users/locations/");
                //URL url = new URL("http://192.168.56.1:8882/api/v1/users/locations/");
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String tokenAuth = "Token " + httpAuthToken;
                //conn.setRequestProperty ("Authorization", tokenAuth);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //Log.i(TAG, mPostData);
                writer.write(mPostData);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                // create JSON object from content
                InputStream inputStream = conn.getInputStream();

            } catch (MalformedURLException e) {
                Log.e(TAG, e.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return true;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
}
