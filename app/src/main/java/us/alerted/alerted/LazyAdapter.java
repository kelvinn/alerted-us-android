package us.alerted.alerted;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

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
        ImageView image_category;
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
            holder.description = (TextView)convertView.findViewById(R.id.description);
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
        holder.image_severity.setImageResource(rowItem.getSeverityImageId());
        holder.image_certainty.setImageResource(rowItem.getCertaintyImageId());
        holder.image_urgency.setImageResource(rowItem.getUrgencyImageId());
        holder.image_category.setImageResource(rowItem.getCategoryImageId());

        // Enable the below to have animated cards
        //Animation animation = AnimationUtils.loadAnimation(context, R.anim.card_animation);
        //holder.card.startAnimation(animation);
        
        
        return convertView;
    }
}
