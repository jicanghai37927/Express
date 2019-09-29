package com.haiyunshan.express;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.express.app.ParagraphConstants;
import com.haiyunshan.express.app.ParagraphStyleUtils;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.dataset.note.style.Style;
import com.haiyunshan.express.dataset.note.entity.ParagraphEntity;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.DocumentManager;
import com.haiyunshan.express.typeface.TypefaceEntry;
import com.haiyunshan.express.typeface.TypefaceManager;
import com.haiyunshan.express.widget.CheckableImageView;
import com.haiyunshan.express.widget.CheckableTextView;

import org.w3c.dom.Text;

import static com.haiyunshan.express.app.ParagraphConstants.deltaLetterSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.deltaLineSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.deltaTextSize;
import static com.haiyunshan.express.app.ParagraphConstants.maxLetterSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.maxLineSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.maxTextSize;
import static com.haiyunshan.express.app.ParagraphConstants.minLetterSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.minLineSpacing;
import static com.haiyunshan.express.app.ParagraphConstants.minTextSize;

public class StyleComposeActivity extends BaseActivity implements View.OnClickListener {

    View mBackBtn;
    TextView mTitleView;

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

    TextView mWordSpaceView;
    View mSmallerWordSpaceBtn;
    View mBiggerWordSpaceBtn;

    TextView mLineSpaceView;
    View mLessLineSpaceBtn;
    View mMoreLineSpaceBtn;

    String mTypeface;

    boolean mItalic;
    boolean mBold;
    boolean mUnderline;

    ParagraphConstants.IntParams mTextSizeParams;

    int mTextColor;

    int mAlignment;
    ParagraphConstants.FractionParams mLetterSpacingParams;
    ParagraphConstants.FractionParams mLineSpacingParams;

    Document mDocument;
    StyleEntity mEntity;

    public static final void startForResult(Activity context, int requestCode, String noteId, String entityId) {
        Intent intent = new Intent(context, StyleComposeActivity.class);
        intent.putExtra("noteId", noteId);
        intent.putExtra("entityId", entityId);
        context.startActivityForResult(intent, requestCode);

        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_compose);

        onViewCreated(findViewById(R.id.root_container), savedInstanceState);
        onActivityCreated(savedInstanceState);

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        {
            this.mBackBtn = view.findViewById(R.id.btn_back);
            mBackBtn.setOnClickListener(this);

            this.mTitleView = (TextView)(view.findViewById(R.id.tv_title));
        }


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
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        {
            String noteId = getIntent().getStringExtra("noteId");
            String entityId = getIntent().getStringExtra("entityId");

            this.mDocument = DocumentManager.instance().obtain(noteId);
            Style style = mDocument.getStyle();
            this.mEntity = style.obtain(entityId);
            if (mEntity == null && !style.getList().isEmpty()) {
                mEntity = style.getList().get(0);
            }
        }

        {
            mTitleView.setText(mEntity.getName());
        }

        {
            StyleEntity style = mEntity;
            StyleEntity defaultStyle = mEntity;

            this.mTypeface = ParagraphStyleUtils.getTypeface(style, defaultStyle);

            this.mItalic = ParagraphStyleUtils.getItalic(style, defaultStyle);
            this.mBold = ParagraphStyleUtils.getBold(style, defaultStyle);
            this.mUnderline = ParagraphStyleUtils.getUnderline(style, defaultStyle);

            int textSize = ParagraphStyleUtils.getTextSize(style, defaultStyle);
            this.mTextSizeParams = new ParagraphConstants.IntParams(textSize, maxTextSize, minTextSize, deltaTextSize, "%1$d磅");

            this.mTextColor = ParagraphStyleUtils.getTextColor(style, defaultStyle);

            this.mAlignment = ParagraphStyleUtils.getAlignment(style, defaultStyle);

            int letterSpacing = ParagraphStyleUtils.getLetterSpacing(style, defaultStyle);
            letterSpacing += 100;
            this.mLetterSpacingParams = new ParagraphConstants.FractionParams(letterSpacing, maxLetterSpacing, minLetterSpacing, deltaLetterSpacing, "%1$1.1f");

            int lineSpacing = ParagraphStyleUtils.getLineSpacing(style, defaultStyle);
            this.mLineSpacingParams = new ParagraphConstants.FractionParams(lineSpacing, maxLineSpacing, minLineSpacing, deltaLineSpacing, "%1$1.1f");
        }

        {
            mStyleView.setText(mEntity.getName());
            this.applyStyle();
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
            mSmallerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMin());
            mBiggerWordSpaceBtn.setEnabled(!mLetterSpacingParams.isMax());

            this.mWordSpaceView.setText(mLetterSpacingParams.toString());
        }

        {
            mLessLineSpaceBtn.setEnabled(!mLineSpacingParams.isMin());
            mMoreLineSpaceBtn.setEnabled(!mLineSpacingParams.isMax());

            this.mLineSpaceView.setText(mLineSpacingParams.toString());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("entityId", mEntity.getId());
        this.setResult(RESULT_OK, intent);

        super.onBackPressed();

        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.onBackPressed();
        }

        if (v == mStyleItem) {

        } else if (v == mTypefaceItem) {

        } else if (v == mSmallerTextSizeBtn) {
            if (!mTextSizeParams.isMin()) {
                mTextSizeParams.decrease();

                mSmallerTextSizeBtn.setEnabled(!mTextSizeParams.isMin());
                mBiggerTextSizeBtn.setEnabled(!mTextSizeParams.isMax());

                this.mTextSizeView.setText(mTextSizeParams.toString());

                {
                    mEntity.setTextSize(mTextSizeParams.getValue());
                    this.applyStyle();
                }
            }
        } else if (v == mBiggerTextSizeBtn) {
            if (!mTextSizeParams.isMax()) {
                mTextSizeParams.increase();

                mSmallerTextSizeBtn.setEnabled(!mTextSizeParams.isMin());
                mBiggerTextSizeBtn.setEnabled(!mTextSizeParams.isMax());

                this.mTextSizeView.setText(mTextSizeParams.toString());

                {
                    mEntity.setTextSize(mTextSizeParams.getValue());
                    this.applyStyle();
                }
            }
        } else if (v == mItalicView) {
            this.mItalic = !mItalicView.isChecked();
            mItalicView.setChecked(mItalic);

            {
                mEntity.setItalic(mItalic);
                this.applyStyle();
            }
        } else if (v == mBoldView) {
            this.mBold = !mBoldView.isChecked();
            mBoldView.setChecked(mBold);

            {
                mEntity.setBold(mBold);
                this.applyStyle();
            }
        } else if (v == mUnderlineView) {
            this.mUnderline = !mUnderlineView.isChecked();
            mUnderlineView.setChecked(mUnderline);

            {
                mEntity.setUnderline(mUnderline);
                this.applyStyle();
            }
        } else if (v == mAlignLeftBtn) {
            boolean checked = mAlignLeftBtn.isChecked();
            if (!checked) {
                mAlignLeftBtn.setChecked(true);
                mAlignCenterBtn.setChecked(false);
                mAlignRightBtn.setChecked(false);

                this.mAlignment = ParagraphEntity.ALIGN_START;

                {
                    mEntity.setAlignment(mAlignment);
                    this.applyStyle();
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
                    mEntity.setAlignment(mAlignment);
                    this.applyStyle();
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
                    mEntity.setAlignment(mAlignment);
                    this.applyStyle();
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
                    mEntity.setLetterMultiplier(value);
                    this.applyStyle();
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
                    mEntity.setLetterMultiplier(value);
                    this.applyStyle();
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
                    mEntity.setLineMultiplier(value);
                    this.applyStyle();
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
                    mEntity.setLineMultiplier(value);
                    this.applyStyle();
                }
            }
        }
    }

    void applyStyle() {
        ParagraphStyleUtils.apply(mStyleView, mEntity, mEntity);
    }

}
