package com.haiyunshan.express.dataset.template;

import org.json.JSONObject;

/**
 *
 */
public class TemplateEntry {

    String mId;
    String mName;

    public TemplateEntry(JSONObject json) {
        this.mId = json.optString("id", "");
        this.mName = json.optString("name", "");
    }

    public String getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }
}
