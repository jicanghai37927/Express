package com.haiyunshan.express.compose.state;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haiyunshan.express.compose.DocumentState;
import com.haiyunshan.express.compose.action.SegmentAction;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.fragment.TextFormatFragment;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;

/**
 *
 */
public class FormatState extends DocumentState {

    static final String TAG = "FormatState";

    Segment mTarget;

    TextFormatFragment mFormatFragment;

    KeepFocusRunnable mKeepFocusRunnable;

    public FormatState(NoteComposeFragment fragment) {
        super(fragment);

        this.mFormatFragment = null;
        this.mKeepFocusRunnable = new KeepFocusRunnable();
    }

    @Override
    public void onStart() {
        super.onStart();

        {
            mParent.mPhotoBtn.setVisibility(View.GONE);
        }

        {
            this.mTarget = mParent.getFocus();
            if (mTarget == null) {
                SegmentHolder holder = mRecyclerView.peekTarget(ParagraphSegmentHolder.class);
                if (holder != null) {
                    mTarget = holder.getSegment();
                }
            }
        }

        {
            Handler handler = this.getHandler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setTarget(mTarget);
                }
            });
        }

        {
            mParent.mAdapter.notifyDataSetChanged();
            mParent.mDividerDecor.setEnable(false);
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();

        this.save();

        this.mFormatFragment = null;
    }

    @Override
    protected void onBindParagraph(ParagraphSegmentHolder holder) {
        super.onBindParagraph(holder);

        ParagraphView mParagraphView = holder.mParagraphView;

        mParagraphView.setEditable(false);
        mParagraphView.setShowSoftInputOnFocus(false);

        mParagraphView.setOnClickListener(mParagraphAction);
        mParagraphView.setOnLongClickListener(null);
        mParagraphView.setOnTouchListener(null);
        mParagraphView.setOnKeyListener(null);

        mParagraphView.setHint(null);

        if (holder.getSegment() == mTarget) {
            holder.mParagraphView.selectAll();
            holder.mParagraphView.requestFocus();
        }
    }

    @Override
    public void onViewDetachedFromWindow(SegmentHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {

        while (true) {
            if (Math.abs(velocityY) < FLING_TRIGGER) {
                break;
            }

            if (mTarget != null) {

                ParagraphSegmentHolder holder = (ParagraphSegmentHolder)mRecyclerView.obtainHolder(mTarget);
                if (holder != null) {
                    ParagraphView mParagraphView = holder.mParagraphView;

                    mParagraphView.selectNone();
                }

                mTarget = null;
                mParent.setFocus(null);

                mFormatFragment.reset();
                mParent.collapse();

                break;
            }

            {
                break;
            }
        }

        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    public void setTarget(Segment target) {

        if (mTarget != target) {
            ParagraphSegmentHolder holder = (ParagraphSegmentHolder)mRecyclerView.obtainHolder(target);
            ParagraphView mParagraphView = holder.mParagraphView;

            mParagraphView.selectAll();
            mParagraphView.requestFocus();
        }

        {
            Segment segment = target;

            TextFormatFragment f = new TextFormatFragment();
            f.setArguments(this, (ParagraphSegment)segment);

            mParent.showFragment("format", f);

            this.mFormatFragment = f;
        }

        this.mTarget = target;
        mParent.setFocus(mTarget);
    }

    public void applyStyle() {
        mParent.mAdapter.notifyDataSetChanged();
    }

    public void applyStyle(ParagraphSegment segment) {
        ParagraphSegmentHolder target = mParent.findParagraphHolder(segment);
        if (target != null) {

            boolean result = mAdapter.notifyItemChanged(segment.getId());
            if (!result) {
                StyleEntity itemStyle = segment.getStyle();
                StyleEntity noteStyle = segment.getDefaultStyle();

                target.mParagraphView.applyStyle(itemStyle, noteStyle);
            }

            {
                mKeepFocusRunnable.mTarget = target;
                getHandler().post(mKeepFocusRunnable);
            }

        }
    }

    public final void save() {
        mParent.saveNote();
    }

    /**
     *
     */
    SegmentAction mParagraphAction = new SegmentAction<ParagraphSegmentHolder>() {

        @Override
        protected void onClick(ParagraphSegmentHolder holder, View v) {

            if (holder.getSegment() != mTarget) {
                ParagraphView mParagraphView = holder.mParagraphView;
                if (mParagraphView.length() != 0) {
                    setTarget(holder.getSegment());
                }
            }
        }
    };

    /**
     * 保存格式化分段在可见范围内
     *
     */
    private class KeepFocusRunnable implements Runnable {

        RecyclerView.ViewHolder mTarget;

        @Override
        public void run() {

            int dy = 0;

            int top = mTarget.itemView.getTop();
            int bottom = mTarget.itemView.getBottom();
            int height = mRecyclerView.getHeight();
            if (top >= 0 && bottom <= height) {
                int x = (height - (bottom - top)) / 2;
                dy = (top - x);

            } else {
                int half = height / 2;
                if (bottom < half) {
                    dy = -(height - bottom);
                } else if (top > half) {
                    dy = top;
                }
            }

            if (dy != 0) {
                mRecyclerView.smoothScrollBy(0, dy);
            }
        }
    }

}
