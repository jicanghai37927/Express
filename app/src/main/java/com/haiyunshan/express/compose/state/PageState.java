package com.haiyunshan.express.compose.state;

import android.view.View;

import com.haiyunshan.express.compose.DocumentAdapter;
import com.haiyunshan.express.compose.DocumentState;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.fragment.PageFragment;
import com.haiyunshan.express.compose.widget.ParagraphView;

/**
 *
 */
public class PageState extends DocumentState {

    public PageState(NoteComposeFragment fragment) {
        super(fragment);
    }

    @Override
    public void onStart() {
        super.onStart();

        {
            mParent.mPhotoBtn.setVisibility(View.GONE);
        }

        {
            mParent.setFocus(null);
        }

        {
            mParent.mHandler.post(new Runnable() {
                @Override
                public void run() {

                    PageFragment f = new PageFragment();
                    f.setArguments(PageState.this);

                    mParent.showFragment("page_setting", f);
                }
            });
        }

        {
            mParent.mDividerDecor.setEnable(false);
            mParent.mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onEnd() {
        super.onEnd();

        mParent.setFocus(null);
    }

    @Override
    protected void onBindParagraph(ParagraphSegmentHolder holder) {
        super.onBindParagraph(holder);

        ParagraphView mParagraphView = holder.mParagraphView;

        mParagraphView.setEditable(false);
        mParagraphView.setShowSoftInputOnFocus(false);

        mParagraphView.setOnClickListener(null);
        mParagraphView.setOnLongClickListener(null);
        mParagraphView.setOnTouchListener(null);
        mParagraphView.setOnKeyListener(null);

        mParagraphView.setHint(null);
    }

    public void notifyPageChanged() {
        DocumentAdapter mAdapter = mParent.mAdapter;
        mAdapter.notifyDataSetChanged();
    }


}
