package com.haiyunshan.express.compose.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.R;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.note.segment.MarginSegment;

/**
 * Created by sanshibro on 2018/2/7.
 */

public class MarginSegmentHolder extends SegmentHolder<MarginSegment> {


    public static MarginSegmentHolder create(NoteComposeFragment fragment, ViewGroup parent, int type) {
        LayoutInflater inflater = fragment.getActivity().getLayoutInflater();

        int resource = R.layout.layout_note_margin_item;
        View view = inflater.inflate(resource, parent, false);
        MarginSegmentHolder holder = new MarginSegmentHolder(fragment, view, type);

        return holder;
    }

    public MarginSegmentHolder(NoteComposeFragment fragment, View itemView, int type) {
        super(fragment, itemView, type);
    }

    @Override
    protected void onBind(int position, MarginSegment segment) {
        super.onBind(position, segment);

        itemView.getLayoutParams().height = segment.getHeight();
    }
}
