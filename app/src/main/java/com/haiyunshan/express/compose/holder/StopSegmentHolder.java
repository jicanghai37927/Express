package com.haiyunshan.express.compose.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.note.segment.StopSegment;

/**
 * Created by sanshibro on 2018/2/7.
 */

public class StopSegmentHolder extends SegmentHolder<StopSegment> implements View.OnLongClickListener {

    TextView mNameView;

    public static StopSegmentHolder create(NoteComposeFragment fragment, ViewGroup parent, int type) {
        LayoutInflater inflater = fragment.getActivity().getLayoutInflater();

        int resource = R.layout.layout_note_stop_item;
        View view = inflater.inflate(resource, parent, false);
        StopSegmentHolder holder = new StopSegmentHolder(fragment, view, type);

        return holder;
    }

    public StopSegmentHolder(NoteComposeFragment fragment, View itemView, int type) {
        super(fragment, itemView, type);

        this.mNameView = (itemView.findViewById(R.id.tv_name));

        itemView.setOnLongClickListener(this);
    }

    @Override
    protected void onBind(int position, StopSegment segment) {
        super.onBind(position, segment);

        int index = mParent.mAdapter.indexStop(segment);
        String text = String.format("停止符 第%1$d", index + 1);
        mNameView.setText(text);
    }

    @Override
    public boolean onLongClick(View v) {
        this.removeStop(this.mSegment);

        return false;
    }

    void removeStop(StopSegment segment) {
        {
            this.getDocument().remove(segment);
            this.getDocument().save();
        }

        {
            mParent.mAdapter.remove(segment.getId(), true);
        }
    }
}
