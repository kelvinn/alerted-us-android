package us.alerted.alerted;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by kelvinn on 30/07/2014.
 */
public class AlertNew extends SugarRecord<AlertNew> {

    @Expose
    private String cap_sender;

    @Expose
    private String cap_headline;

    @Expose
    private String cap_urgency;

    @Expose
    private String cap_severity;

    @Expose
    private String cap_certainty;

    @Expose
    private String cap_effective;

    @Expose
    private String cap_expires;

    @Expose
    private String cap_received;

    @Expose
    private String cap_description;

    @Expose
    private String cap_instruction;

    @Expose
    private String cap_category;

    @Expose
    private String cap_slug;

    @Expose
    private String cap_event;

    @Expose
    private List<Info> info_set;

    public String getCap_sender() {
        return cap_sender;
    }

    public void setCap_sender(String cap_sender) {
        this.cap_sender = cap_sender;
    }

    public String getCap_headline() {
        return cap_headline;
    }

    public void setCap_headline(String cap_headline) {
        this.cap_headline = cap_headline;
    }

    public String getCap_urgency() {
        return cap_urgency;
    }

    public void setCap_urgency(String cap_urgency) {
        this.cap_urgency = cap_urgency;
    }

    public String getCap_severity() {
        return cap_severity;
    }

    public void setCap_severity(String cap_severity) {
        this.cap_severity = cap_severity;
    }

    public String getCap_certainty() {
        return cap_certainty;
    }

    public void setCap_certainty(String cap_certainty) {
        this.cap_certainty = cap_certainty;
    }

    public String getCap_effective() {
        return cap_effective;
    }

    public void setCap_effective(String cap_effective) {
        this.cap_effective = cap_effective;
    }

    public String getCap_expires() {
        return cap_expires;
    }

    public void setCap_expires(String cap_expires) {
        this.cap_expires = cap_expires;
    }

    public String getCap_received() {
        return cap_received;
    }

    public void setCap_received(String cap_received) {
        this.cap_received = cap_received;
    }

    public String getCap_description() {
        return cap_description;
    }

    public void setCap_description(String cap_description) {
        this.cap_description = cap_description;
    }

    public String getCap_instruction() {
        return cap_instruction;
    }

    public void setCap_instruction(String cap_instruction) {
        this.cap_instruction = cap_instruction;
    }

    public String getCap_category() {
        return cap_category;
    }

    public void setCap_category(String cap_category) {
        this.cap_category = cap_category;
    }

    public String getCap_slug() {
        return cap_slug;
    }

    public void setCap_slug(String cap_slug) {
        this.cap_slug = cap_slug;
    }

    public String getCap_event() {
        return cap_event;
    }

    public void setCap_event(String cap_event) {
        this.cap_event = cap_event;
    }

    public List<Info> getInfo() {
        return info_set;
    }

    public void setInfo(List<Info> info_set) {
        this.info_set = info_set;
    }

}

