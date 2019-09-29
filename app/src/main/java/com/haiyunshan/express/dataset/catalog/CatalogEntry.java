package com.haiyunshan.express.dataset.catalog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class CatalogEntry {

    String mId;
    String mName;

    long mCreated;
    long mModified;
    long mDeleted;

    boolean mEditable;  // 是否可以编辑
    int mSort;          // 排序值

    public CatalogEntry(String id, String name) {
        this.mId = id;
        this.mName = name;

        this.mCreated = System.currentTimeMillis();
        this.mModified = mCreated;
        this.mDeleted = -1;
    }

    CatalogEntry(JSONObject json) {
        this.mId = json.optString("id", "");
        this.mName = json.optString("name", "");

        this.mCreated = json.optLong("created", System.currentTimeMillis());
        this.mModified = json.optLong("modified", System.currentTimeMillis());
        this.mDeleted = json.optLong("deleted", -1);

    }

    JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", this.mId);
            json.put("name", this.mName);

            json.put("created", this.mCreated);
            json.put("modified", this.mModified);
            json.put("deleted", this.mDeleted);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public String getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public long getCreated() {
        return this.mCreated;
    }

    public long getModified() {
        return this.mModified;
    }

    public void setModified(long time) {
        this.mModified = time;
    }

    public boolean isEditable() {
        return this.mEditable;
    }

    void setEditable(boolean editable) {
        this.mEditable = editable;
    }

    public int getSort() {
        return this.mSort;
    }

    void setSort(int sort) {
        this.mSort = sort;
    }

    public long getDeleted() {
        return this.mDeleted;
    }

    public void setDeleted(long time) {
        this.mDeleted = time;
    }
}
