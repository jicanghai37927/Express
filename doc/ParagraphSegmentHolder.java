package com.haiyunshan.express.note.holder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.haiyunshan.express.App;
import com.haiyunshan.express.fragment.note.NoteComposeFragment;
import com.haiyunshan.express.R;
import com.haiyunshan.express.app.SoftKeyboardUtils;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.dataset.note.style.Style;
import com.haiyunshan.express.dataset.note.entity.Entity;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.widget.OnDoubleClickListener;
import com.haiyunshan.express.widget.ParagraphView;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 *
 */
public class ParagraphSegmentHolder extends SegmentHolder<ParagraphSegment>
        implements View.OnClickListener, View.OnTouchListener, View.OnKeyListener, View.OnLongClickListener, OnDoubleClickListener {

    private static final int DELAY_UNLOCK_CONTENT_HEIGHT = 200;

    ParagraphView mParagraphView;

    static ParagraphSegmentHolder create(NoteComposeFragment fragment, ViewGroup parent) {
        LayoutInflater inflater = fragment.getActivity().getLayoutInflater();

        int resource = R.layout.layout_note_paragraph_item;
        View view = inflater.inflate(resource, parent, false);
        ParagraphSegmentHolder holder = new ParagraphSegmentHolder(fragment, view);

        return holder;
    }

    public ParagraphSegmentHolder(NoteComposeFragment fragment, View itemView) {
        super(fragment, itemView);

        this.mParagraphView = (ParagraphView)itemView.findViewById(R.id.tv_text);
    }

    @Override
    public void bind(int position, ParagraphSegment entity) {
        super.bind(position, entity);

        this.applyStyle();
        this.applyPage();

        mParagraphView.setText(entity.getText());

        int index = getNote().indexOf(entity);
        if (index >= 0) {

            {
                String hint = null;
                if (mContext.getMode() == NoteComposeFragment.MODE_EDIT) {
                    if (index == 0 && getNote().size() == 1) {
                        hint = "正文";
                    }
                }

                mParagraphView.setHint(hint);
            }

            {
                int minLines = 1;
                if (getNote().size() == index + 1) {
                    minLines = this.getMinLines(mContext.getActivity(), getNote());
                }
                int lines = mParagraphView.getMinLines();
                if (lines != minLines) {
                    mParagraphView.setMinLines(minLines);
                }
            }
        }

        int mode = mContext.getMode();
        this.applyMode(mode);

        if (mode == NoteComposeFragment.MODE_FORMAT && !mContext.isCollapse()) {
            Segment formatEn = mContext.getFormatEntity();
            if (entity == formatEn) {
                mParagraphView.requestFocus();
                mParagraphView.selectAll();
            } else {
                mParagraphView.setSelection(0);
            }
        }
    }

    @Override
    public void applyChanged() {
        if (mSegment != null) {
            mSegment.setText(mParagraphView.getText());
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();

        if (mSegment != null) {
            mSegment.setText(mParagraphView.getText());

            if (mContext.getMode() == NoteComposeFragment.MODE_FORMAT) {
                if (mParagraphView.hasSelection()) {
                    if (mSegment == mContext.getFormatEntity()) {
                        mContext.collapseTextFormat();
                    }
                }
            }
        }
    }

    public void onModeChanged(int oldMode, int newMode) {
        int mode = newMode;

        this.applyMode(mode);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP && isPanelVisible()) {
            this.lockContentHeight();
            this.hidePanel(true);

            // 键盘显示后，恢复原来设置
            v.removeCallbacks(mUnlockContentHeight);
            v.postDelayed(mUnlockContentHeight, DELAY_UNLOCK_CONTENT_HEIGHT);
        }

        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (mParagraphView.getSelectionStart() == 0) {
                    this.requestDelete();
                }
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == mParagraphView) {
            int mode = mContext.getMode();
            if (mode == NoteComposeFragment.MODE_VIEW) {

            } else if (mode == NoteComposeFragment.MODE_FORMAT) {
                if (mParagraphView.length() != 0) {
                    mParagraphView.requestFocus();

                    if (!mParagraphView.hasSelection()) {
                        mParagraphView.selectAll();
                    }

                    mContext.setFormatEntity(mSegment);
                }
            } else if (mode == NoteComposeFragment.MODE_EDIT) {

            } else if (mode == NoteComposeFragment.MODE_PAGE) {

            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == mParagraphView) {
            Log.w("ParagraphSegmentHolder", "onLongClick");
        }

        return false;
    }

    @Override
    public void onDoubleClick(View v) {
        if (v == mParagraphView) {
            Log.w("ParagraphSegmentHolder", "onDoubleClick");
        }
    }

    public void setMinLines(int lines) {
        if (mParagraphView.getMinLines() != lines) {
            mParagraphView.setMinLines(lines);
        }
    }

    public void requestTab() {
        mParagraphView.insert("\t");
    }

    public void setHint(CharSequence str) {
        mParagraphView.setHint(str);
    }

    public void setText(CharSequence text) {
        if (mSegment != null) {
            mSegment.setText(text);
        }

        mParagraphView.setText(text);
    }

    public void setSelection(int index) {
        if (index > mParagraphView.length()) {
            mParagraphView.setSelection(mParagraphView.length());
        } else {
            mParagraphView.setSelection(index);
        }
    }

    @Override
    public void clearFocus() {
        mParagraphView.clearFocus();
    }

    @Override
    public void requestFocus() {
        mParagraphView.requestFocus();
        mParagraphView.requestFocusFromTouch();

    }

    @Override
    public void beginFormat() {
        super.beginFormat();

        mParagraphView.requestFocus();
        mParagraphView.selectAll();
    }

    void requestDelete() {

        // 判断是否是第一个段落
        int index = getNote().indexOf(mSegment);
        if (index <= 0) {
            if (mParagraphView.length() != 0 && mParagraphView.getText().charAt(0) =='\n') {
                mParagraphView.delete(0, 1);
            }
            return;
        }

        // 判断是否是段落
        Segment en = getNote().get(index - 1);
        if (!en.getType().equalsIgnoreCase(Entity.TYPE_PARAGRAPH)) {
            if (mParagraphView.length() != 0 && mParagraphView.getText().charAt(0) =='\n') {
                mParagraphView.delete(0, 1);
            }
            return;
        }

        mParagraphView.setOnKeyListener(null);
        ParagraphSegment entity = this.mSegment;
        mSegment = null;

        // 合并
        entity.setText(mParagraphView.getText());
        mContext.mergeParagraph(entity, (ParagraphSegment) en);
    }

    public static int getMinLines(Activity context, Document note) {
        Style ss = note.getStyle();
        StyleEntity s = null;
        if (s == null) {
            if (!ss.isEmpty()) {
                s = ss.getList().get(0);
            }
        }

        int lines;

        int displayHeight = WindowUtils.getDisplayHeight(context);
        if (s == null) {
            int height = (int)TypedValue.applyDimension(COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
            lines = displayHeight / height;
        } else {
            Integer textSize = s.getTextSize();
            textSize = (textSize == null || textSize == 0)? 18: textSize;
            int height = (int)TypedValue.applyDimension(COMPLEX_UNIT_DIP, textSize, context.getResources().getDisplayMetrics());
            lines = displayHeight / height;
        }

        lines = lines / 3;
        lines = (lines < 1)? 1: lines;

        return lines;
    }

    boolean isPanelVisible() {
        return (mContext.getFormatContainer().getVisibility() == View.VISIBLE);
    }

    void hidePanel(boolean showKeyboard) {

        mContext.hidePanel();

        if (showKeyboard) {

            lockContentHeight();

            setPanel(false);

            mParagraphView.setKeyboard(true);

            mParagraphView.removeCallbacks(mUnlockContentHeight);
            mParagraphView.postDelayed(mUnlockContentHeight, DELAY_UNLOCK_CONTENT_HEIGHT);
        } else {
            this.setPanel(false);
        }

    }
    void setPanel(boolean visible) {

        View view = mContext.getFormatContainer();

        boolean old = (view.getVisibility() == View.VISIBLE);
        if (!(old ^ visible)) {
            return;
        }

        if (visible) {
            int height = getSoftKeyboardHeight(mContext.getActivity());
            view.getLayoutParams().height = height;
            view.setVisibility(View.VISIBLE);

        } else {
            int height = 0;
            view.getLayoutParams().height = height;
            view.setVisibility(View.GONE);
        }
    }

    void lockContentHeight() {
        View view = mContext.getEntityContainer();

        int height = view.getHeight();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
        params.height = height;
        params.weight = 0;
    }

    void unlockContentHeight() {
        View view = mContext.getEntityContainer();

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = 1;

        // 判断软键盘是否显示，同步一次软键盘高度
        if (SoftKeyboardUtils.isVisible(mContext.getActivity())) {
            int value = PrefUtils.getKeyboardHeight();
            int height = getSoftKeyboardHeight(mContext.getActivity());
            if (height > 0 && height != value) {
                view.requestLayout();
            }
        }
    }

    public static int getSoftKeyboardHeight(Activity context) {

        int height = SoftKeyboardUtils.getHeight(context);
        if (height <= 0) {
            height = PrefUtils.getKeyboardHeight();
            if (height <= 0) {
                height = context.getResources().getDimensionPixelSize(R.dimen.soft_keyboard_height);
            }
        } else {
            PrefUtils.setKeyboardHeight(height);
        }

        return height;
    }

    public static int getSoftKeyboardHeight() {
        return PrefUtils.getKeyboardHeight();
    }

    public void applyStyle() {

        StyleEntity itemStyle = mSegment.getStyle();
        StyleEntity noteStyle = mSegment.getDefaultStyle();

        mParagraphView.applyStyle(itemStyle, noteStyle);
    }

    void applyPage() {
        int left = getPagePaddingLeft();
        int top = mParagraphView.getPaddingTop();
        int right = getPagePaddingRight();
        int bottom = mParagraphView.getPaddingBottom();

        mParagraphView.setPadding(left, top, right, bottom);
    }

    void applyMode(int mode) {
        if (mode == NoteComposeFragment.MODE_VIEW) {
            mParagraphView.setEditable(false);

            mParagraphView.setOnClickListener(this);
            mParagraphView.setOnLongClickListener(this);


            mParagraphView.setOnTouchListener(null);
            mParagraphView.setOnKeyListener(null);

        } else if (mode == NoteComposeFragment.MODE_FORMAT) {
            mParagraphView.setEditable(false);

            mParagraphView.setOnClickListener(this);
            mParagraphView.setOnLongClickListener(null);


            mParagraphView.setOnTouchListener(null);
            mParagraphView.setOnKeyListener(null);

        } else if (mode == NoteComposeFragment.MODE_EDIT) {
            mParagraphView.setEditable(true);

            mParagraphView.setOnClickListener(null);
            mParagraphView.setOnLongClickListener(null);


            mParagraphView.setOnTouchListener(this);
            mParagraphView.setOnKeyListener(this);

        } else if (mode == NoteComposeFragment.MODE_PAGE) {
            mParagraphView.setEditable(false);

            mParagraphView.setOnClickListener(null);
            mParagraphView.setOnLongClickListener(null);


            mParagraphView.setOnTouchListener(null);
            mParagraphView.setOnKeyListener(null);

        }
    }

    /**
     *
     */
    Runnable mUnlockContentHeight = new Runnable() {

        @Override
        public void run() {
            unlockContentHeight();
        }
    };

    /**
     *
     */
    private static class PrefUtils {

        static final String prefName = "softkeyboard_pref";

        static final String keyKeyboardHeight = "keyboard_height";

        static void setKeyboardHeight(int value) {
            SharedPreferences prefs = getSharedPreferences();
            prefs.edit().putInt(keyKeyboardHeight, value).commit();
        }

        static int getKeyboardHeight() {
            SharedPreferences prefs = getSharedPreferences();
            int value = prefs.getInt(keyKeyboardHeight, -1);

            return value;
        }

        static SharedPreferences getSharedPreferences() {
            Context context = App.instance();

            SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE); // 多进程访问
            return prefs;
        }

    }

}
