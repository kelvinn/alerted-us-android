package us.alerted.alerted;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.newrelic.agent.android.NewRelic;

import org.json.JSONObject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;
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
    ArrayList<String> alertNameList;
    public BroadcastReceiver receiver;
    //private SharedPreferences sharedPref = getApplicationContext().getPreferences(Context.MODE_PRIVATE);

    private List<RowItem> rowItems;

    private static Integer[] images = {
            R.drawable.prisoners,
            R.drawable.prisoners,
            R.drawable.prisoners,
            R.drawable.prisoners,
            R.drawable.prisoners,
            R.drawable.prisoners,
            R.drawable.prisoners,
            R.drawable.prisoners
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewRelic.withApplicationToken(
                "xXxXxXxXxXx"
        ).start(this.getApplication());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //String httpAuthToken = sharedPref.getString("AlertedToken", null);
        isUserLoggedIn = sharedPref.getBoolean("userLoggedInState", false);

        if (!isUserLoggedIn) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else {

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String s = intent.getStringExtra(NotificationService.NOTIF_MESSAGE);
                    if (s.equals("NEW_CARD")){
                        Intent newIntent = getIntent();
                        overridePendingTransition(0, 0);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(newIntent);
                    }
                }
            };

            numOfMissedMessages = getString(R.string.num_of_missed_messages);

            /*
            setContentView(R.layout.activity_main);

            // TODO ENABLE BELOW
            tView = (TextView) findViewById(R.id.tViewId);
            tView.setMovementMethod(new ScrollingMovementMethod());

            */


            setContentView(R.layout.activity_main);

            List<Alert> alerts = Alert.find(Alert.class, null, null, null, "effective DESC", "8");
            //Collections.reverse(alerts);

            //ArrayList<JSONObject> listdata = new ArrayList<JSONObject>();

            ListView lv = (ListView) findViewById(R.id.myListImg);
            rowItems = new ArrayList<RowItem>();

            //Populate the List
            for (int i = 0; i < alerts.size(); i++) {
                RowItem item = new RowItem(images[i], alerts.get(i).headline, alerts.get(i).certainty);
                rowItems.add(item);
            }

            // Set the adapter on the ListView
            LazyAdapter adapter = new LazyAdapter(getApplicationContext(), R.layout.list_row, rowItems);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Context context = getApplicationContext();
                    CharSequence text = "Hello toast!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                /*
                Intent intent = new Intent(MainActivity.this, SendMessage.class);
                String message = "abc";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                */
                }
            });

            startService(new Intent(this, NotificationService.class));
            startService(new Intent(this, LocationService.class));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        inBackground = false;
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(NotificationService.NOTIF_RESULT));
    }

    public void onStop(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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

            // Stop services
            stopService(new Intent(this, NotificationService.class));
            stopService(new Intent(this, LocationService.class));

            // Erase settings from shared prefs
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.first_launch), true);
            editor.putBoolean(getString(R.string.was_sns_submitted), true);
            editor.remove("AlertedToken");
            editor.putBoolean("userLoggedInState", false);
            editor.apply();

            // Now go back to LoginActivity
            //Intent intent = getIntent();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            //MainActivity.this.startActivity(intent);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
