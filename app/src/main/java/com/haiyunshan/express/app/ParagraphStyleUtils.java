package com.haiyunshan.express.app;

import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.typeface.TypefaceManager;

/**
 * Created by sanshibro on 25/11/2017.
 */

public class ParagraphStyleUtils {

    public static final void apply(TextView view, StyleEntity style) {
        apply(view, null, style);
    }

    public static final void apply(TextView view, StyleEntity style, StyleEntity defaultStyle) {

        String typeface = getTypeface(style, defaultStyle);

        boolean italic = getItalic(style, defaultStyle);
        boolean bold = getBold(style, defaultStyle);
        boolean underline = getUnderline(style, defaultStyle);

        int textSize = getTextSize(style, defaultStyle);

        int textColor = getTextColor(style, defaultStyle);

        int align = getAlignment(style, defaultStyle);
        boolean wrapContent = getWrapContent(style, defaultStyle);

        int letterSpacing = getLetterSpacing(style, defaultStyle);
        int lineSpacing = getLineSpacing(style, defaultStyle);

        {
            Typeface tf = TypefaceManager.instance().getTypeface(typeface, bold, italic);
            view.setTypeface(tf);

            view.getPaint().setUnderlineText(underline);

            view.setTextSize(textSize);

            view.setTextColor(textColor);
            view.setHintTextColor(0xff8f8e94);

            view.setTextAlignment(align);

            view.setLetterSpacing(letterSpacing * 1.f / 100);
            view.setLineSpacing(0, lineSpacing * 1.f / 100);
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        boolean wrap = (params.width == ViewGroup.LayoutParams.WRAP_CONTENT);
        if (wrap ^ wrapContent) {
            params.width = (wrapContent)? ViewGroup.LayoutParams.WRAP_CONTENT: ViewGroup.LayoutParams.MATCH_PARENT;
            view.requestLayout();
        } else {
            view.postInvalidateOnAnimation();
        }

    }

    public static final String getTypeface(StyleEntity itemStyle, StyleEntity defaultStyle) {
        String value = defaultStyle.getTypeface();

        if (itemStyle != null) {
            String value1 = itemStyle.getTypeface();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final int getTextSize(StyleEntity itemStyle, StyleEntity defaultStyle) {
        int value = defaultStyle.getTextSize();

        if (itemStyle != null) {
            Integer value1 = itemStyle.getTextSize();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final boolean getBold(StyleEntity itemStyle, StyleEntity defaultStyle) {
        boolean value = defaultStyle.isBold();

        if (itemStyle != null) {
            Boolean value1 = itemStyle.isBold();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final boolean getItalic(StyleEntity itemStyle, StyleEntity defaultStyle) {
        boolean value = defaultStyle.isItalic();

        if (itemStyle != null) {
            Boolean value1 = itemStyle.isItalic();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final boolean getUnderline(StyleEntity itemStyle, StyleEntity defaultStyle) {
        boolean value = defaultStyle.isUnderline();

        if (itemStyle != null) {
            Boolean value1 = itemStyle.isUnderline();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final int getTextColor(StyleEntity itemStyle, StyleEntity defaultStyle) {
        int value = defaultStyle.getTextColor();

        if (itemStyle != null) {
            Integer value1 = itemStyle.getTextColor();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final int getAlignment(StyleEntity itemStyle, StyleEntity defaultStyle) {
        int value = defaultStyle.getAlignment();

        if (itemStyle != null) {
            Integer value1 = itemStyle.getAlignment();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final boolean getWrapContent(StyleEntity itemStyle, StyleEntity defaultStyle) {
        boolean value = defaultStyle.isWrapContent();

        if (itemStyle != null) {
            Boolean value1 = itemStyle.isWrapContent();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final int getLetterSpacing(StyleEntity itemStyle, StyleEntity defaultStyle) {
        int value = defaultStyle.getLetterMultiplier();

        if (itemStyle != null) {
            Integer value1 = itemStyle.getLetterMultiplier();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }

    public static final int getLineSpacing(StyleEntity itemStyle, StyleEntity defaultStyle) {
        int value = defaultStyle.getLineMultiplier();

        if (itemStyle != null) {
            Integer value1 = itemStyle.getLineMultiplier();
            value = (value1 != null) ? value1 : value;
        }

        return value;
    }
}
