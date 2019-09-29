package com.haiyunshan.express.fragment.note;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.ParagraphConstants;
import com.haiyunshan.express.dataset.note.page.Page;
import com.haiyunshan.express.note.Document;

import static com.haiyunshan.express.app.ParagraphConstants.deltaPadding;
import static com.haiyunshan.express.app.ParagraphConstants.maxPadding;
import static com.haiyunshan.express.app.ParagraphConstants.minPadding;

/**
 *
 */
public class PageFragment extends Fragment implements View.OnClickListener {

    View mDoneBtn;

    TextView mPaddingLeftView;
    View mSmallerPaddingLeftBtn;
    View mBiggerPaddingLeftBtn;

    TextView mPaddingRightView;
    View mLessPaddingRightBtn;
    View mMorePaddingRightBtn;

    ParagraphConstants.IntParams mPaddingLeftParams;
    ParagraphConstants.IntParams mPaddingRightParams;

    NoteComposeFragment mParent;

    public PageFragment() {

    }

    public void setArguments(NoteComposeFragment parent) {
        this.mParent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);
        }

        {
            this.mPaddingLeftView = (TextView)(view.findViewById(R.id.tv_padding_left));

            this.mSmallerPaddingLeftBtn = view.findViewById(R.id.btn_smaller_padding_left);
            mSmallerPaddingLeftBtn.setOnClickListener(this);

            this.mBiggerPaddingLeftBtn = view.findViewById(R.id.btn_bigger_padding_left);
            mBiggerPaddingLeftBtn.setOnClickListener(this);
        }

        {
            this.mPaddingRightView = (TextView)(view.findViewById(R.id.tv_padding_right));

            this.mLessPaddingRightBtn = view.findViewById(R.id.btn_less_padding_right);
            mLessPaddingRightBtn.setOnClickListener(this);

            this.mMorePaddingRightBtn = view.findViewById(R.id.btn_more_padding_right);
            mMorePaddingRightBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Page page = mParent.getDocument().getPage();

            int paddingLeft = page.getPaddingLeft();
            this.mPaddingLeftParams = new ParagraphConstants.IntParams(paddingLeft, maxPadding, minPadding, deltaPadding, "%1$d磅");

            int paddingRight = page.getPaddingRight();
            this.mPaddingRightParams = new ParagraphConstants.IntParams(paddingRight, maxPadding, minPadding, deltaPadding, "%1$d磅");
        }

        {
            mSmallerPaddingLeftBtn.setEnabled(!mPaddingLeftParams.isMin());
            mBiggerPaddingLeftBtn.setEnabled(!mPaddingLeftParams.isMax());

            this.mPaddingLeftView.setText(mPaddingLeftParams.toString());
        }

        {
            mLessPaddingRightBtn.setEnabled(!mPaddingRightParams.isMin());
            mMorePaddingRightBtn.setEnabled(!mPaddingRightParams.isMax());

            this.mPaddingRightView.setText(mPaddingRightParams.toString());
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mDoneBtn) {
            mParent.hidePageSetting();
            mParent.getDocument().save(Document.saveMaskPage);

        } else if (v == mSmallerPaddingLeftBtn) {
            if (!mPaddingLeftParams.isMin()) {
                mPaddingLeftParams.decrease();

                mSmallerPaddingLeftBtn.setEnabled(!mPaddingLeftParams.isMin());
                mBiggerPaddingLeftBtn.setEnabled(!mPaddingLeftParams.isMax());

                this.mPaddingLeftView.setText(mPaddingLeftParams.toString());

                {
                    int value = mPaddingLeftParams.getValue();

                    Page page = mParent.getDocument().getPage();
                    page.setPaddingLeft(value);

                    mParent.notifyPageChanged();
                }
            }
        } else if (v == mBiggerPaddingLeftBtn) {
            if (!mPaddingLeftParams.isMax()) {
                mPaddingLeftParams.increase();

                mSmallerPaddingLeftBtn.setEnabled(!mPaddingLeftParams.isMin());
                mBiggerPaddingLeftBtn.setEnabled(!mPaddingLeftParams.isMax());

                this.mPaddingLeftView.setText(mPaddingLeftParams.toString());

                {
                    int value = mPaddingLeftParams.getValue();

                    Page page = mParent.getDocument().getPage();
                    page.setPaddingLeft(value);

                    mParent.notifyPageChanged();
                }
            }
        } else if (v == mLessPaddingRightBtn) {
            if (!mPaddingRightParams.isMin()) {
                mPaddingRightParams.decrease();

                {
                    mLessPaddingRightBtn.setEnabled(!mPaddingRightParams.isMin());
                    mMorePaddingRightBtn.setEnabled(!mPaddingRightParams.isMax());

                    this.mPaddingRightView.setText(mPaddingRightParams.toString());
                }

                {
                    int value = mPaddingRightParams.getValue();

                    Page page = mParent.getDocument().getPage();
                    page.setPaddingRight(value);

                    mParent.notifyPageChanged();
                }
            }

        } else if (v == mMorePaddingRightBtn) {
            if (!mPaddingRightParams.isMax()) {

                mPaddingRightParams.increase();

                {
                    mLessPaddingRightBtn.setEnabled(!mPaddingRightParams.isMin());
                    mMorePaddingRightBtn.setEnabled(!mPaddingRightParams.isMax());

                    this.mPaddingRightView.setText(mPaddingRightParams.toString());
                }

                {
                    int value = mPaddingRightParams.getValue();

                    Page page = mParent.getDocument().getPage();
                    page.setPaddingRight(value);

                    mParent.notifyPageChanged();
                }
            }
        }
    }
}
