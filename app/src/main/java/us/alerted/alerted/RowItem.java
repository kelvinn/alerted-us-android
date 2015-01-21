package us.alerted.alerted;

import android.util.Log;


import com.ocpsoft.pretty.time.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RowItem {

    Map<String,Integer> urgencyLookup = new HashMap<String, Integer>();
    {
        urgencyLookup.put("Immediate", R.drawable.fourbar);
        urgencyLookup.put("Expected",R.drawable.threebar);
        urgencyLookup.put("Future",R.drawable.threebar);
        urgencyLookup.put("Past",R.drawable.onebar);
        urgencyLookup.put("Unknown",R.drawable.nobar);
    }

    Map<String,Integer> severityLookup = new HashMap<String, Integer>();
    {
        severityLookup.put("Extreme", R.drawable.fourbar);
        severityLookup.put("Severe",R.drawable.threebar);
        severityLookup.put("Moderate",R.drawable.twobar);
        severityLookup.put("Minor",R.drawable.onebar);
        severityLookup.put("Unknown",R.drawable.nobar);
    }

    Map<String,Integer> certaintyLookup = new HashMap<String, Integer>();
    {
        certaintyLookup.put("Observed", R.drawable.fourbar);
        certaintyLookup.put("Likely",R.drawable.threebar);
        certaintyLookup.put("Possible",R.drawable.twobar);
        certaintyLookup.put("Unlikely",R.drawable.onebar);
        certaintyLookup.put("Unknown",R.drawable.nobar);
    }

    Map<String,Integer> categoryLookup = new HashMap<String, Integer>();
    {
        categoryLookup.put("Geo", R.drawable.geo);
        categoryLookup.put("Met",R.drawable.met);
        categoryLookup.put("Safety",R.drawable.safety);
        categoryLookup.put("Security",R.drawable.security);
        categoryLookup.put("Rescue",R.drawable.rescue);
        categoryLookup.put("Fire",R.drawable.fire);
        categoryLookup.put("Health",R.drawable.health);
        categoryLookup.put("Env",R.drawable.env);
        categoryLookup.put("Transport",R.drawable.transport);
        categoryLookup.put("Infra",R.drawable.infra);
        categoryLookup.put("CBRNE",R.drawable.cbrne);
        categoryLookup.put("Other",R.drawable.other);
    }

    private String TAG = this.getClass().getSimpleName();
    private int imageId;
    private Long id;
    private String title;
    private String description;
    private int desc_cap_severity;
    private int desc_cap_urgency;
    private int desc_cap_certainty;
    private int desc_cap_category;
    private String effective;
    public PrettyTime p = new PrettyTime();
    String pretty_formatted;

    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    //private int urgencyImageId;
    // TODO image to be put in when map is ready
    // public RowItem(int imageId, String title, String desc) {
    public RowItem(Long id, String title, String description, String desc_cap_certainty,
                   String desc_cap_severity, String desc_cap_urgency, String desc_cap_category,
                   String effective) {
        //this.imageId = imageId;
        this.title = title;
        this.id = id;

        String formatted_effective = new String();
        try {
            formatted_effective = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(dt.parse(effective));
            pretty_formatted = p.format(dt.parse(effective));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //this.description = desc;
        if(description.length() > 250){
            this.description = description.substring(0,249) + "...";
        }
        else {
            this.description = description;
        }
        this.desc_cap_urgency = urgencyLookup.get(desc_cap_urgency);
        this.desc_cap_severity = severityLookup.get(desc_cap_severity);
        this.desc_cap_certainty = certaintyLookup.get(desc_cap_certainty);
        this.desc_cap_category = categoryLookup.get(desc_cap_category);
        Log.i("Alerted", formatted_effective);

        this.effective = "Effective " + pretty_formatted;

        //this.urgencyImageId = urgencyImageId;
    }
    /* TODO image to be put in when map is ready
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    */
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return description;
    }
    public void setDesc(String desc) {
        this.description = desc;
    }

    public int getUrgencyImageId() {
        return desc_cap_urgency;
    }
    public void setDescCapUrgency(int desc_cap_urgency) {
        this.desc_cap_urgency = desc_cap_urgency;
    }

    public int getSeverityImageId() {
        return desc_cap_severity;
    }
    public void setDescCapSeverity(int desc_cap_severity) {
        this.desc_cap_severity = desc_cap_severity;
    }

    public int getCertaintyImageId() {
        return desc_cap_certainty;
    }
    public void setDescCapCertainty(int desc_cap_certainty) {
        this.desc_cap_certainty = desc_cap_certainty;
    }

    public int getCategoryImageId() {
        return desc_cap_category;
    }

    public String getTitle() {
        return title;
    }

    public String getEffective() {
        return this.effective;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return title + "\n" + description;
    }
}
