package us.alerted.alerted;

/**
 * Created by kelvin on 1/03/15.
 */
import com.google.gson.annotations.Expose;


public class Info {

    @Expose
    private String cap_language;
    @Expose
    private String cap_category;
    @Expose
    private String cap_event;
    @Expose
    private String cap_response_type;
    @Expose
    private String cap_urgency;
    @Expose
    private String cap_severity;
    @Expose
    private String cap_certainty;
    @Expose
    private String cap_audience;
    @Expose
    private String cap_event_code;
    @Expose
    private String cap_effective;
    @Expose
    private String cap_onset;
    @Expose
    private String cap_expires;
    @Expose
    private String cap_sender_name;
    @Expose
    private String cap_headline;
    @Expose
    private String cap_description;
    @Expose
    private String cap_instruction;
    @Expose
    private String cap_link;
    @Expose
    private String cap_contact;


    public String getCap_language() {
        return cap_language;
    }

    public void setCap_language(String cap_language) {
        this.cap_language = cap_language;
    }

    public String getCap_category() {
        return cap_category;
    }

    public void setCap_category(String cap_category) {
        this.cap_category = cap_category;
    }

}