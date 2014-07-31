package us.alerted.alerted;

import android.util.Log;

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


    private String TAG = this.getClass().getSimpleName();
    private int imageId;
    private Long id;
    private String title;
    private String description;
    private int desc_cap_severity;
    private int desc_cap_urgency;
    private int desc_cap_certainty;
    DrawableLookup drawableLookup;

    //private int urgencyImageId;
    // TODO image to be put in when map is ready
    // public RowItem(int imageId, String title, String desc) {
    public RowItem(Long id, String title, String desc_cap_certainty, String desc_cap_severity, String desc_cap_urgency) {
        //this.imageId = imageId;
        this.title = title;
        this.id = id;
        //this.description = desc;
        this.desc_cap_urgency = urgencyLookup.get(desc_cap_urgency);
        this.desc_cap_severity = severityLookup.get(desc_cap_severity);
        this.desc_cap_certainty = certaintyLookup.get(desc_cap_certainty);


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

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return title + "\n" + description;
    }
}
