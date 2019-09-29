package com.haiyunshan.express.note.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.fragment.note.NoteComposeFragment;
import com.haiyunshan.express.R;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.segment.ParagraphSegment;

/**
 *
 */
public class TitleSegmentHolder extends ParagraphSegmentHolder {

    static TitleSegmentHolder create(NoteComposeFragment fragment, ViewGroup parent) {
        LayoutInflater inflater = fragment.getActivity().getLayoutInflater();

        int resource = R.layout.layout_note_paragraph_item;
        View view = inflater.inflate(resource, parent, false);
        TitleSegmentHolder holder = new TitleSegmentHolder(fragment, view);

        return holder;
    }

    public TitleSegmentHolder(NoteComposeFragment fragment, View itemView) {
        super(fragment, itemView);

        int left = mParagraphView.getPaddingLeft();
        int top = mParagraphView.getPaddingTop();
        int right = mParagraphView.getPaddingRight();
        int bottom = mParagraphView.getPaddingBottom();

        top = fragment.getResources().getDimensionPixelSize(R.dimen.title_item_padding);
        bottom = top;

        mParagraphView.setPadding(left, top, right, bottom);
    }

    @Override
    public void bind(int position, ParagraphSegment entity) {
        super.bind(position, entity);
    }

    @Override
    void applyMode(int mode) {
        super.applyMode(mode);


        {
            String hint = null;
            if (mode == NoteComposeFragment.MODE_EDIT) {

                Document n = this.getNote();
                if (mSegment == n.getTitle()) {
                    hint = "标题";
                } else if (mSegment == n.getSubtitle()) {
                    hint = "副标题";
                }
            }

            mParagraphView.setHint(hint);
        }
    }
}
