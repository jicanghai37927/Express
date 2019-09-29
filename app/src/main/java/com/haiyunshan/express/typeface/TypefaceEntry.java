package com.haiyunshan.express.typeface;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 *
 */
public class TypefaceEntry {

    public static final int typeFile    = 0;
    public static final int typeSystem  = 1;
    public static final int typeAsset   = 2;

    String mId;
    String mName;
    String mUri;

    String mAlias;      // 备注名
    String mSource;     // 来源

    long mCreated;

    int mType;
    int mEnable;

    TypefaceEntry(String id, String name) {
        this.mId = id;
        this.mName = name;
        this.mUri = id + ".font";

        this.mAlias = name;
        this.mSource = "";

        this.mCreated = System.currentTimeMillis();

        this.mType = typeFile;
        this.mEnable = 1;

    }

    TypefaceEntry(JSONObject json) {
        this.mId = json.optString("id", "");
        this.mName = json.optString("name", "");
        this.mUri = json.optString("uri", "");

        this.mAlias = json.optString("alias", "");
        this.mSource = json.optString("source", "");

        this.mCreated = json.optLong("created", 0);

        this.mType = json.optInt("type", typeFile);
        this.mEnable = json.optInt("enable", 1);
        if (mType == typeFile) {
            if (TextUtils.isEmpty(mUri)) {
                mUri = mId + ".font";
            }
        }
    }

    JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", this.mId);
            json.put("name", this.mName);
            json.put("uri", this.mUri);

            json.put("alias", this.mAlias);
            json.put("source", this.mSource);

            json.put("created", this.mCreated);
        } catch (Exception e) {

        }

        return json;
    }

    public String getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public String getUri() {
        String uri = this.mUri;

        return uri;
    }

    public String getAlias() {
        return this.mAlias;
    }

    public void setAlias(String text) {
        this.mAlias = text;
    }

    public String getSource() {
        return this.mSource;
    }

    public void setSource(String text) {
        this.mSource = text;
    }

    public long getCreated() {
        return this.mCreated;
    }

    public int getType() {
        return this.mType;
    }

    public boolean isEnable() {
        return (mEnable != 0);
    }

}
