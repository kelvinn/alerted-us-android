package us.alerted.alerted;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LazyAdapter extends ArrayAdapter<RowItem> {

    Context context;
    PrettyTime p = new PrettyTime();
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    Date formatted_effective;
    Date formatted_expires;

    public LazyAdapter(Context context, int resourceId, List<RowItem> items){
        super(context, resourceId, items);
        this.context = context;
    }

    public class ViewHolder{
        // TODO image to be put in when map is ready
        // ImageView image;
        TextView title;
        TextView description;
        TextView received;
        TextView status;
        ImageView image_severity;
        ImageView image_urgency;
        ImageView image_certainty;
        ImageView image_category;
        LinearLayout card;
    }

    public static boolean isAlertActive(String effective, String expires) {

        return true;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        RowItem rowItem = getItem(position);



        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){


            convertView = mInflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.card = (LinearLayout) convertView.findViewById(R.id.card);
            // TODO image to be put in when map is ready
            // holder.image = (ImageView)convertView.findViewById(R.id.list_image);
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.description = (TextView)convertView.findViewById(R.id.description);
            holder.status = (TextView)convertView.findViewById(R.id.status);
            holder.received = (TextView)convertView.findViewById(R.id.received);
            holder.image_urgency = (ImageView)convertView.findViewById(R.id.urgency_image);
            holder.image_certainty = (ImageView)convertView.findViewById(R.id.certainty_image);
            holder.image_severity = (ImageView)convertView.findViewById(R.id.severity_image);
            holder.image_category = (ImageView)convertView.findViewById(R.id.category_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder)convertView.getTag();

        // TODO image to be put in when map is ready
        // holder.image.setImageResource(rowItem.getImageId());
        holder.title.setText(rowItem.getTitle());
        holder.description.setText(rowItem.getDesc());

        try {
            formatted_effective = dt.parse(rowItem.getEffective());
            formatted_expires = dt.parse(rowItem.getExpires());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Compare expired time to current time to see if alerts is expired or not
        if (formatted_expires.getTime() > System.currentTimeMillis()
                && System.currentTimeMillis() > formatted_effective.getTime()) {
            holder.status.setBackgroundResource(R.color.red_alerts);
            holder.status.setTextColor(Color.parseColor("#F5F5F5"));
            holder.status.setText("Active");
        } else {
            holder.status.setBackgroundResource(R.color.wallet_holo_blue_light);
            holder.status.setTextColor(Color.parseColor("#F5F5F5"));
            holder.status.setText("Inactive");
        }


        holder.received.setText(rowItem.getReceived());
        holder.image_severity.setImageResource(rowItem.getSeverityImageId());
        holder.image_certainty.setImageResource(rowItem.getCertaintyImageId());
        holder.image_urgency.setImageResource(rowItem.getUrgencyImageId());
        holder.image_category.setImageResource(rowItem.getCategoryImageId());

        return convertView;
        }
}
