package com.haiyunshan.express.dataset.note.style;

import android.graphics.Color;
import android.text.TextUtils;

import com.haiyunshan.express.dataset.note.entity.ParagraphEntity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 *
 */
public class StyleEntity {

    public static final int ALIGN_START = ParagraphEntity.ALIGN_START;
    public static final int ALIGN_CENTER = ParagraphEntity.ALIGN_CENTER;
    public static final int ALIGN_END = ParagraphEntity.ALIGN_END;


    String mId = "";                    // 唯一ID
    String mName = "";                  // 名称

    String mTypeface = null;            // 字体

    Integer mTextColor = null;          // 文本颜色
    Integer mTextSize = null;           // 文字大小，单位：SP

    Boolean mBold = null;               // 粗体
    Boolean mItalic = null;             // 斜体
    Boolean mUnderline = null;          //

    Integer mAlignment = null;          // 对齐方式
    Boolean mWrapContent = null;        //

    Integer mLetterMultiplier = null;   // 字大小，%
    Integer mLineMultiplier = null;     // 行大小，%

    public StyleEntity() {

    }

    public StyleEntity(StyleEntity style) {

        this.mId = style.mId;
        this.mName = style.mName;

        if (style.mTypeface != null) {
            this.mTypeface = new String(style.mTypeface);
        }

        if (style.mTextColor != null) {
            this.mTextColor = new Integer(style.mTextColor);
        }
        if (style.mTextSize != null) {
            this.mTextSize = new Integer(style.mTextSize);
        }

        if (style.mBold != null) {
            this.mBold = new Boolean(style.mBold);
        }
        if (style.mItalic != null) {
            this.mItalic = new Boolean(style.mItalic);
        }
        if (style.mUnderline != null) {
            this.mUnderline = new Boolean(style.mUnderline);
        }

        if (style.mAlignment != null) {
            this.mAlignment = new Integer(style.mAlignment);
        }
        if (style.mWrapContent != null) {
            this.mWrapContent = new Boolean(style.mWrapContent);
        }

        if (style.mLetterMultiplier != null) {
            this.mLetterMultiplier = new Integer(style.mLetterMultiplier);
        }
        if (style.mLineMultiplier != null) {
            this.mLineMultiplier = new Integer(style.mLineMultiplier);
        }

    }

    public StyleEntity(JSONObject json) {
        if (json == null) {

        } else {
            this.mId = json.optString("id", "");
            this.mName = json.optString("name", "");

            if (json.has("typeface")) {
                this.mTypeface = json.optString("typeface", "system");
                if (mTypeface.equalsIgnoreCase("default")) {
                    mTypeface = "system";
                }
            }

            if (json.has("textSize")) {
                this.mTextSize = json.optInt("textSize", 0);
            }
            if (json.has("textColor")) {
                String color = json.optString("textColor", "");
                if (TextUtils.isEmpty(color)) {
                    this.mTextColor = Color.BLACK;
                } else {
                    if (color.startsWith("#")) {
                        color = color.substring(1);

                        if (color.length() != 8) {
                            this.mTextColor = Integer.parseInt(color, 16);
                            mTextColor |= 0xff000000;
                        } else {
                            int alpha = (Integer.parseInt(color.substring(0, 2), 16));
                            mTextColor = (alpha << 24) | (Integer.parseInt(color.substring(2), 16));
                        }

                    } else {
                        this.mTextColor = Color.BLACK;
                    }
                }
            }

            if (json.has("bold")) {
                this.mBold = json.optBoolean("bold", false);
            }
            if (json.has("italic")) {
                this.mItalic = json.optBoolean("italic", false);
            }
            if (json.has("underline")) {
                this.mUnderline = json.optBoolean("underline", false);
            }

            if (json.has("align")) {

                String align = json.optString("align", "");
                if (TextUtils.isEmpty(align)) {
                    mAlignment = ALIGN_START;
                } else if (align.equalsIgnoreCase("start")) {
                    mAlignment = ALIGN_START;
                } else if (align.equalsIgnoreCase("center")) {
                    mAlignment = ALIGN_CENTER;
                } else if (align.equalsIgnoreCase("end")) {
                    mAlignment = ALIGN_END;
                } else {
                    mAlignment = ALIGN_START;
                }
            }

            if (json.has("wrapContent")) {
                this.mWrapContent = json.optBoolean("wrapContent", false);
            }

            if (json.has("letterMultiplier")) {
                this.mLetterMultiplier = (json.optInt("letterMultiplier", 0));
            }
            if (json.has("lineMultiplier")) {
                this.mLineMultiplier = (json.optInt("lineMultiplier", 100));
            }

        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            if (!TextUtils.isEmpty(this.mId)) {
                json.put("id", this.mId);
            }

            if (!TextUtils.isEmpty(mName)) {
                json.put("name", this.mName);
            }

            if (mTypeface != null) {
                json.put("typeface", mTypeface);
            }

            if (mTextSize != null) {
                json.put("textSize", mTextSize);
            }
            if (mTextColor != null) {
                json.put("textColor", "#" + Integer.toHexString(mTextColor));
            }

            if (mBold != null) {
                json.put("bold", mBold);
            }
            if (mItalic != null) {
                json.put("italic", mItalic);
            }
            if (mUnderline != null) {
                json.put("underline", mUnderline);
            }

            if (mAlignment != null) {
                String align = "start";
                align = (mAlignment == ALIGN_START)? "start": align;
                align = (mAlignment == ALIGN_CENTER)? "center": align;
                align = (mAlignment == ALIGN_END)? "end": align;

                json.put("align", align);
            }
            if (mWrapContent != null) {
                json.put("wrapContent", mWrapContent);
            }

            if (mLetterMultiplier != null) {
                json.put("letterMultiplier", mLetterMultiplier);
            }
            if (mLineMultiplier != null) {
                json.put("lineMultiplier", mLineMultiplier);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public void set(StyleEntity style) {

        this.mTypeface = (style.mTypeface != null)? new String(style.mTypeface): mTypeface;

        this.mTextColor = (style.mTextColor != null)? new Integer(style.mTextColor): mTextColor;
        this.mTextSize = (style.mTextSize != null)? new Integer(style.mTextSize): mTextSize;

        this.mBold = (style.mBold != null)? new Boolean(style.mBold): mBold;
        this.mItalic = (style.mItalic != null)? new Boolean(style.mItalic): mItalic;
        this.mUnderline = (style.mUnderline != null)? new Boolean(style.mUnderline): mUnderline;

        this.mAlignment = (style.mAlignment != null)? new Integer(style.mAlignment): mAlignment;
        this.mWrapContent = (style.mWrapContent != null)? new Boolean(style.mWrapContent): mWrapContent;

        this.mLetterMultiplier = (style.mLetterMultiplier != null)? new Integer(style.mLetterMultiplier): mLetterMultiplier;
        this.mLineMultiplier = (style.mLineMultiplier != null)? new Integer(style.mLineMultiplier): mLineMultiplier;
    }

    public void clear() {
        this.mTypeface = null;          // 字体

        this.mTextColor = null;        // 文本颜色
        this.mTextSize = null;         // 文字大小，单位：SP

        this.mBold = null;               // 粗体
        this.mItalic = null;             // 斜体
        this.mUnderline = null;          //

        this.mAlignment = null;         // 对齐方式
        this.mWrapContent = null;       //

        this.mLetterMultiplier = null;   // 字大小，%
        this.mLineMultiplier = null;     // 行大小，%

    }

    public void fill() {
        this.mTypeface = this.getTypefaceValue();

        mTextSize = this.getTextSizeValue();
        mTextColor = this.getTextColorValue();

        mBold = this.isBoldValue();
        mItalic = this.isItalicValue();
        mUnderline = this.isUnderlineValue();

        mAlignment = this.getAlignmentValue();
        mWrapContent = this.isWrapContentValue();

        mLetterMultiplier = this.getLetterMultiplierValue();
        mLineMultiplier = this.getLineMultiplierValue();

    }

    public boolean isSet() {
        return mTypeface != null
                || mTextColor != null
                || mTextSize != null
                || mBold != null
                || mItalic != null
                || mUnderline != null
                || mAlignment != null
                || mWrapContent != null
                || mLetterMultiplier != null
                || mLineMultiplier != null;
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

    public void setName(String name) {
        this.mName = name;
    }

    public String getTypeface() {
        return mTypeface;
    }

    public void setTypeface(String typeface) {
        this.mTypeface = typeface;
    }

    public Integer getTextColor() {
        return this.mTextColor;
    }

    public void setTextColor(int value) {
        this.mTextColor = value;
    }

    public Integer getTextSize() {
        return this.mTextSize;
    }

    public void setTextSize(int size) {
        this.mTextSize = size;
    }

    public Boolean isBold() {
        return this.mBold;
    }

    public void setBold(boolean bold) {
        this.mBold = bold;
    }

    public Boolean isItalic() {
        return this.mItalic;
    }

    public void setItalic(boolean italic) {
        this.mItalic = italic;
    }

    public Boolean isUnderline() {
        return this.mUnderline;
    }

    public void setUnderline(boolean value) {
        this.mUnderline = value;
    }

    public Integer getAlignment() {
        return this.mAlignment;
    }

    public void setAlignment(int align) {
        this.mAlignment = align;
    }

    public Boolean isWrapContent() {
        return this.mWrapContent;
    }

    public void setWrapContent(boolean value) {
        this.mWrapContent = value;
    }

    public Integer getLetterMultiplier() {
        return mLetterMultiplier;
    }

    public void setLetterMultiplier(int value) {
        this.mLetterMultiplier = value;
    }

    public Integer getLineMultiplier() {
        return mLineMultiplier;
    }

    public void setLineMultiplier(int value) {
        this.mLineMultiplier = value;
    }

    String getTypefaceValue() {
        if (TextUtils.isEmpty(mTypeface)) {
            return "system";
        }

        return mTypeface;
    }

    int getTextColorValue() {
        if (mTextColor == null) {
            return Color.BLACK;
        }

        return this.mTextColor;
    }

    int getTextSizeValue() {
        if (mTextSize == null) {
            return 18;
        }

        return this.mTextSize.intValue();
    }

    boolean isBoldValue() {
        if (mBold == null) {
            return false;
        }

        return this.mBold.booleanValue();
    }

    boolean isItalicValue() {
        if (mItalic == null) {
            return false;
        }

        return this.mItalic.booleanValue();
    }

    boolean isUnderlineValue() {
        if (mUnderline == null) {
            return false;
        }

        return this.mUnderline.booleanValue();
    }

    int getAlignmentValue() {
        if (mAlignment == null) {
            return ALIGN_START;
        }

        return this.mAlignment.intValue();
    }

    boolean isWrapContentValue() {
        if (mWrapContent == null) {
            return false;
        }

        return this.mWrapContent.booleanValue();
    }

    int getLetterMultiplierValue() {
        if (mLetterMultiplier == null) {
            return 0;
        }

        return mLetterMultiplier.intValue();
    }

    int getLineMultiplierValue() {
        if (mLineMultiplier == null) {
            return 100;
        }

        return mLineMultiplier.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StyleEntity)) {
            return false;
        }

        StyleEntity style = (StyleEntity)obj;

        if (!TextUtils.isEmpty(this.mTypeface)
                && !TextUtils.isEmpty(style.mTypeface)
                && !(this.getTypefaceValue().equalsIgnoreCase(style.getTypefaceValue()))) {
            return false;
        }

        if (this.mTextSize != null
                && style.mTextSize != null
                && this.getTextSizeValue() != style.getTextSizeValue()) {
            return false;
        }
        if (this.mTextColor != null
                && style.mTextColor != null
                && this.getTextColorValue() != style.getTextColorValue()) {
            return false;
        }

        if (this.mItalic != null
                && style.mItalic != null
                && this.isItalicValue() != style.isItalicValue()) {
            return false;
        }
        if (this.mBold != null
                && style.mBold != null
                && this.isBoldValue() != style.isBoldValue()) {
            return false;
        }
        if (this.mUnderline != null
                && style.mUnderline != null
                && this.isUnderlineValue() != style.isUnderlineValue()) {
            return false;
        }

        if (this.mAlignment != null
                && style.mAlignment != null
                && this.getAlignmentValue() != style.getAlignmentValue()) {
            return false;
        }
        if (this.mWrapContent != null
                && style.mWrapContent != null
                && this.isWrapContentValue() != style.isWrapContentValue()) {
            return false;
        }

        if (this.mLetterMultiplier != null
                && style.mLetterMultiplier != null
                && this.getLetterMultiplierValue() != style.getLetterMultiplierValue()) {
            return false;
        }

        if (this.mLineMultiplier != null
                && style.mLineMultiplier != null
                && this.getLineMultiplierValue() != style.getLineMultiplierValue()) {
            return false;
        }

        return true;
    }
}
