package model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;

/**
 * Created by nkandavel on 7/7/16.
 */
public class LeadData {

    private final static ObjectMapper JSON = new ObjectMapper();
    static {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    private long lead_id;
    private String lead_date;
    private String lead_type;
    private String lead_email;

    public LeadData() {
    }

    public LeadData(long lead_id, String lead_date, String lead_type, String lead_email) {
        this.lead_id = lead_id;
        this.lead_date = lead_date;
        this.lead_type = lead_type;
        this.lead_email = lead_email;
    }

    public long getLead_id() {
        return lead_id;
    }

    public String getLead_date() {
        return lead_date;
    }

    public String getLead_type() {
        return lead_type;
    }

    public String getLead_email() {
        return lead_email;
    }

    public byte[] toJsonAsBytes() {
        try {
            return JSON.writeValueAsBytes(this);
        } catch (IOException e) {
            return null;
        }
    }

    public static LeadData fromJsonAsBytes(byte[] bytes) {
        try {
            return JSON.readValue(bytes, LeadData.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("ID %d: Date %s: Type %s: Email %s:",
                lead_id,lead_date,lead_type,lead_email);
    }
}
