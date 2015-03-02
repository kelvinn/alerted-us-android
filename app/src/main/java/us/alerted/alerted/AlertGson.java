package us.alerted.alerted;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by kelvinn on 30/07/2014.
 */
public class AlertGson {





    @Expose
    private String cap_sender;

    @Expose
    private String cap_received;

    @Expose
    private String cap_slug;

    @Expose
    private String contributor;

    @Expose
    private String cap_incidents;

    @Expose
    private String cap_code;

    @Expose
    private String cap_addresses;

    @Expose
    private String cap_restriction;

    @Expose
    private String cap_scope;

    @Expose
    private String cap_source;

    @Expose
    private String cap_message_type;

    @Expose
    private String cap_status;

    @Expose
    private String cap_sent;

    @Expose
    private String cap_id;

    @Expose
    private List<InfoGson> info_set;

    public String getCap_sender() {
        return cap_sender;
    }

    public void setCap_sender(String cap_sender) {
        this.cap_sender = cap_sender;
    }

    public String getCap_received() {
        return cap_received;
    }

    public void setCap_received(String cap_received) {
        this.cap_received = cap_received;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getCap_slug() {
        return cap_slug;
    }

    public void setCap_slug(String cap_slug) {
        this.cap_slug = cap_slug;
    }

    public String getCap_incidents() {
        return cap_incidents;
    }

    public void setCap_incidents(String cap_incidents) {
        this.cap_incidents = cap_incidents;
    }

    public String getCap_source() {
        return cap_source;
    }

    public void setCap_source(String cap_source) {
        this.cap_source = cap_source;
    }




    public String getCap_code() {
        return cap_code;
    }

    public void setCap_code(String cap_code) {
        this.cap_code = cap_code;
    }

    public String getCap_addresses() { return cap_addresses; }

    public void setCap_addresses(String cap_addresses) {
        this.cap_addresses = cap_addresses;
    }

    public String getCap_restriction() { return cap_restriction; }

    public void setCap_restriction(String cap_restriction) {
        this.cap_restriction = cap_restriction;
    }

    public String getCap_scope() {
        return cap_scope;
    }

    public void setCap_scope(String cap_scope) {
        this.cap_scope = cap_scope;
    }

    public String getCap_status() {
        return cap_status;
    }

    public void setCap_status(String cap_status) {
        this.cap_status = cap_status;
    }

    public String getCap_sent() {
        return cap_sent;
    }

    public void setCap_sent(String cap_sent) {
        this.cap_sent = cap_sent;
    }

    public String getCap_id() {
        return cap_id;
    }

    public void setCap_id(String cap_id) {
        this.cap_id = cap_id;
    }




    public List<InfoGson> getInfo() {
        return info_set;
    }

    public void setInfo(List<InfoGson> info_set) {
        this.info_set = info_set;
    }

}

