package com.haiyunshan.express.note.segment;

import android.app.Activity;
import android.graphics.Color;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TabStopSpan;

import com.haiyunshan.express.dataset.note.entity.ParagraphEntity;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.style.highlight.HighlightSpan;

import org.json.JSONObject;

/**
 *
 */
public class ParagraphSegment extends Segment {

    Spanned mText;
    int mStart;
    int mEnd;

    StyleEntity mStyle;

    public ParagraphSegment(Document doc) {
        super(doc);

        this.mType = Segment.TYPE_PARAGRAPH;

        SpannableString text = new SpannableString("");
        text.setSpan(new TabStopSpan.Standard(100), 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        this.mText = text;
        this.mStart = -1;
        this.mEnd = -1;

        this.mStyle = null;
    }

    public ParagraphSegment(Document doc, ParagraphEntity entity) {
        super(doc, entity);

        SpannableString text = new SpannableString(entity.getText());
        text.setSpan(new TabStopSpan.Standard(100), 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        entity.attachStyle(text);

        this.mText = text;

        this.mStart = -1;
        this.mEnd = -1;

        this.mStyle = entity.getStyle();
    }

    @Override
    public ParagraphEntity toEntity() {
        ParagraphEntity entity = new ParagraphEntity(this.mId, this.getType(this));
        entity.setId(this.mId);

        entity.setCreated(this.mCreated);
        entity.setModified(this.mModified);

        entity.setText(mText.toString());
        entity.setStyle(this.mStyle);

        this.handleStyle(entity, this.mText);

        return entity;
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(this.mText);
    }

    public Spanned getText() {
        return this.mText;
    }

    public void setText(CharSequence text) {
        if (text instanceof Spanned) {
            this.mText = (Spanned)text;
        } else {
            this.mText = new SpannableString(text);
        }

        this.mStart = Selection.getSelectionStart(text);
        this.mEnd = Selection.getSelectionEnd(text);
    }

    public boolean hasSelection() {
        return (mStart >= 0) && (mEnd >= 0);
    }

    public int getSelectionStart() {
        return mStart;
    }

    public int getSelectionEnd() {
        return mEnd;
    }

    public void setSelectionStart(int pos) {
        this.mStart = pos;
    }

    public void setSelectionEnd(int pos) {
        this.mEnd = pos;
    }

    public void setSelection(int start, int stop) {
        this.mStart = start;
        this.mEnd = stop;
    }

    public boolean isStyleChanged() {
        if (!mStyle.isSet()) {
            return false;
        }

        boolean value = (mStyle.equals(this.getDefaultStyle()));
        return !value;
    }

    public StyleEntity getStyle() {
        return this.mStyle;
    }

    public StyleEntity getDefaultStyle() {
        String id = mStyle.getId();
        StyleEntity entity = mDocument.getStyle().obtain(id);
        return entity;
    }

    public void setStyle(StyleEntity style) {
        this.mStyle = style;
    }

    void handleStyle(ParagraphEntity entity, Spanned text) {
        int start = 0;
        int end = text.length();

        {
            HighlightSpan[] spans = text.getSpans(start, end, HighlightSpan.class);
            if (spans != null && spans.length > 0) {
                for (HighlightSpan span : spans) {
                    JSONObject obj = ParagraphEntity.toJSON(span, text.getSpanStart(span), text.getSpanEnd(span));
                    entity.addSpan(obj);
                }
            }
        }

        {
            StyleSpan[] spans = text.getSpans(start, end, StyleSpan.class);
            if (spans != null && spans.length > 0) {
                for (StyleSpan span : spans) {
                    JSONObject obj = ParagraphEntity.toJSON(span, text.getSpanStart(span), text.getSpanEnd(span));
                    entity.addSpan(obj);
                }
            }
        }
    }

}
