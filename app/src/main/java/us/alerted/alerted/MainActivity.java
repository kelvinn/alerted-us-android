package us.alerted.alerted;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import us.alerted.alerted.R;

public class MainActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    private String numOfMissedMessages;
    private TextView tView;
    public SharedPreferences sharedPref;
    private SharedPreferences savedValues;
    public static Boolean inBackground = true;
    public String httpAuthToken;
    public Boolean isUserLoggedIn;
    //private SharedPreferences sharedPref = getApplicationContext().getPreferences(Context.MODE_PRIVATE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //String httpAuthToken = sharedPref.getString("AlertedToken", null);
        isUserLoggedIn = sharedPref.getBoolean("userLoggedInState", false);
        if (!isUserLoggedIn) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else {
            numOfMissedMessages = getString(R.string.num_of_missed_messages);
            setContentView(R.layout.activity_main);
            tView = (TextView) findViewById(R.id.tViewId);
            tView.setMovementMethod(new ScrollingMovementMethod());
            startService(new Intent(this, NotificationService.class));
            startService(new Intent(this, LocationService.class));
        }
    }

    public void onStop(){
        super.onStop();
        inBackground = true;
    }

    public void onRestart(){
        super.onRestart();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //String httpAuthToken = sharedPref.getString("AlertedToken", null);
        isUserLoggedIn = sharedPref.getBoolean("userLoggedInState", false);
        if (!isUserLoggedIn) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else {
            tView.setText("");
        }

    }

    public void onResume(){
        super.onResume();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        isUserLoggedIn = sharedPref.getBoolean("userLoggedInState", false);
        if (!isUserLoggedIn) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else {
            inBackground = false;
            savedValues = NotificationService.savedValues;
            int numOfMissedMessages = 0;
            if(savedValues != null){
                numOfMissedMessages = savedValues.getInt(this.numOfMissedMessages, 0);
            }
            String newMessage = getMessage(numOfMissedMessages);
            if(newMessage!=""){
                Log.i("displaying message", newMessage);
                tView.append(newMessage);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        isUserLoggedIn = sharedPref.getBoolean("userLoggedInState", false);
        if (!isUserLoggedIn) {
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_authenticated, menu);
        }
        return true;
    }

    // If messages have been missed, check the backlog. Otherwise check the current intent for a new message.
    private String getMessage(int numOfMissedMessages) {
        String message = "";
        String linesOfMessageCount = getString(R.string.lines_of_message_count);
        if(numOfMissedMessages > 0){
            String plural = numOfMissedMessages > 1 ? "s" : "";
            Log.i("onResume","missed " + numOfMissedMessages + " message" + plural);
            tView.append("You missed " + numOfMissedMessages +" message" + plural + ". Your most recent was:\n");
            for(int i = 0; i < savedValues.getInt(linesOfMessageCount, 0); i++){
                String line = savedValues.getString("MessageLine"+i, "");
                message+= (line + "\n");
            }
            NotificationManager mNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotification.cancel(R.string.notification_number);
            SharedPreferences.Editor editor=savedValues.edit();
            editor.putInt(this.numOfMissedMessages, 0);
            editor.putInt(linesOfMessageCount, 0);
            editor.apply();
        }
        else{
            Intent intent = getIntent();
            if(intent!=null){
                Bundle extras = intent.getExtras();
                if(extras!=null){
                    for(String key: extras.keySet()){
                        message+= key + "=" + extras.getString(key) + "\n";
                    }
                }
            }
        }
        message+="\n";
        return message;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("AlertedToken");
            editor.putBoolean("userLoggedInState", false);
            editor.apply();
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
