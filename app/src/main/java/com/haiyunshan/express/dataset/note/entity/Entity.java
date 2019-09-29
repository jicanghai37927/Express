package com.haiyunshan.express.dataset.note.entity;

import android.text.TextUtils;

import com.haiyunshan.express.app.UUIDUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Entity {

    public static final String TYPE_PARAGRAPH   = "paragraph";
    public static final String TYPE_STOP        = "stop";
    public static final String TYPE_PICTURE     = "picture";

    String mId;     //
    String mType;   //

    long mCreated;  //
    long mModified; //

    Entity(String id, String type) {
        this.mId = id;
        this.mType = type;

        this.mCreated = System.currentTimeMillis();
        this.mModified = this.mCreated;
    }

    Entity(JSONObject json) {
        this.mId = json.optString("id", "");
        this.mType = json.optString("type", "");

        this.mCreated = json.optLong("created", -1);
        this.mModified = json.optLong("modified", -1);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", mId);
            json.put("type", mType);

            json.put("created", mCreated);
            json.put("modified", mModified);
        } catch (JSONException e) {
            json = null;
        }


        return json;
    }

    public String getId() {
        return this.mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public long getCreated() {
        return mCreated;
    }

    public void setCreated(long time) {
        this.mCreated = time;
    }

    public long getModified() {
        return mModified;
    }

    public void setModified(long time) {
        this.mModified = time;
    }

}
