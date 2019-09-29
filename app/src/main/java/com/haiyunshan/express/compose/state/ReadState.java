package com.haiyunshan.express.compose.state;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.SoftKeyboardUtils;
import com.haiyunshan.express.compose.DocumentState;
import com.haiyunshan.express.compose.action.SegmentAction;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.style.highlight.HighlightSpan;

/**
 *
 */
public class ReadState extends DocumentState {

    public ReadState(NoteComposeFragment fragment) {
        super(fragment);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 取消焦点Segment
        {
            mParent.setFocus(null);
        }

        {
            mParent.mDoneBtn.setVisibility(View.GONE);
            mParent.mBackBtn.setVisibility(View.VISIBLE);

            mParent.mPhotoBtn.setVisibility(View.GONE);

            mParent.mCreateBtn.setVisibility(View.GONE);
            mParent.mComposeBtn.setVisibility(View.VISIBLE);
        }

        {
            mParent.hideFragment();
            SoftKeyboardUtils.hide(mContext);
        }

        {
            mParent.mDividerDecor.setEnable(false);

            mParent.mAdapter.notifyDataSetChanged();
        }

        mParent.setBottomBar(true);

    }

    @Override
    protected void onEnd() {
        super.onEnd();

        mParent.saveNote();

        mParent.setFocus(null);

        // 记录DecorBottom
        SoftKeyboardUtils.markBottom(mContext);

        mParent.setBottomBar(false);
    }

    @Override
    protected void onBindParagraph(ParagraphSegmentHolder holder) {
        super.onBindParagraph(holder);

        ParagraphView mParagraphView = holder.mParagraphView;

        mParagraphView.setEditable(false);

        mParagraphView.setOnClickListener(null);
        mParagraphView.setOnLongClickListener(null);
        mParagraphView.setOnTouchListener(mParagraphAction);
        mParagraphView.setOnKeyListener(null);

        mParagraphView.setSelectionActionModeCallback(mActionModeCallback);

        mParagraphView.setHint(null);
    }

    @Override
    public void onViewDetachedFromWindow(SegmentHolder holder) {
        if (holder instanceof ParagraphSegmentHolder) {
            ParagraphSegmentHolder target = (ParagraphSegmentHolder)holder;
            target.getSegment().setText(target.mParagraphView.getText());
        }
    }

    public final void highlight() {
        Segment segment = mParent.getFocus();
        if (segment == null) {
            return;
        }

        ParagraphSegmentHolder holder = (ParagraphSegmentHolder)mRecyclerView.obtainHolder(segment);
        if (holder == null) {
            return;
        }

        ParagraphView view = holder.mParagraphView;
        int start = view.getSelectionStart();
        int end = view.getSelectionEnd();
        if (start == end) {
            return;
        }

        Spannable text = view.getText();

        int min = Math.max(0, Math.min(start, end));
        int max = Math.max(0, Math.max(start, end));

        HighlightSpan[] spans = text.getSpans(min, max, HighlightSpan.class);
        if (spans != null && spans.length > 0) {
            return;
        }

        HighlightSpan span = new HighlightSpan(mContext.getDrawable(R.drawable.bdreader_note_bg_brown_1));
        text.setSpan(span, min, max, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    public final void style(int style) {
        Segment segment = mParent.getFocus();
        if (segment == null) {
            return;
        }

        ParagraphSegmentHolder holder = (ParagraphSegmentHolder)mRecyclerView.obtainHolder(segment);
        if (holder == null) {
            return;
        }

        ParagraphView view = holder.mParagraphView;
        int start = view.getSelectionStart();
        int end = view.getSelectionEnd();
        if (start == end) {
            return;
        }

        Spannable text = view.getText();

        int min = Math.max(0, Math.min(start, end));
        int max = Math.max(0, Math.max(start, end));

        StyleSpan[] spans = text.getSpans(min, max, StyleSpan.class);
        if (spans != null && spans.length > 0) {
            for (StyleSpan span : spans) {
                if ((span.getStyle() & style) != 0) {
                    return;
                }
            }
        }

        StyleSpan span = new StyleSpan(style);
        text.setSpan(span, min, max, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    /**
     *
     */
    SegmentAction mParagraphAction = new SegmentAction<ParagraphSegmentHolder>() {

        @Override
        protected boolean onTouch(ParagraphSegmentHolder holder, View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mParent.setFocus(holder.getSegment());
            }

            return super.onTouch(holder, v, event);
        }
    };

    ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.clear();

            if (false) {
                SpannableString text = new SpannableString("Highlight");
                text.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                text.setSpan(new ImageSpan(mContext, R.drawable.ic_backspace), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                menu.add(Menu.NONE, R.id.highlight, 0, text);
            }


            {
                SpannableString text = new SpannableString("Highlight");
//                text.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//                text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                menu.add(Menu.NONE, R.id.highlight, 0, text);
            }

            {
                SpannableString text = new SpannableString("Bold");
                text.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                menu.add(Menu.NONE, R.id.bold, 0, text);
            }
            {
                SpannableString text = new SpannableString("Italic");
                text.setSpan(new StyleSpan(Typeface.ITALIC), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                menu.add(Menu.NONE, R.id.italic, 0, text);
            }

            menu.add(Menu.NONE, android.R.id.copy, Menu.NONE, android.R.string.copy);
            menu.add(Menu.NONE, android.R.id.selectAll, Menu.NONE, android.R.string.selectAll);


            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.paste
                    || id == android.R.id.selectAll
                    || id == android.R.id.copy
                    || id == android.R.id.cut) {
                return false;
            }

            if (id == R.id.highlight) {
                highlight();
            } else if (id == R.id.bold) {
                style(Typeface.BOLD);
            } else if (id == R.id.italic) {
                style(Typeface.ITALIC);
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
}
