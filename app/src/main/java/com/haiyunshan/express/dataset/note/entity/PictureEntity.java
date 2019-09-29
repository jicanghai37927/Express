package com.haiyunshan.express.dataset.note.entity;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;

import com.haiyunshan.express.App;
import com.haiyunshan.express.R;
import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.style.highlight.HighlightSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class PictureEntity extends Entity {

    String mName;
    String mSource;
    int mWidth;
    int mHeight;

    public static final PictureEntity create() {
        String id = UUIDUtils.next();
        String type = Entity.TYPE_PICTURE;

        return new PictureEntity(id, type);
    }

    public PictureEntity(String id, String type) {
        super(id, type);

        this.mName = "";
        this.mSource = "";
        this.mWidth = 0;
        this.mHeight = 0;
    }

    public PictureEntity(JSONObject json) {
        super(json);

        this.mName = json.optString("name", "");
        this.mSource = json.optString("src", "");
        this.mWidth = json.optInt("width", 0);
        this.mHeight = json.optInt("height", 0);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        if (json == null) {
            return json;
        }

        try {
            json.put("name", mName);
            json.put("src", mSource);
            json.put("width", mWidth);
            json.put("height", mHeight);

        } catch (JSONException e) {
            json = null;
        }

        return json;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setSource(String src, int width, int height) {
        this.mSource = src;
        this.mWidth = width;
        this.mHeight = height;
    }

    public String getName() {
        return mName;
    }

    public String getSource() {
        return mSource;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
