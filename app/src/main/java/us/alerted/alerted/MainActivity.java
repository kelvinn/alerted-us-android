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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.newrelic.agent.android.NewRelic;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    static final public String EXTRA_MESSAGE = "us.alerted.alerted.MainActivity.MESSAGE";
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

        PrettyTime p = new PrettyTime();
        NewRelic.withApplicationToken(
                "xXxXxXxXxXx"
        ).start(this.getApplication());

        //overridePendingTransition(R.anim.animation_leave,
        //        R.anim.animation_enter);

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

            final ListView lv = (ListView) findViewById(R.id.myListImg);
            //TextView empty=(TextView)findViewById(R.id.empty);
            //lv.setEmptyView(empty);

            //TextView empty_subtext=(TextView)findViewById(R.id.empty_subtext);
            //lv.setEmptyView(empty_subtext);

            LinearLayout empty = (LinearLayout)findViewById(R.id.empty);
            lv.setEmptyView(empty);
            rowItems = new ArrayList<RowItem>();

            if (alerts.size() > 0) {
                //Populate the List
                for (int i = 0; i < alerts.size(); i++) {
                    // TODO image to be put in when map is ready
                    // RowItem item = new RowItem(images[i], alerts.get(i).headline, alerts.get(i).certainty);

                    RowItem item = new RowItem(alerts.get(i).getId(), alerts.get(i).event,
                            alerts.get(i).description, alerts.get(i).certainty,
                            alerts.get(i).severity, alerts.get(i).urgency, alerts.get(i).category,
                            alerts.get(i).effective);
                    rowItems.add(item);
                }
            }


            // Set the adapter on the ListView
            LazyAdapter adapter = new LazyAdapter(getApplicationContext(), R.layout.list_row, rowItems);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    Intent intent = new Intent(MainActivity.this, MainDetailActivity.class);
                    Long message = rowItems.get(position).getId();

                    Bundle extras = new Bundle();
                    extras.putLong(EXTRA_MESSAGE, message);

                    // add bundle to intent
                    intent.putExtras(extras);

                    startActivity(intent);

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

            // Erase all records in database
            Alert.deleteAll(Alert.class);

            // Erase settings from shared prefs (e.g. reset back go normal state)
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.first_launch), true);
            editor.putBoolean(getString(R.string.was_sns_submitted), true);
            editor.putBoolean(getString(R.string.post_new_gmc_token), true);

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
