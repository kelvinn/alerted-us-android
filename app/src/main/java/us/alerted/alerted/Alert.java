package us.alerted.alerted;

//import com.orm.SugarRecord;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.orm.SugarRecord;

/**
 * Created by kelvinn on 30/07/2014.
 */

/*
@DatabaseTable(tableName = "alerts")
public class Alert {
    @DatabaseField
    private String cap_headline;
    @DatabaseField
    private String cap_urgency;
    @DatabaseField
    private String cap_severity;
    @DatabaseField
    private String cap_certainty;
    @DatabaseField
    private String cap_effective;
    @DatabaseField
    private String cap_expires;
    @DatabaseField
    private String cap_description;
    @DatabaseField
    private String cap_instruction;
    @DatabaseField
    private String cap_category;
    @DatabaseField
    private String cap_slug;
    @DatabaseField
    private String cap_event;


    public Alert() {
        // ORMLite needs a no-arg constructor
    }
    public Alert(String name, String password) {
        this.cap_headline = cap_headline;
        this.cap_urgency = cap_urgency;
        this.cap_severity = cap_severity;
        this.cap_certainty = cap_certainty;
        this.cap_effective = cap_effective;
        this.cap_expires = cap_expires;
        this.cap_description = cap_description;
        this.cap_instruction = cap_instruction;
        this.cap_category = cap_category;
        this.cap_slug = cap_slug;
        this.cap_event = cap_event;
    }
    public String getHeadline() {
        return cap_headline;
    }
    public void setHeadline(String cap_headline) {
        this.cap_headline = cap_headline;
    }
    public String getUrgency() {
        return cap_urgency;
    }
    public void setUrgency(String cap_urgency) {
        this.cap_urgency = cap_urgency;
    }

    public String getSeverity() {
        return cap_severity;
    }
    public void setSeverity(String cap_severity) {
        this.cap_severity = cap_severity;
    }

    public String getCertainty() {
        return cap_certainty;
    }
    public void setCertainty(String cap_certainty) {
        this.cap_certainty = cap_certainty;
    }

    public String getEffective() {
        return cap_effective;
    }
    public void setEffective(String cap_effective) {
        this.cap_effective = cap_effective;
    }

}
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
