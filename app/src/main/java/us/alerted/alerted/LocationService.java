package us.alerted.alerted;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

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

public class LocationService extends Service implements
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private String TAG = this.getClass().getSimpleName();
    private SubmitCrdTask mSubmitCrdTask = null;

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    private Location location;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    private TextView mConnectionState;
    private TextView mConnectionStatus;
    private Bundle data;
    public String httpAuthToken;

    SharedPreferences sharedPref;

    public static final String ACTION_NEW_LOCATION = "us.alerted.alerted.action.newlocation";
    public static final String EXTRA_LOCATION = "location";

    private static Context mContext;

    /*
     * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
     * method handleRequestSuccess of LocationUpdateReceiver.
     *
     */
    boolean mUpdatesRequested = false;

    @Override
    public void onCreate() {
        super.onCreate();


        mContext = getApplicationContext();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        try {
            data = getApplicationContext().getPackageManager().getApplicationInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (servicesAvailable() && httpAuthTokenExists()) {
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(60 * 60 * 1000)
                    .setFastestInterval(5 * 60 * 1000);

            mLocationClient = new LocationClient(this, this, this);
            mLocationClient.connect();
        } else {
            stopSelf();
        }
    }

    protected boolean submitLocation(){
        //String httpAuthToken = sharedPref.getString("AlertedToken", "");

        try {
            JSONObject postData = new JSONObject();
            JSONObject geom = new JSONObject();

            geom.put("type", "Point");
            geom.put("coordinates", Utils.format(location));
            postData.put("name","Current Location");
            postData.put("source","current");
            postData.put("geom", geom);


            String apiUrl;
            //Log.i(TAG, "Submitting data: " + mPostData);
            if (BuildConfig.DEBUG) {
                apiUrl = data.getString("api.url.test.token");
            } else {
                apiUrl = data.getString("api.url.prod.token");
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
            //Log.i(TAG, mPostData);
            writer.write(postData.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();


            // create JSON object from content
            InputStream inputStream = conn.getInputStream();

            int code = conn.getResponseCode();
            if (code == 201){
                return true;
            } else {
                return false;
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch(JSONException e) {
            Log.e(TAG, e.toString());
        }
        return true;
    }
    public class SubmitCrdTask extends AsyncTask<Void, Void, Boolean> {



        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = submitLocation();

            return true;
        }
    }


    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        //Log.d(TAG, "Connected to Google Play Services.");
        Location lastLocation = mLocationClient.getLastLocation();
        //Log.d(TAG, "Last location is: " + (lastLocation == null ? "null" : Utils.format(lastLocation)));
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }



    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        //Log.d(TAG, "Disconnected from Google Play Services.");
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google Play Services failed.");
    }

    /**
     * Report location updates to the UI.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        SubmitCrdTask mSubmitCrdTask = new SubmitCrdTask();
        mSubmitCrdTask.execute((Void) null);


        // TODO figure out how to use the below
        Intent intent = new Intent(ACTION_NEW_LOCATION).putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    public boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            //Log.d(TAG, "Google Play services is available.");
            return true;
        }
        Log.e(TAG, "Google Play services NOT available.");
        return false;
    }

    public boolean httpAuthTokenExists() {


        String httpAuthToken = sharedPref.getString("AlertedToken", null);

        if (httpAuthToken != null) {
            return true;
        } else {
            return false;
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand()");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy()");

        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
            mLocationClient.disconnect();
        }
        super.onDestroy();
    }

}
