package us.alerted.alerted;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import us.alerted.alerted.adapters.BaseInflaterAdapter;
import us.alerted.alerted.adapters.CardItemData;
import us.alerted.alerted.adapters.inflaters.CardInflater;

public class HelloCardListActivity extends Activity
{
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);

		ListView list = (ListView)findViewById(R.id.list_view);

		list.addHeaderView(new View(this));
		list.addFooterView(new View(this));

		BaseInflaterAdapter<CardItemData> adapter = new BaseInflaterAdapter<CardItemData>(new CardInflater());
		for (int i = 0; i < 50; i++)
		{
			CardItemData data = new CardItemData("Item " + i + " Line 1", "Item " + i + " Line 2", "Item " + i + " Line 3");
			adapter.addItem(data, false);
		}

		list.setAdapter(adapter);
	}
}
