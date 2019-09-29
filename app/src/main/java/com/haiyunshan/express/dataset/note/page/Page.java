package com.haiyunshan.express.dataset.note.page;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Page {

    int mPaddingLeft;
    int mPaddingTop;
    int mPaddingRight;
    int mPaddingBottom;

    public Page() {
        this.mPaddingLeft = 20;
        this.mPaddingTop = 0;
        this.mPaddingRight = 20;
        this.mPaddingBottom = 0;
    }

    public Page(JSONObject json) {
        if (json == null) {

            {
                this.mPaddingLeft = 20;
                this.mPaddingTop = 0;
                this.mPaddingRight = 20;
                this.mPaddingBottom = 0;
            }
        } else {

            {
                this.mPaddingLeft = json.optInt("paddingLeft", 0);
                this.mPaddingTop = json.optInt("paddingTop", 0);
                this.mPaddingRight = json.optInt("paddingRight", 0);
                this.mPaddingBottom = json.optInt("paddingBottom", 0);
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("paddingLeft", this.mPaddingLeft);
            json.put("paddingTop", this.mPaddingTop);
            json.put("paddingRight", this.mPaddingRight);
            json.put("paddingBottom", this.mPaddingBottom);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    public int getPaddingLeft() {
        return this.mPaddingLeft;
    }

    public int getPaddingTop() {
        return this.mPaddingTop;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public int getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
    }

    public void setPaddingLeft(int left) {
        this.setPadding(left, mPaddingTop, mPaddingRight, mPaddingBottom);
    }

    public void setPaddingRight(int right) {
        this.setPadding(mPaddingLeft, mPaddingTop, right, mPaddingBottom);
    }
}
