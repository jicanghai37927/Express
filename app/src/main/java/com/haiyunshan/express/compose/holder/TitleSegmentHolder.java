package com.haiyunshan.express.compose.holder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.dataset.note.page.Page;
import com.haiyunshan.express.note.segment.ParagraphSegment;

/**
 *
 */
public class TitleSegmentHolder extends ParagraphSegmentHolder {

    public static TitleSegmentHolder create(NoteComposeFragment fragment, ViewGroup parent, int type) {
        LayoutInflater inflater = fragment.getActivity().getLayoutInflater();

        int resource = R.layout.layout_note_paragraph_item;
        View view = inflater.inflate(resource, parent, false);
        TitleSegmentHolder holder = new TitleSegmentHolder(fragment, view, type);

        return holder;
    }

    public static View create(Activity context, ViewGroup parent, ParagraphSegment segment) {
        View view = ParagraphSegmentHolder.create(context, parent, segment);

        if (segment != null) {

            ParagraphView mParagraphView = (ParagraphView)(view.findViewById(R.id.tv_text));

            int left = mParagraphView.getPaddingLeft();
            int right = mParagraphView.getPaddingRight();

            int top = context.getResources().getDimensionPixelSize(R.dimen.title_item_padding);
            int bottom = top;

            mParagraphView.setPadding(left, top, right, bottom);
        }

        return view;
    }

    public TitleSegmentHolder(NoteComposeFragment fragment, View itemView, int type) {
        super(fragment, itemView, type);

        {
            int left = mParagraphView.getPaddingLeft();
            int right = mParagraphView.getPaddingRight();

            int top = fragment.getResources().getDimensionPixelSize(R.dimen.title_item_padding);
            int bottom = top;

            mParagraphView.setPadding(left, top, right, bottom);
        }

    }

    @Override
    public void onBind(int position, ParagraphSegment segment) {
        super.onBind(position, segment);

        if (mSegment == getDocument().getTitle()) {


            mParagraphView.setSingleLine(false);
            mParagraphView.setMinLines(1);

        } else if (mSegment == getDocument().getSubtitle()) {

            mParagraphView.setMinLines(1);
            mParagraphView.setSingleLine(true);

        }
    }

}
