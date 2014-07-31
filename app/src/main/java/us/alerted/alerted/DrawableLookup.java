package us.alerted.alerted;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kelvinn on 31/07/2014.
 */
public class DrawableLookup {


    public String urgency;

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

    public Integer getUrgency(String urgency) {
        return urgencyLookup.get(urgency);
    }

    public Integer getSeverity(String severity) {
        return severityLookup.get(severity);
    }

    public Integer getCertainty(String certainty) {
        return certaintyLookup.get(certainty);
    }
}
