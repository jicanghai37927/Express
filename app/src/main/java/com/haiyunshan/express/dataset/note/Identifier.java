package com.haiyunshan.express.dataset.note;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Identifier {

    String mId;
    String mTemplateId;

    long mCreated;
    long mModified;

    public Identifier(String id, String templateId) {
        this.mId = id;
        this.mTemplateId = templateId;

        this.mCreated = System.currentTimeMillis();
        this.mModified = this.mCreated;
    }

    public Identifier(String id, JSONObject json) {
        if (json == null) {
            this.mId = id;
            this.mTemplateId = "note";

            this.mCreated = System.currentTimeMillis();
            this.mModified = this.mCreated;
        } else {
            this.mId = json.optString("id", "");
            this.mTemplateId = json.optString("templateId", "note");

            this.mCreated = json.optLong("created", System.currentTimeMillis());
            this.mModified = json.optLong("modified", this.mCreated);
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", this.mId);
            json.put("templateId", this.mTemplateId);

            json.put("created", this.mCreated);
            json.put("modified", this.mModified);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public String getId() {
        return this.mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getTemplateId() {
        return this.mTemplateId;
    }

    public long getCreated() {
        return this.mCreated;
    }

    public void setCreated(long time) {
        this.mCreated = time;
    }

    public long getModified() {
        return this.mModified;
    }

    public void setModified(long time) {
        this.mModified = time;
    }

}
