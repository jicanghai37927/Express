package com.haiyunshan.express.dataset.note;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 笔记条目
 *
 */
public class NoteEntry {

    String mId;
    String mName;
    String mDesc;

    String mCatalog; // 目录

    long mCreated;
    long mModified;
    long mDeleted;

    public NoteEntry(String id, String name, String desc) {
       this(id, name, desc, System.currentTimeMillis());
    }

    public NoteEntry(String id, String name, String desc, long time) {
        this.mId = id;
        this.mName = name;
        this.mDesc = desc;

        this.mCatalog = "";

        this.mCreated = time;
        this.mModified = time;
        this.mDeleted = -1;
    }

    NoteEntry(JSONObject json) {
        this.mId = json.optString("id", "");
        this.mName = json.optString("name", "");
        this.mDesc = json.optString("desc", "");

        this.mCatalog = json.optString("catalog", "");

        this.mCreated = json.optLong("created", System.currentTimeMillis());
        this.mModified = json.optLong("modified", System.currentTimeMillis());
        this.mDeleted = json.optLong("deleted", -1);

    }

    JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", this.mId);
            json.put("name", this.mName);
            json.put("desc", this.mDesc);

            json.put("catalog", this.mCatalog);

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

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String value) {
        this.mName = value;
    }

    public String getDesc() {
        return this.mDesc;
    }

    public void setDesc(String value) {
        this.mDesc = value;
    }

    public String getCatalog() {
        return this.mCatalog;
    }

    public void setCatalog(String value) {
        this.mCatalog = value;
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

    public long getDeleted() {
        return this.mDeleted;
    }

    public void setDeleted(long time) {
        this.mDeleted = time;
    }
}
