package com.haiyunshan.express.compose.state;

import android.os.Handler;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;

import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.DocumentAdapter;
import com.haiyunshan.express.compose.DocumentState;
import com.haiyunshan.express.compose.action.SegmentAction;
import com.haiyunshan.express.compose.fragment.InsertionFragment;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.note.segment.StopSegment;

import java.nio.charset.Charset;

/**
 *
 */
public class InsertState extends DocumentState {

    static final String TAG = "ComposeState";

    boolean mAutoHideKeyboard;

    int mBoundTop;
    int mBoundBottom;

    public InsertState(NoteComposeFragment fragment) {
        super(fragment);
    }

    @Override
    public void onStart() {
        super.onStart();

        {
            int keyboardHeight = mParent.getExtraHeight();
            int height = WindowUtils.getDisplayHeight(mContext);
            this.mBoundTop = -keyboardHeight;   // 键盘高度
            this.mBoundBottom = height;         // 屏幕高度

            LogUtils.w(TAG, "bound = [" + mBoundTop + ", " + mBoundBottom + "]");

            int range = mRecyclerView.computeVerticalScrollRange();
            this.mAutoHideKeyboard = (range > (mBoundBottom - mBoundTop + height));

            LogUtils.w(TAG, "range = " + range + ", height = " + height + ", auto = " + mAutoHideKeyboard);
        }

        {
            ParagraphSegment focus = mParent.findFocus();
            mParent.setFocus(focus);
        }

        if (mParent.getFocus() == null) {

        } else {

            Handler handler = this.getHandler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    SegmentHolder holder = mRecyclerView.obtainHolder(mParent.getFocus());

                    ParagraphSegmentHolder target = (ParagraphSegmentHolder)holder;
                    if (target != null) {
                        target.setSelection();
                        target.requestFocus();
                    }

                    {
                        InsertionFragment f = new InsertionFragment();
                        f.setArguments(InsertState.this, null);
                        mParent.showFragment("insertion", f);
                    }
                }
            }, 100);
        }

        {
            mParent.mDividerDecor.setEnable(true);
            mParent.mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();

        this.save();
    }

    @Override
    public void onViewAttachedToWindow(SegmentHolder holder) {

    }

    @Override
    public void onViewDetachedFromWindow(SegmentHolder holder) {

        if (holder instanceof ParagraphSegmentHolder) {
            ParagraphSegmentHolder target = (ParagraphSegmentHolder)holder;
            target.getSegment().setText(target.mParagraphView.getText());
        }

    }

    @Override
    protected void onBindParagraph(ParagraphSegmentHolder holder) {
        super.onBindParagraph(holder);

        ParagraphView mParagraphView = holder.mParagraphView;

        mParagraphView.setEditable(true);
        mParagraphView.setShowSoftInputOnFocus(false);

        mParagraphView.setOnClickListener(null);
        mParagraphView.setOnLongClickListener(null);
        mParagraphView.setOnTouchListener(mParagraphAction);
        mParagraphView.setOnKeyListener(null);

        holder.updateHint();

        holder.setSelection();

    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        while (true) {
            if (Math.abs(velocityY) < FLING_TRIGGER) {
                break;
            }

            Segment focus = mParent.getFocus();
            if (focus != null) {
                mParent.setFocus(null);
                save();

                mParent.collapse();

                break;
            }

            {
                break;
            }

        }

        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    public final void save() {
        mParent.saveNote();
    }

    public void onCharacter(char c) {
        ParagraphSegmentHolder target = mParent.find(mContext.getCurrentFocus());
        if (target == null) {
            return;
        }

        ParagraphView mParagraphView = target.mParagraphView;
        mParagraphView.insert(String.valueOf(c));
    }

    public void onEvent(int event) {
        ParagraphSegmentHolder target = mParent.find(mContext.getCurrentFocus());
        if (target == null) {
            return;
        }

        switch (event) {
            case InsertionFragment.eventBackspace: {
                this.doBackspace(target);
                break;
            }
            case InsertionFragment.eventSplit: {
                this.doSplit(target);
                break;
            }
            case InsertionFragment.eventStop: {
                this.doStop(target);
                break;
            }
        }
    }

    void doBackspace(ParagraphSegmentHolder target) {
        ParagraphView mParagraphView = target.mParagraphView;
        int start = mParagraphView.getSelectionStart();
        int end = mParagraphView.getSelectionEnd();
        if (start == end && start == 0) {
            mParent.requestDelete(target);
        } else {
            mParagraphView.delete();
        }
    }

    void doSplit(ParagraphSegmentHolder target) {

        ParagraphSegmentHolder itemHolder = target;
        ParagraphSegment en = itemHolder.getSegment();
        if (mDocument.indexOf(en) >= 0) {
            ParagraphView edit = target.mParagraphView;

            Editable text = edit.getText();
            int start = edit.getSelectionStart();
            int end = edit.getSelectionEnd();

            if (text.length() != 0) {
                if (start == end) {
                    int pos = start;

                    CharSequence t1;
                    CharSequence t2;

                    t1 = text.subSequence(0, pos);
                    t2 = text.subSequence(pos, text.length());

                    this.splitParagraph(itemHolder, t1, t2);

                } else {
                    CharSequence t1 = text.subSequence(0, start);
                    CharSequence t2 = text.subSequence(start, end);
                    CharSequence t3 = text.subSequence(end, text.length());

                    this.splitParagraph(itemHolder, t1, t2, t3);
                }
            }

        } else {

        }
    }

    void doStop(ParagraphSegmentHolder target) {

        ParagraphSegmentHolder itemHolder = target;
        ParagraphSegment en = itemHolder.getSegment();
        if (mDocument.indexOf(en) >= 0) {
            ParagraphView edit = target.mParagraphView;

            CharSequence text = edit.getText();
            int start = edit.getSelectionStart();
            int end = edit.getSelectionEnd();

            if (text.length() != 0 && (start == end)) {
                int pos = start;
                if (pos == 0) { // 在开始插入
                    this.insertStop(en, -1);
                } else if ((pos + 1) == (text.length())) { // 在末尾插入
                    this.insertStop(en, 1);
                } else { // 在中间插入
                    this.doSplit(target);
                    this.insertStop(en, 1);
                }
            }

        } else {

        }
    }

    void splitParagraph(ParagraphSegmentHolder holder, CharSequence t1, CharSequence t2) {

        DocumentAdapter mAdapter = mParent.mAdapter;

        // 当前数据
        ParagraphSegment en = holder.getSegment();
        en.setText(t1);
        holder.mParagraphView.setText(t1);
        holder.mParagraphView.setSelection(t1.length());
        holder.mParagraphView.setMinLines(1);

        // 新的数据
        final ParagraphSegment entity = new ParagraphSegment(this.mDocument);
        entity.setText(t2);
        entity.setStyle(new StyleEntity(en.getStyle()));

        // 更新列表
        {
            int index = mAdapter.indexOf(en.getId());
            index = mAdapter.add(entity, index + 1);
//            mAdapter.notifyItemInserted(index);
            mAdapter.notifyDataSetChanged();
        }

        // 更新数据
        {
            int index = mDocument.indexOf(en);
            mDocument.add(entity, index + 1);

            mDocument.save();
        }

        mParent.mHandler.post(new Runnable() {
            @Override
            public void run() {

                SegmentHolder itemHolder = mRecyclerView.obtainHolder(entity);
                if (itemHolder != null) {
                    itemHolder.requestFocus();
                }
            }
        });

    }

    void splitParagraph(ParagraphSegmentHolder holder, CharSequence t1, CharSequence t2, CharSequence t3) {

        DocumentAdapter mAdapter = mParent.mAdapter;

        // 当前数据
        ParagraphSegment en = holder.getSegment();
        en.setText(t1);
        holder.mParagraphView.setText(t1);
        holder.requestFocus();

        // 新的数据
        ParagraphSegment entity = new ParagraphSegment(mDocument);
        entity.setText(t2);
        entity.setStyle(new StyleEntity(en.getStyle()));

        ParagraphSegment e3 = new ParagraphSegment(mDocument);
        e3.setText(t3);
        e3.setStyle(new StyleEntity(en.getStyle()));

        // 更新列表
        {
            int index = mAdapter.indexOf(en.getId());
            index = mAdapter.add(entity, index + 1);
            mAdapter.add(e3, index + 1);

            mAdapter.notifyItemRangeInserted(index, 2);
        }

        // 更新数据
        {
            int index = mDocument.indexOf(en);
            mDocument.add(entity, index + 1);
            mDocument.add(e3, index + 2);

            mDocument.save();
        }
    }

    StopSegment insertStop(ParagraphSegment segment, int position) {

        StopSegment seg = null;

        // 更新数据
        {
            int pos = mDocument.indexOf(segment);
            if (pos >= 0) {
                seg = new StopSegment(mDocument);
                if (position < 0) {
                    mDocument.add(seg, pos);
                } else {
                    mDocument.add(seg, pos + 1);
                }

                mDocument.save();
            }
        }

        // 更新界面
        if (seg != null) {
            int index = mAdapter.indexOf(segment.getId());
            if (index >= 0) {
                if (position < 0) {
                    mAdapter.add(seg, index);
                } else {
                    mAdapter.add(seg, index + 1);
                }
            }

            mAdapter.notifyItemRangeInserted(index, 1);
        }

        return seg;
    }

    /**
     *
     */
    SegmentAction mParagraphAction = new SegmentAction<ParagraphSegmentHolder>() {

        @Override
        protected boolean onTouch(ParagraphSegmentHolder holder, View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mParent.setFocus(holder.getSegment());

                mParent.expand();
            }

            return super.onTouch(holder, v, event);
        }
    };

}
