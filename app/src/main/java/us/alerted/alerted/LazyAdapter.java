package us.alerted.alerted;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LazyAdapter extends ArrayAdapter<RowItem> {

    Context context;



    public LazyAdapter(Context context, int resourceId, List<RowItem> items){
        super(context, resourceId, items);
        this.context = context;
    }

    public class ViewHolder{
        // TODO image to be put in when map is ready
        // ImageView image;
        TextView title;
        TextView description;
        ImageView image_severity;
        ImageView image_urgency;
        ImageView image_certainty;
        LinearLayout card;
        //ImageView urgencyImageId;
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
            //holder.description = (TextView)convertView.findViewById(R.id.description);
            //holder.desc_cap_severity = (TextView)convertView.findViewById(R.id.desc_cap_severity);
            //holder.desc_cap_urgency = (TextView)convertView.findViewById(R.id.desc_cap_urgency);
            holder.image_urgency = (ImageView)convertView.findViewById(R.id.urgency_image);
            holder.image_certainty = (ImageView)convertView.findViewById(R.id.certainty_image);
            holder.image_severity = (ImageView)convertView.findViewById(R.id.severity_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder)convertView.getTag();

        // TODO image to be put in when map is ready
        // holder.image.setImageResource(rowItem.getImageId());
        holder.title.setText(rowItem.getTitle());
        //holder.description.setText(rowItem.getDesc());
        //holder.desc_cap_severity.setText(rowItem.getDescCapSeverity());
        //holder.desc_cap_urgency.setText(rowItem.getDescCapUrgency());
        holder.image_severity.setImageResource(rowItem.getSeverityImageId());
        holder.image_certainty.setImageResource(rowItem.getCertaintyImageId());
        holder.image_urgency.setImageResource(rowItem.getUrgencyImageId());
        //holder.desc_cap_urgency = (ImageView)convertView.findViewById(R.id.urgency_image);
        //holder.desc_cap_certainty = (ImageView)convertView.findViewById(R.id.certainty_image);
        //holder.desc_cap_severity = (ImageView)convertView.findViewById(R.id.severity_image);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.card_animation);
        holder.card.startAnimation(animation);
        
        
        return convertView;
    }
}
