package com.haiyunshan.express.compose.holder;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.dataset.note.page.Page;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;

/**
 *
 */
public class ParagraphSegmentHolder extends SegmentHolder<ParagraphSegment> {

    public ParagraphView mParagraphView;

    public static ParagraphSegmentHolder create(NoteComposeFragment fragment, ViewGroup parent, int type) {

        View view = create(fragment.getActivity(), parent, null);
        ParagraphSegmentHolder holder = new ParagraphSegmentHolder(fragment, view, type);

        return holder;
    }

    public static View create(Activity context, ViewGroup parent, ParagraphSegment segment) {
        LayoutInflater inflater = context.getLayoutInflater();

        int resource = R.layout.layout_note_paragraph_item;
        View view = inflater.inflate(resource, parent, false);
        if (segment != null) {

            ParagraphView mParagraphView = (ParagraphView)(view.findViewById(R.id.tv_text));

            {
                Page page = segment.getDocument().getPage();
                int left = (int) WindowUtils.dp2px(page.getPaddingLeft());
                int top = mParagraphView.getPaddingTop();
                int right = (int)WindowUtils.dp2px(page.getPaddingRight());
                int bottom = mParagraphView.getPaddingBottom();

                mParagraphView.setPadding(left, top, right, bottom);
            }

            {
                mParagraphView.applyStyle(segment.getStyle(), segment.getDefaultStyle());
            }

            mParagraphView.setText(segment.getText());

            mParagraphView.setEditable(false);

        }

        return view;
    }

    public ParagraphSegmentHolder(NoteComposeFragment fragment, View itemView, int type) {
        super(fragment, itemView, type);

        this.mParagraphView = (ParagraphView)itemView.findViewById(R.id.tv_text);

    }

    @CallSuper
    @Override
    public void onBind(int position, ParagraphSegment segment) {
        super.onBind(position, segment);

        {
            mParagraphView.setSelectionActionModeCallback(null);
        }

        {
            mParagraphView.setTag(R.id.tag_segment, this);
        }

        {
            Page page = this.getDocument().getPage();
            int left = (int) WindowUtils.dp2px(page.getPaddingLeft());
            int top = mParagraphView.getPaddingTop();
            int right = (int)WindowUtils.dp2px(page.getPaddingRight());
            int bottom = mParagraphView.getPaddingBottom();

            mParagraphView.setPadding(left, top, right, bottom);
        }

        {
            mParagraphView.applyStyle(segment.getStyle(), segment.getDefaultStyle());
        }

        mParagraphView.setText(segment.getText());

        {
            Segment last = getDocument().lastParagraph();

            int minLines = 1;
            if (segment == last) {
                minLines = 11;
            }

            mParagraphView.setMinLines(minLines);
        }
    }

    @Override
    public void requestFocus() {
        mParagraphView.requestFocus();
    }

    public void setSelectionEnd() {
        if (mSegment.hasSelection()) {
            mParagraphView.setSelection(mSegment.getSelectionEnd());
        }
    }

    public void setSelection() {
        if (mSegment.hasSelection()) {
            mParagraphView.setSelection(mSegment.getSelectionStart(), mSegment.getSelectionEnd());
        }
    }

    public int getSelectionStart() {
        return mParagraphView.getSelectionStart();
    }

    public int getSelectionEnd() {
        return mParagraphView.getSelectionEnd();
    }

    public void setHint(CharSequence hint) {
        mParagraphView.setHint(hint);
    }

    public void updateHint() {

        Segment segment = this.getSegment();
        if (this.getSegment() == getDocument().getTitle()) {
            mParagraphView.setHint("标题");
        } else if (this.getSegment() == getDocument().getSubtitle()) {
            mParagraphView.setHint("副标题");
        } else {

            Segment first = getDocument().peekParagraph();
            Segment last = getDocument().lastParagraph();

            {
                String hint = null;
                if ((first == last) && (segment == first)) {
                    hint = ("正文");
                }
                mParagraphView.setHint(hint);
            }

        }
    }
}
