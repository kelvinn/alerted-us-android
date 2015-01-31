package us.alerted.alerted;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class MainDetailActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    static final public String EXTRA_MESSAGE = "us.alerted.alerted.MainActivity.MESSAGE";
    public SharedPreferences sharedPref;

    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Long id = bundle.getLong(EXTRA_MESSAGE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Alert alert = Alert.findById(Alert.class, id);

        RowItem rowItem = new RowItem(alert.getId(), alert.headline, alert.description,
                alert.certainty, alert.severity, alert.urgency, alert.category, alert.effective,
                alert.expires, alert.received);


        setContentView(R.layout.activity_main_detail);

        Calendar expiresDate = Calendar.getInstance();
        String formatted_expires = new String();
        String formatted_effective = new String();
        try {
            formatted_expires = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(dt.parse(alert.expires));
            formatted_effective = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(dt.parse(alert.effective));
            expiresDate.setTime(dt.parse(alert.expires));
            long msUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
            long msUntil = expiresDate.getTimeInMillis()-msUtc;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.headline)).setText(String.valueOf(alert.headline));
        ((TextView)findViewById(R.id.effective)).setText(formatted_effective);
        ((TextView)findViewById(R.id.expires)).setText(formatted_expires);
        ((TextView)findViewById(R.id.description)).setText(String.valueOf(alert.description));
        ((TextView)findViewById(R.id.instruction)).setText(String.valueOf(alert.instruction));
        ((ImageView)findViewById(R.id.urgency_image)).setImageResource(rowItem.getUrgencyImageId());
        ((ImageView)findViewById(R.id.severity_image)).setImageResource(rowItem.getSeverityImageId());
        ((ImageView)findViewById(R.id.certainty_image)).setImageResource(rowItem.getCertaintyImageId());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_detail, menu);
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
            Intent intent = new Intent(MainDetailActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
