package us.alerted.alerted;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImgCardActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();

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
		setContentView(R.layout.activity_main_img);


        JSONObject recData = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        //List<Alert> alerts = Alert.listAll(Alert.class);


        List<Alert> alerts = Alert.find(Alert.class, null, null, null, "effective", "10");
        Collections.reverse(alerts);

        //find(Alert.class, "name = ? and title = ?", "satya", "title1");

        ArrayList<JSONObject> listdata = new ArrayList<JSONObject>();

        if (jsonArray != null) {
            for (int i=0;i<jsonArray.length();i++){
                //listdata.add(jArray.get(i).toString());
                listdata.add(jsonArray.optJSONObject(i));
            }
        }


        ListView lv = (ListView) findViewById(R.id.myListImg);
		 rowItems = new ArrayList<RowItem>();
		 
	        //Populate the List
	        for (int i = 0; i < alerts.size(); i++) {
//                jsonObj = listdata.get(i);
                //try {
                    //String title = jsonObj.get("cap_headline").toString();
                    //String description = jsonObj.get("cap_severity").toString();
                RowItem item = new RowItem(images[i], alerts.get(i).headline, alerts.get(i).certainty);
                rowItems.add(item);
                //} catch (JSONException e) {
                //    e.printStackTrace();
                //}
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

	        lv.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {


					// TODO Auto-generated method stub
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	}

}
