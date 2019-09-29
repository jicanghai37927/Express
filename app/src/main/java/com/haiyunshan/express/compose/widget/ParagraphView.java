package com.haiyunshan.express.compose.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.ParagraphStyleUtils;
import com.haiyunshan.express.app.SoftKeyboardUtils;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.widget.HighlightEditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 *
 */
public class ParagraphView extends FrameLayout implements View.OnKeyListener, View.OnTouchListener {

    static final String TAG = "ParagraphView";

    HighlightEditText mEditText;

    OnKeyListener mOnKeyListener = null;
    OnTouchListener mOnTouchListener = null;

    Rect mSelectionBounds;

    public ParagraphView(@NonNull Context context) {
        this(context, null);
    }

    public ParagraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParagraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ParagraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.mEditText = this.createEditText(context);
        this.addView(mEditText);
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        super.setOnKeyListener(l);

        this.mOnKeyListener = l;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);

        this.mOnTouchListener = l;
    }

    public void setSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        mEditText.setCustomSelectionActionModeCallback(actionModeCallback);
    }

    public ActionMode.Callback getSelectionActionModeCallback() {
        return mEditText.getCustomSelectionActionModeCallback();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mEditText.setPadding(left, top, right, bottom);
    }

    @Override
    public int getPaddingLeft() {
        return mEditText.getPaddingLeft();
    }

    @Override
    public int getPaddingTop() {
        return mEditText.getPaddingTop();
    }

    @Override
    public int getPaddingRight() {
        return mEditText.getPaddingRight();
    }

    @Override
    public int getPaddingBottom() {
        return mEditText.getPaddingBottom();
    }

    public final void setText(CharSequence text) {
        mEditText.setText(text);
    }

    public final void setHint(CharSequence hint) {
        mEditText.setHint(hint);
    }

    public int getMinLines() {
        return mEditText.getMinLines();
    }

    public void setMinLines(int minlines) {
        mEditText.setMinLines(minlines);
    }

    public void setMaxLines(int maxlines) {
        mEditText.setMaxLines(maxlines);
    }

    public void setSingleLine(boolean singleLine) {
        mEditText.setSingleLine(singleLine);
    }

    public int getY(int selection) {
        int y = 0;

        int lineHeight = this.getLineHeight();
        int count = this.getLineCount();
        for (int i = 0; i < count; i++) {
            int end = this.getLineEnd(i);
            if (selection < end) {
                break;
            }

            y += lineHeight;
        }

        return y;
    }

    int getLine(int selection) {
        int line = 0;

        int count = this.getLineCount();
        for (int i = 0; i < count; i++) {
            int end = this.getLineEnd(i);
            if (selection <= end) {
                line = i;
                break;
            }
        }

        return line;
    }

    public Rect getSelectionBounds() {
        if (mSelectionBounds == null) {
            mSelectionBounds = new Rect();
        }

        int top;
        int bottom;

        Rect rect = mSelectionBounds;

        int start = mEditText.getSelectionStart();
        int end = mEditText.getSelectionEnd();

        {
            int line = this.getLine(start);
            mEditText.getLineBounds(line, rect);
            top = rect.top;
        }

        {
            int line = this.getLine(end);
            mEditText.getLineBounds(line, rect);
            bottom = rect.bottom;
        }

        rect.top = top;
        rect.bottom = bottom;

        return rect;
    }

    public void delete() {

        int start = mEditText.getSelectionStart();
        int end = mEditText.getSelectionEnd();
        if (start <= 0 || start > mEditText.length()) {
            return;
        }
        if (end <= 0 || end > mEditText.length()) {
            return;
        }

        Editable e = mEditText.getEditableText();
        if (start == end) {
            e.delete(start - 1, start);
        } else {
            e.delete(start, end);
        }
    }

    @Override
    public void clearFocus() {
        super.clearFocus();

        this.clearChildFocus(mEditText);
    }

    public EditText getView() {
        return this.mEditText;
    }

    public void insert(CharSequence text) {

        int start = mEditText.getSelectionStart();
        int end = mEditText.getSelectionEnd();
        if (start < 0 || start > mEditText.length()) {
            return;
        }
        if (end < 0 || end > mEditText.length()) {
            return;
        }

        Editable e = mEditText.getText();
        if (start == end) {
            e.insert(start, text);
        } else {
            e.replace(start, end, text);
        }
    }

    public void selectAll() {
        mEditText.selectAll();
    }

    public void selectNone() {
        int start = mEditText.getSelectionStart();
        if (start >= 0) {
            mEditText.setSelection(start);
        }
    }

    public void setSelection(int index) {
        mEditText.setSelection(index);
    }

    public void setSelection(int start, int stop) {
        mEditText.setSelection(start, stop);
    }

    public Editable getText() {
        return mEditText.getText();
    }

    public boolean hasSelection() {
        return mEditText.hasSelection();
    }

    public void delete(int st, int en) {
        mEditText.getEditableText().delete(st, en);
    }

    public int length() {
        return mEditText.length();
    }

    public int getSelectionStart() {
        return mEditText.getSelectionStart();
    }

    public int getSelectionEnd() {
        return mEditText.getSelectionEnd();
    }

    public void setKeyboard(boolean visible) {
        if (visible) {
            SoftKeyboardUtils.show(getContext(), mEditText);
        } else {
            SoftKeyboardUtils.hide(getContext(), mEditText);
        }
    }

    public void showSoftInput() {
        SoftKeyboardUtils.show(getContext(), mEditText);
    }

    public void hideSoftInput() {
        SoftKeyboardUtils.hide(getContext(), mEditText);
    }

    public void setEditable(boolean enable) {

        mEditText.setCursorVisible(enable);
        mEditText.setShowSoftInputOnFocus(enable);

        {
            boolean result = this.prepareCursorControllers();
            if (!result) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mEditText.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
                }
            }
        }
    }

    public int getStart(int y) {
        int lineHeight = mEditText.getLineHeight();
        int line = y / lineHeight;
        if (y % lineHeight != 0) {
            ++line;
        }

        return this.getLineStart(line);
    }

    public int getEnd(int y) {
        int lineHeight = mEditText.getLineHeight();
        int line = y / lineHeight;
        if (y % lineHeight != 0) {
            ++line;
        }

        int count = this.getLineCount();
        line = (line >= count)? (count - 1): line;
        line = (line < 0)? 0: line;

        return this.getLineEnd(line);
    }

    public int getLineCount() {
        return mEditText.getLineCount();
    }

    public int getLineHeight() {
        return mEditText.getLineHeight();
    }

    public int getLineStart(int line) {
        return mEditText.getLayout().getLineStart(line);
    }

    public int getLineEnd(int line) {
        return mEditText.getLayout().getLineEnd(line);
    }

    public final boolean getShowSoftInputOnFocus() {
        return mEditText.getShowSoftInputOnFocus();
    }

    public boolean textCanBeSelected() {
        try {
            Method textCanBeSelected = TextView.class.getDeclaredMethod("textCanBeSelected");
            textCanBeSelected.setAccessible(true);
            Object obj = textCanBeSelected.invoke(mEditText);

            return (Boolean)obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public final void setShowSoftInputOnFocus(boolean show) {
        mEditText.setShowSoftInputOnFocus(show);
    }

    public void setCursorVisible(boolean visible) {
        mEditText.setCursorVisible(visible);
    }

    public boolean isCursorVisible() {
        return mEditText.isCursorVisible();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int left = mEditText.getLeft();
        int top = mEditText.getTop();
        int right = mEditText.getRight();
        int bottom = mEditText.getBottom();

        float x = ev.getX();
        float y = ev.getY();
        if (x >= left && x < right) {
            return super.dispatchTouchEvent(ev);
        }

        MotionEvent event = MotionEvent.obtain(ev);
        if (x < left) {
            event.offsetLocation(left - x, 0);
        }
        if (x > right) {
            event.offsetLocation(right - x - 1, 0);
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void applyStyle(StyleEntity style, StyleEntity defaultStyle) {
        ParagraphStyleUtils.apply(this.mEditText, style, defaultStyle);
    }

    HighlightEditText createEditText(Context context) {
        HighlightEditText edit = new HighlightEditText(context);
        edit.setId(R.id.edit_paragraph);

        edit.setMinWidth(320);

        edit.setTextColor(Color.BLACK);
        edit.setBackgroundColor(Color.TRANSPARENT);
        edit.setGravity(Gravity.TOP | Gravity.LEFT);
        edit.setPadding(0, 0, 0, 0);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        edit.setLayoutParams(params);

        edit.setOnKeyListener(this);
        edit.setOnTouchListener(this);

        return edit;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (mOnKeyListener != null) {
            return mOnKeyListener.onKey(this, keyCode, event);
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mOnTouchListener != null) {
            return mOnTouchListener.onTouch(this, event);
        }

        return false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    boolean prepareCursorControllers() {
        boolean result = nullLayouts(mEditText);

        return result;
    }

    private boolean nullLayouts(TextView view) {

        try {
            Method textCanBeSelected = TextView.class.getDeclaredMethod("nullLayouts");
            textCanBeSelected.setAccessible(true);
            textCanBeSelected.invoke(view);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
