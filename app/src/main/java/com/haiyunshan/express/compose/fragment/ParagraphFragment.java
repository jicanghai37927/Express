package com.haiyunshan.express.compose.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.ParagraphConstants;
import com.haiyunshan.express.app.ParagraphStyleUtils;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.dataset.note.entity.ParagraphEntity;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.typeface.TypefaceEntry;
import com.haiyunshan.express.typeface.TypefaceManager;
import com.haiyunshan.express.widget.CheckableImageView;
import com.haiyunshan.express.widget.CheckableTextView;

import static com.haiyunshan.express.app.ParagraphConstants.deltaLetterSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.deltaLineSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.deltaTextSize;
import static com.haiyunshan.express.app.ParagraphConstants.maxLetterSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.maxLineSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.maxTextSize;
import static com.haiyunshan.express.app.ParagraphConstants.minLetterSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.minLineSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.minTextSize;

/**
 * 文本
 *
 */
public class ParagraphFragment extends Fragment implements View.OnClickListener {

    View mDoneBtn;

    ScrollView mScrollView;

    View mStyleItem;
    TextView mStyleView;

    View mTypefaceItem;
    TextView mTypefaceView;

    CheckableTextView mItalicView;
    CheckableTextView mBoldView;
    CheckableTextView mUnderlineView;

    TextView mTextSizeView;
    View mSmallerTextSizeBtn;
    View mBiggerTextSizeBtn;

    CheckableImageView mAlignLeftBtn;
    CheckableImageView mAlignCenterBtn;
    CheckableImageView mAlignRightBtn;

    CheckableTextView mWrapContentBtn;
    CheckableTextView mMatchParentBtn;

    TextView mWordSpaceView;
    View mSmallerWordSpaceBtn;
    View mBiggerWordSpaceBtn;

    TextView mLineSpaceView;
    View mLessLineSpaceBtn;
    View mMoreLineSpaceBtn;

    View mSaveBtn;

    String mTypeface;

    boolean mItalic;
    boolean mBold;
    boolean mUnderline;

    ParagraphConstants.IntParams mTextSizeParams;

    int mTextColor;

    int mAlignment;
    boolean mWrapContent;

    ParagraphConstants.FractionParams mLetterSpacingParams;
    ParagraphConstants.FractionParams mLineSpacingParams;

    TextFormatFragment mParent;
    ParagraphSegment mSegment;

    public ParagraphFragment() {

    }

    public void setArguments(TextFormatFragment parent, ParagraphSegment segment) {
        this.mParent = parent;
        this.mSegment = segment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paragraph, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mDoneBtn = view.findViewById(R.id.btn_done);
        mDoneBtn.setOnClickListener(this);

        this.mScrollView = (ScrollView)(view.findViewById(R.id.sv_view));

        {
            this.mStyleItem = view.findViewById(R.id.item_style);
            mStyleItem.setOnClickListener(this);

            this.mStyleView = (TextView)(view.findViewById(R.id.tv_style_name));
            mStyleView.setClipToOutline(false);
        }

        {
            this.mTypefaceItem = view.findViewById(R.id.item_typeface);
            mTypefaceItem.setOnClickListener(this);

            this.mTypefaceView = (TextView)(view.findViewById(R.id.tv_typeface_name));
        }

        {
            this.mItalicView = (CheckableTextView)(view.findViewById(R.id.cb_italic));
            mItalicView.setOnClickListener(this);

            this.mBoldView = (CheckableTextView)(view.findViewById(R.id.cb_bold));
            mBoldView.setOnClickListener(this);

            this.mUnderlineView = (CheckableTextView)(view.findViewById(R.id.cb_underline));
            mUnderlineView.setOnClickListener(this);

            mUnderlineView.getPaint().setUnderlineText(true);
        }
        {
            this.mTextSizeView = (TextView)(view.findViewById(R.id.tv_text_size));

            this.mSmallerTextSizeBtn = view.findViewById(R.id.btn_smaller_text_size);
            mSmallerTextSizeBtn.setOnClickListener(this);

            this.mBiggerTextSizeBtn = view.findViewById(R.id.btn_bigger_text_size);
            mBiggerTextSizeBtn.setOnClickListener(this);
        }

        {
            this.mAlignLeftBtn = (CheckableImageView)(view.findViewById(R.id.btn_align_left));
            mAlignLeftBtn.setOnClickListener(this);

            this.mAlignCenterBtn = (CheckableImageView)(view.findViewById(R.id.btn_align_center));
            mAlignCenterBtn.setOnClickListener(this);

            this.mAlignRightBtn = (CheckableImageView)(view.findViewById(R.id.btn_align_right));
            mAlignRightBtn.setOnClickListener(this);
        }

        {
            this.mWrapContentBtn = (CheckableTextView)(view.findViewById(R.id.cb_wrap_content));
            mWrapContentBtn.setOnClickListener(this);

            this.mMatchParentBtn = (CheckableTextView)(view.findViewById(R.id.cb_match_parent));
            mMatchParentBtn.setOnClickListener(this);
        }

        {
            this.mWordSpaceView = (TextView)(view.findViewById(R.id.tv_word_space));

            this.mSmallerWordSpaceBtn = view.findViewById(R.id.btn_smaller_word_space);
            mSmallerWordSpaceBtn.setOnClickListener(this);

            this.mBiggerWordSpaceBtn = view.findViewById(R.id.btn_bigger_word_space);
            mBiggerWordSpaceBtn.setOnClickListener(this);
        }

        {
            this.mLineSpaceView = (TextView)(view.findViewById(R.id.tv_line_space));

            this.mLessLineSpaceBtn = view.findViewById(R.id.btn_less_line_space);
            mLessLineSpaceBtn.setOnClickListener(this);

            this.mMoreLineSpaceBtn = view.findViewById(R.id.btn_more_line_space);
            mMoreLineSpaceBtn.setOnClickListener(this);
        }

        {
            this.mSaveBtn = view.findViewById(R.id.btn_save);
            mSaveBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StyleEntity style = mSegment.getStyle();
        StyleEntity defaultStyle = mSegment.getDefaultStyle();

        {
            this.mTypeface = ParagraphStyleUtils.getTypeface(style, defaultStyle);

            this.mItalic = ParagraphStyleUtils.getItalic(style, defaultStyle);
            this.mBold = ParagraphStyleUtils.getBold(style, defaultStyle);
            this.mUnderline = ParagraphStyleUtils.getUnderline(style, defaultStyle);

            int textSize = ParagraphStyleUtils.getTextSize(style, defaultStyle);
            this.mTextSizeParams = new ParagraphConstants.IntParams(textSize, maxTextSize, minTextSize, deltaTextSize, "%1$d磅");

            this.mTextColor = ParagraphStyleUtils.getTextColor(style, defaultStyle);

            this.mAlignment = ParagraphStyleUtils.getAlignment(style, defaultStyle);

            this.mWrapContent = ParagraphStyleUtils.getWrapContent(style, defaultStyle);

            int letterSpacing = ParagraphStyleUtils.getLetterSpacing(style, defaultStyle);
            letterSpacing += 100;
            this.mLetterSpacingParams = new ParagraphConstants.FractionParams(letterSpacing, maxLetterSpacing, minLetterSpacing, deltaLetterSpacing, "%1$1.1f");

            int lineSpacing = ParagraphStyleUtils.getLineSpacing(style, defaultStyle);
            this.mLineSpacingParams = new ParagraphConstants.FractionParams(lineSpacing, maxLineSpacing, minLineSpacing, deltaLineSpacing, "%1$1.1f");
        }

        {
            mStyleView.setText(defaultStyle.getName());
            this.applyStyle(false);
        }

        {
            TypefaceEntry e = TypefaceManager.instance().obtain(mTypeface);
            mTypefaceView.setText(e.getAlias());

            Typeface tf = TypefaceManager.instance().getTypeface(e, false, false);
            mTypefaceView.setTypeface(tf);
        }

        {
            this.mItalicView.setChecked(mItalic);

            this.mBoldView.setChecked(mBold);

            this.mUnderlineView.setChecked(mUnderline);
        }

        {
            mSmallerTextSizeBtn.setEnabled(!mTextSizeParams.isMin());
            mBiggerTextSizeBtn.setEnabled(!mTextSizeParams.isMax());

            this.mTextSizeView.setText(mTextSizeParams.toString());
        }

        {
            mAlignLeftBtn.setChecked((mAlignment == ParagraphEntity.ALIGN_START));
            mAlignCenterBtn.setChecked((mAlignment == ParagraphEntity.ALIGN_CENTER));
            mAlignRightBtn.setChecked((mAlignment == ParagraphEntity.ALIGN_END));
        }

        {
            mWrapContentBtn.setChecked(mWrapContent);
            mMatchParentBtn.setChecked(!mWrapContent);
        }

        {
            mSmallerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMin());
            mBiggerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMax());

            this.mWordSpaceView.setText(mLetterSpacingParams.toString());
        }

        {
            mLessLineSpaceBtn.setEnabled(!mLineSpacingParams.isMin());
            mMoreLineSpaceBtn.setEnabled(!mLineSpacingParams.isMax());

            this.mLineSpaceView.setText(mLineSpacingParams.toString());
        }

        {
            boolean value = mSegment.isStyleChanged();
            mSaveBtn.setEnabled(value);
        }

        if (savedInstanceState != null) {
            int scrollY = savedInstanceState.getInt("scrollY", 0);
            mScrollView.scrollTo(0, scrollY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("scrollY", mScrollView.getScrollY());
    }

    @Override
    public void onClick(View v) {
        StyleEntity mStyle = mSegment.getStyle();

        if (v == mDoneBtn) {
            mParent.close();
        } else if (v == mStyleItem) {
            mParent.showStyle();
        } else if (v == mTypefaceItem) {
            mParent.showTypeface();
        } else if (v == mSaveBtn) {
            mSegment.getDefaultStyle().set(mSegment.getStyle());
            mSegment.getDocument().clearStyle(mSegment.getStyle().getId());

            mSaveBtn.setEnabled(false);

            mParent.getParent().applyStyle();

        } else if (v == mSmallerTextSizeBtn) {
            if (!mTextSizeParams.isMin()) {
                mTextSizeParams.decrease();

                mSmallerTextSizeBtn.setEnabled(!mTextSizeParams.isMin());
                mBiggerTextSizeBtn.setEnabled(!mTextSizeParams.isMax());

                this.mTextSizeView.setText(mTextSizeParams.toString());

                {
                    mStyle.setTextSize(mTextSizeParams.getValue());
                    this.applyStyle(true);
                }
            }
        } else if (v == mBiggerTextSizeBtn) {
            if (!mTextSizeParams.isMax()) {
                mTextSizeParams.increase();

                mSmallerTextSizeBtn.setEnabled(!mTextSizeParams.isMin());
                mBiggerTextSizeBtn.setEnabled(!mTextSizeParams.isMax());

                this.mTextSizeView.setText(mTextSizeParams.toString());

                {
                    mStyle.setTextSize(mTextSizeParams.getValue());
                    this.applyStyle(true);
                }
            }
        } else if (v == mItalicView) {
            this.mItalic = !mItalicView.isChecked();
            mItalicView.setChecked(mItalic);

            {
                mStyle.setItalic(mItalic);
                this.applyStyle(true);
            }
        } else if (v == mBoldView) {
            this.mBold = !mBoldView.isChecked();
            mBoldView.setChecked(mBold);

            {
                mStyle.setBold(mBold);
                this.applyStyle(true);
            }
        } else if (v == mUnderlineView) {
            this.mUnderline = !mUnderlineView.isChecked();
            mUnderlineView.setChecked(mUnderline);

            {
                mStyle.setUnderline(mUnderline);
                this.applyStyle(true);
            }

        } else if (v == mAlignLeftBtn) {
            boolean checked = mAlignLeftBtn.isChecked();
            if (!checked) {
                mAlignLeftBtn.setChecked(true);
                mAlignCenterBtn.setChecked(false);
                mAlignRightBtn.setChecked(false);

                this.mAlignment = ParagraphEntity.ALIGN_START;

                {
                    mStyle.setAlignment(mAlignment);
                    this.applyStyle(true);
                }
            }

        } else if (v == mAlignCenterBtn) {
            boolean checked = mAlignCenterBtn.isChecked();
            if (!checked) {
                mAlignLeftBtn.setChecked(false);
                mAlignCenterBtn.setChecked(true);
                mAlignRightBtn.setChecked(false);

                this.mAlignment = ParagraphEntity.ALIGN_CENTER;

                {
                    mStyle.setAlignment(mAlignment);
                    this.applyStyle(true);
                }
            }

        } else if (v == mAlignRightBtn) {
            boolean checked = mAlignRightBtn.isChecked();
            if (!checked) {
                mAlignLeftBtn.setChecked(false);
                mAlignCenterBtn.setChecked(false);
                mAlignRightBtn.setChecked(true);

                this.mAlignment = ParagraphEntity.ALIGN_END;

                {
                    mStyle.setAlignment(mAlignment);
                    this.applyStyle(true);
                }
            }

        } else if (v == mWrapContentBtn) {
            boolean checked = mWrapContentBtn.isChecked();
            if (!checked) {
                mWrapContentBtn.setChecked(true);
                mMatchParentBtn.setChecked(false);

                this.mWrapContent = true;

                {
                    mStyle.setWrapContent(mWrapContent);
                    this.applyStyle(true);
                }
            }

        } else if (v == mMatchParentBtn) {
            boolean checked = mMatchParentBtn.isChecked();
            if (!checked) {
                mWrapContentBtn.setChecked(false);
                mMatchParentBtn.setChecked(true);

                this.mWrapContent = false;

                {
                    mStyle.setWrapContent(mWrapContent);
                    this.applyStyle(true);
                }
            }

        } else if (v == mSmallerWordSpaceBtn) {
            if (!mLetterSpacingParams.isMin()) {
                mLetterSpacingParams.decrease();

                mSmallerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMin());
                mBiggerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMax());

                this.mWordSpaceView.setText(mLetterSpacingParams.toString());

                {
                    int value = mLetterSpacingParams.getIntValue();
                    value -= 100;
                    mStyle.setLetterMultiplier(value);
                    this.applyStyle(true);
                }
            }
        } else if (v == mBiggerWordSpaceBtn) {
            if (!mLetterSpacingParams.isMax()) {
                mLetterSpacingParams.increase();

                mSmallerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMin());
                mBiggerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMax());

                this.mWordSpaceView.setText(mLetterSpacingParams.toString());

                {
                    int value = mLetterSpacingParams.getIntValue();
                    value -= 100;
                    mStyle.setLetterMultiplier(value);
                    this.applyStyle(true);
                }
            }
        } else if (v == mLessLineSpaceBtn) {
            if (!mLineSpacingParams.isMin()) {
                mLineSpacingParams.decrease();

                {
                    mLessLineSpaceBtn.setEnabled(!mLineSpacingParams.isMin());
                    mMoreLineSpaceBtn.setEnabled(!mLineSpacingParams.isMax());

                    this.mLineSpaceView.setText(mLineSpacingParams.toString());
                }

                {
                    int value = mLineSpacingParams.getIntValue();
                    mStyle.setLineMultiplier(value);
                    this.applyStyle(true);
                }
            }

        } else if (v == mMoreLineSpaceBtn) {
            if (!mLineSpacingParams.isMax()) {

                mLineSpacingParams.increase();

                {
                    mLessLineSpaceBtn.setEnabled(!mLineSpacingParams.isMin());
                    mMoreLineSpaceBtn.setEnabled(!mLineSpacingParams.isMax());

                    this.mLineSpaceView.setText(mLineSpacingParams.toString());
                }

                {
                    int value = mLineSpacingParams.getIntValue();
                    mStyle.setLineMultiplier(value);
                    this.applyStyle(true);
                }
            }
        }
    }

    void applyStyle(boolean notify) {
        {
            StyleEntity mStyle = mSegment.getStyle();
            StyleEntity mDefaultStyle = mSegment.getDefaultStyle();

            ParagraphStyleUtils.apply(mStyleView, mStyle, mDefaultStyle);
            mStyleView.setTextAlignment(StyleEntity.ALIGN_START);
        }

        {
            boolean value = mSegment.isStyleChanged();
            mSaveBtn.setEnabled(value);
        }

        if (notify) {
            mParent.mParent.applyStyle(mSegment);
        }
    }
}
