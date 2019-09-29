package com.haiyunshan.express.style.highlight;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ParagraphStyle;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.express.array.ArrayUtils;
import com.haiyunshan.express.array.GrowingArrayUtils;


/**
 * Created by sanshibro on 2018/3/8.
 */

public class HighlightPainter {

    Rect sTempRect = new Rect();

    SpanSet<HighlightSpan> mLineBackgroundSpans;

    TextView mView;

    Paint.FontMetricsInt mFontMetrics;

    private final HighlightSpan[] NO_PARA_SPANS =
            ArrayUtils.emptyArray(HighlightSpan.class);

    public HighlightPainter(TextView view) {

        this.mView = view;

        mLineBackgroundSpans = new SpanSet<>(HighlightSpan.class);

        this.mFontMetrics = new Paint.FontMetricsInt();
    }

    public void draw(Canvas canvas) {

        canvas.save();
        canvas.translate(mView.getPaddingLeft(), 0);

        this.drawBackground(canvas);

        canvas.restore();
    }

    public void drawBackground(Canvas canvas) {

        final long lineRange = getLineRangeForDraw(canvas);
        int firstLine = unpackRangeStartFromLong(lineRange);
        int lastLine = unpackRangeEndFromLong(lineRange);
        
        if (lastLine < 0) {
            return;
        }

        Spanned buffer = (Spanned) (mView.getText());
        int textLength = buffer.length();
        mLineBackgroundSpans.init(buffer, 0, textLength);

        if (mLineBackgroundSpans.numberOfSpans <= 0) {
            mLineBackgroundSpans.recycle();
            return;
        }

        TextPaint paint = mView.getPaint();
        paint.getFontMetricsInt(mFontMetrics);

        HighlightSpan[] spans = NO_PARA_SPANS;
        int spansLength = 0;

        int previousLineEnd = mView.getLayout().getLineStart(firstLine);
        int previousLineBottom = mView.getLayout().getLineTop(firstLine);

        int spanEnd = 0;
        final int width = mView.getWidth();
        for (int i = firstLine; i <= lastLine; i++) {

            int start = previousLineEnd;
            int end = mView.getLayout().getLineStart(i + 1);
            previousLineEnd = end;

            int ltop = previousLineBottom;
            int lbottom = mView.getLayout().getLineTop(i + 1);
            previousLineBottom = lbottom;
            
            int lbaseline = lbottom - mView.getLayout().getLineDescent(i);

//            if (start >= spanEnd)
            {

                // These should be infrequent, so we'll use this so that
                // we don't have to check as often.
//                spanEnd = mLineBackgroundSpans.getNextTransition(start, textLength);

                // All LineBackgroundSpans on a line contribute to its background.
                spansLength = 0;

                // Duplication of the logic of getParagraphSpans
                if (start != end || start == 0) {

                    // Equivalent to a getSpans(start, end), but filling the 'spans' local
                    // array instead to reduce memory allocation
                    for (int j = 0; j < mLineBackgroundSpans.numberOfSpans; j++) {
                        // equal test is valid since both intervals are not empty by
                        // construction
                        if (mLineBackgroundSpans.spanStarts[j] >= end ||
                                mLineBackgroundSpans.spanEnds[j] <= start) {
                            continue;
                        }

                        spans = GrowingArrayUtils.append(spans, spansLength, mLineBackgroundSpans.spans[j]);
                        spansLength++;
                    }
                }
            }

            for (int n = 0; n < spansLength; n++) {
                HighlightSpan span = (HighlightSpan) spans[n];

                span.draw(mView.getLayout(), canvas, paint, mFontMetrics,
                        0, width, ltop, lbaseline, lbottom,
                        buffer, start, end,
                        i);
            }
        }
        

        mLineBackgroundSpans.recycle();
    }

    public long getLineRangeForDraw(Canvas canvas) {

        TextView mEditText = this.mView;

        int dtop, dbottom;

        synchronized (sTempRect) {
            if (!canvas.getClipBounds(sTempRect)) {
                // Negative range end used as a special flag
                return packRangeInLong(0, -1);
            }

            dtop = sTempRect.top;
            dbottom = sTempRect.bottom;
        }

        final int top = Math.max(dtop, 0);
        final int bottom = Math.min(mView.getLayout().getLineTop(mView.getLineCount()), dbottom);

        if (top >= bottom) return packRangeInLong(0, -1);
        return packRangeInLong(mView.getLayout().getLineForVertical(top), mView.getLayout().getLineForVertical(bottom));
    }

    public long packRangeInLong(int start, int end) {
        return (((long) start) << 32) | end;
    }

    /**
     * Get the start value from a range packed in a long by {@link #packRangeInLong(int, int)}
     * @see #unpackRangeEndFromLong(long)
     * @see #packRangeInLong(int, int)
     * @hide
     */
    public int unpackRangeStartFromLong(long range) {
        return (int) (range >>> 32);
    }

    /**
     * Get the end value from a range packed in a long by {@link #packRangeInLong(int, int)}
     * @see #unpackRangeStartFromLong(long)
     * @see #packRangeInLong(int, int)
     * @hide
     */
    public int unpackRangeEndFromLong(long range) {
        return (int) (range & 0x00000000FFFFFFFFL);
    }

}
