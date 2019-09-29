package com.haiyunshan.express.style.highlight;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.ParagraphStyle;
import android.text.style.TabStopSpan;
import android.text.style.UpdateAppearance;

import com.haiyunshan.express.app.LogUtils;

import java.util.Arrays;

/**
 * Created by sanshibro on 2018/3/8.
 */

public class HighlightSpan implements ParagraphStyle {

    static final String TAG = "HighlightSpan";

    Drawable mDrawable;

    public HighlightSpan(Drawable drawable) {
        this.mDrawable = drawable;
    }

    public void draw(Layout layout, Canvas c, TextPaint p, Paint.FontMetricsInt metrics,
                     int left, int right, int top, int baseline, int bottom,
                     Spanned text, int start, int end, int lnum) {

        if (isEmptyLine(text, start, end)) {
            return;
        }

        left += (int)layout.getLineLeft(lnum); // 一行的开始位置

        if (text.charAt(start) == '\t') {
            if (layout.getLineContainsTab(lnum)) {
                TabStopSpan[] spans = text.getSpans(start, end, TabStopSpan.class);
                if (spans != null && spans.length > 0) {
                    left += spans[0].getTabStop();
                }
            }
        }

        int spanStart = text.getSpanStart(this);
        int spanEnd = text.getSpanEnd(this);

        int offset = 0;
        int width = 0;

        if (start < spanStart) { // 第一行

            offset = (int)(p.measureText(text, start, spanStart));

            if (end <= spanEnd) {
                width = (int)(p.measureText(text, start, end));
            } else {
                width = (int)p.measureText(text, start, spanEnd);
            }

        } else if (spanStart <= start && end <= spanEnd) { // 中间行

            width = (int)p.measureText(text, start, end);

        } else if (end > spanEnd) { // 最后一行

            width = (int)p.measureText(text, start, spanEnd);

        }

        int fontHeight = metrics.bottom - metrics.ascent;
        mDrawable.setBounds(left + offset, top, left + width, top + fontHeight);
        mDrawable.draw(c);
    }

    boolean isEmptyLine(Spanned text, int start, int end) {
        boolean result = true;

        for (int i = start; i < end; i++) {
            char c = text.charAt(i);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                continue;
            }

            result = false;
            break;
        }

        return result;
    }
}
