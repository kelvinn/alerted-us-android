package us.alerted.alerted;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

public class ImgCardActivity extends Activity {

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

		ListView lv = (ListView) findViewById(R.id.myListImg);
		 rowItems = new ArrayList<RowItem>();
		 
	        String[] titles = {"Flood Advisory issued July 29 at 7:35PM MDT until July 29 at 10:30PM MDT by NWS Pueblo","Movie2","白河水庫:調節性放水","Movie4","Movie5","Flood Advisory","Movie7","Movie8"};
	        String[] descriptions = {"How likely: XXXX   How soon: XXXX   How Severe: XXXX","Second movie","Third Movie","Fourth Movie","Fifth Movie",
	        		"Sixth Movie","Seventh Movie","Eighth Movie"};
	        //Populate the List
	        for (int i = 0; i < titles.length; i++) {
	            RowItem item = new RowItem(images[i], titles[i], descriptions[i]);
	            rowItems.add(item);
	        }


	        // Set the adapter on the ListView
	        LazyAdapter adapter = new LazyAdapter(getApplicationContext(), R.layout.list_row, rowItems);
	        lv.setAdapter(adapter);
	        
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
