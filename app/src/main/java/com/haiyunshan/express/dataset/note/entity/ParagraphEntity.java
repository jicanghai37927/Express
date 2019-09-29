package com.haiyunshan.express.dataset.note.entity;

import android.app.Activity;
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
public class ParagraphEntity extends Entity {

    public static final int ALIGN_START = View.TEXT_ALIGNMENT_TEXT_START;
    public static final int ALIGN_CENTER = View.TEXT_ALIGNMENT_CENTER;
    public static final int ALIGN_END = View.TEXT_ALIGNMENT_TEXT_END;

    String mText;

    StyleEntity mStyle;

    JSONArray mSpans;

    public static final ParagraphEntity create() {
        String id = UUIDUtils.next();
        String type = Entity.TYPE_PARAGRAPH;

        return new ParagraphEntity(id, type);
    }

    public ParagraphEntity(String id, String type) {
        super(id, type);

        this.mText = "";
        this.mStyle = new StyleEntity();
    }

    public ParagraphEntity(JSONObject json) {
        super(json);

        this.mText = json.optString("text", "");

        this.mStyle = new StyleEntity(json.optJSONObject("style"));

        this.mSpans = json.optJSONArray("spans");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        if (json == null) {
            return json;
        }

        try {
            json.put("text", mText);

            json.put("style", mStyle.toJson());

            if (mSpans != null) {
                json.put("spans", this.mSpans);
            }

        } catch (JSONException e) {
            json = null;
        }

        return json;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public void clearSpans() {
        this.mSpans = null;
    }

    public StyleEntity getStyle() {
        return this.mStyle;
    }

    public void setStyle(StyleEntity style) {
        this.mStyle = style;
    }

    public void addSpan(JSONObject obj) {
        if (this.mSpans == null) {
            this.mSpans = new JSONArray();
        }

        mSpans.put(obj);
    }

    public void attachStyle(Spannable text) {
        if (mSpans == null) {
            return;
        }

        Context context = App.instance();

        int size = mSpans.length();
        for (int i = 0; i < size; i++) {
            JSONObject obj = mSpans.optJSONObject(i);
            if (obj == null) {
                continue;
            }

            String type = obj.optString("t");
            if (TextUtils.isEmpty(type)) {
                continue;
            }

            int start = obj.optInt("s", -1);
            int end = obj.optInt("e", -1);
            if (start < 0 || end < 0 || start == end) {
                continue;
            }

            Object span = null;
            if (type.equalsIgnoreCase("hl")) {
                span = new HighlightSpan(context.getDrawable(R.drawable.bdreader_note_bg_brown_1));
            } else if (type.equalsIgnoreCase("s")) {
                String value = obj.optString("v", "");
                int v = Typeface.NORMAL;
                if (value.indexOf("bold") >= 0) {
                    v |= Typeface.BOLD;
                }
                if (value.indexOf("italic") >= 0) {
                    v |= Typeface.ITALIC;
                }
                if (v != Typeface.NORMAL) {
                    span = new StyleSpan(v);
                }
            }

            if (span != null) {
                text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
    }

    public static JSONObject toJSON(HighlightSpan span, int start, int end) {
        JSONObject json = null;

        try {
            json = toStyle(start, end);
            json.put("t", "hl");
        } catch (JSONException e) {
            json = null;
        }

        return json;
    }

    public static JSONObject toJSON(StyleSpan span, int start, int end) {
        JSONObject json = null;
        if ((span.getStyle() & Typeface.BOLD_ITALIC) == 0) {
            return json;
        }

        try {
            json = toStyle(start, end);
            json.put("t", "s");

            String style = null;
            int value = span.getStyle();
            if (value == Typeface.BOLD) {
                style = "bold";
            } else if (value == Typeface.ITALIC) {
                style = "italic";
            } else if (value == Typeface.BOLD_ITALIC) {
                style = "bold|italic";
            }

            json.put("v", style);

        } catch (JSONException e) {
            json = null;
        }

        return json;
    }

    static JSONObject toStyle(int start, int end) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("s", start);
        json.put("e", end);

        return json;
    }
}
