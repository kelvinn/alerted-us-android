package us.alerted.alerted;

import android.app.Activity;
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

import com.newrelic.agent.android.NewRelic;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.List;

import static us.alerted.alerted.LocationService.*;

public class MainActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    public SharedPreferences sharedPref;
    public static Boolean inBackground = true;
    public BroadcastReceiver receiver;

    static final public String EXTRA_MESSAGE = "us.alerted.alerted.MainActivity.MESSAGE";
    //private SharedPreferences sharedPref = getApplicationContext().getPreferences(Context.MODE_PRIVATE);

    private List<RowItem> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mint.initAndStartSession(MainActivity.this, BuildConfig.SPLUNK_MINT_ID);

        //overridePendingTransition(R.anim.animation_leave,
        //        R.anim.animation_enter);


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(NOTIF_MESSAGE);
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

        /*
        setContentView(R.layout.activity_main);

        // TODO ENABLE BELOW
        tView = (TextView) findViewById(R.id.tViewId);
        tView.setMovementMethod(new ScrollingMovementMethod());

        */

        setContentView(R.layout.activity_main);

        List<Alert> alerts = Alert.find(Alert.class, null, null, null, "effective DESC", "8");

        final ListView lv = (ListView) findViewById(R.id.myListImg);

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
                        alerts.get(i).effective, alerts.get(i).expires, alerts.get(i).received);
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

        // Now we start the alarm service to keep polling for alerts
        Intent msgIntent = new Intent(this, StarterService.class);
        startService(msgIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        inBackground = false;
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(NOTIF_RESULT));

    }

    public void onStop(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
        inBackground = true;
    }

    public void onRestart(){
        super.onRestart();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void onResume(){
        super.onResume();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        inBackground = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


}
