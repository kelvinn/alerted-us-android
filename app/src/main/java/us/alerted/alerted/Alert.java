package us.alerted.alerted;

import com.orm.SugarRecord;

/**
 * Created by kelvinn on 30/07/2014.
 */
public class Alert extends SugarRecord<Alert> {
    String headline;
    String urgency;
    String severity;
    String certainty;
    String effective;
    String expires;
    String description;
    String instruction;
    String category;
    String slug;
    String event;

    public Alert(){
    }

    public Alert(String headline, String urgency, String severity, String certainty,
                 String effective, String expires, String description,
                 String instruction, String category, String slug, String event){
        this.headline = headline;
        this.urgency = urgency;
        this.severity = severity;
        this.certainty = certainty;
        this.effective = effective;
        this.expires = expires;
        this.description = description;
        this.instruction = instruction;
        this.category = category;
        this.slug = slug;
        this.event = event;
    }
}
