package com.haiyunshan.express.compose.state;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.app.SoftKeyboardUtils;
import com.haiyunshan.express.compose.DocumentState;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.action.SegmentAction;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;

/**
 * 编辑
 */
public class ComposeState extends DocumentState {

    static final String TAG = "ComposeState";

    public ComposeState(NoteComposeFragment parent) {
        super(parent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 更新焦点Segment，决定是否弹出键盘
        {
            ParagraphSegment focus = mParent.findFocus();
            mParent.setFocus(focus);
        }

        // 顶部菜单栏
        {
            mParent.mDoneBtn.setVisibility(View.VISIBLE);
            mParent.mBackBtn.setVisibility(View.GONE);

            mParent.mPhotoBtn.setVisibility(View.VISIBLE);

            mParent.mCreateBtn.setVisibility(View.VISIBLE);
            mParent.mComposeBtn.setVisibility(View.GONE);
        }

        // 软键盘
        if (mParent.getFocus() == null) {

            mParent.hideFragment();
            SoftKeyboardUtils.hide(mContext);

        } else {

            if (SoftKeyboardUtils.isVisible(mContext)) {
                mParent.hideFragment();
            } else {
                Handler handler = mParent.mHandler;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mParent.showKeyboard(mParent.getFocus());
                    }
                }, 100);
            }

        }

        // 内容
        {
            mParent.mAdapter.notifyDataSetChanged();
            mParent.mDividerDecor.setEnable(true);
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();

        mParent.saveNote();
    }

    @Override
    protected void onBindParagraph(ParagraphSegmentHolder holder) {
        super.onBindParagraph(holder);

        ParagraphView mParagraphView = holder.mParagraphView;

        mParagraphView.setEditable(true);
        mParagraphView.setShowSoftInputOnFocus(true);

        mParagraphView.setOnClickListener(null);
        mParagraphView.setOnLongClickListener(null);
        mParagraphView.setOnTouchListener(mParagraphAction);
        mParagraphView.setOnKeyListener(mParagraphAction);

        holder.updateHint();
    }

    @Override
    public void onViewDetachedFromWindow(SegmentHolder holder) {
        if (holder instanceof ParagraphSegmentHolder) {
            ParagraphSegmentHolder target = (ParagraphSegmentHolder)holder;
            target.getSegment().setText(target.mParagraphView.getText());
        }
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
                mParent.saveNote();

                mRecyclerView.setPreserveFocusAfterLayout(false);

                // 同时隐藏键盘
                SoftKeyboardUtils.hide(mContext);

                break;
            }

            {
                break;
            }
        }


        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    /**
     *
     */
    SegmentAction mParagraphAction = new SegmentAction<ParagraphSegmentHolder>() {

        @Override
        protected boolean onKey(ParagraphSegmentHolder holder, View v, int keyCode, KeyEvent event) {

            ParagraphView mParagraphView = holder.mParagraphView;
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (mParagraphView.getSelectionStart() == 0) {
                        mParent.requestDelete(holder);
                    }
                }
            }

            return false;
        }

        @Override
        protected boolean onTouch(ParagraphSegmentHolder holder, View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mParent.setFocus(holder.getSegment());
            }

            return super.onTouch(holder, v, event);
        }
    };

}
