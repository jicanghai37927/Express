package com.haiyunshan.express.compose.holder;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.compose.DocumentState;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.widget.DocumentView;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;

public class SegmentHolder<SEG extends Segment> extends RecyclerView.ViewHolder {

    public static final int TYPE_TITLE      = 1;
    public static final int TYPE_SUBTITLE   = 2;

    public static final int TYPE_PARAGRAPH  = 101;
    public static final int TYPE_MARGIN     = 201;
    public static final int TYPE_STOP       = 301;
    public static final int TYPE_PICTURE    = 401;

    int mType;

    int mPosition;
    SEG mSegment;

    NoteComposeFragment mParent;
    Activity mContext;
    DocumentView mRecyclerView;

    public SegmentHolder(NoteComposeFragment parent, View itemView, int type) {
        super(itemView);

        this.mType = type;
        this.mParent = parent;
        this.mContext = parent.getActivity();
        this.mRecyclerView = parent.mRecyclerView;
    }

    public final void bind(int position, SEG segment) {
        this.mPosition = position;
        this.mSegment = segment;

        this.onBind(position, segment);

        DocumentState state = mParent.peekState();
        if (state != null) {
            state.bind(this);
        }
    }

    public int getType() {
        return this.mType;
    }

    public Document getDocument() {
        return mSegment.getDocument();
    }

    public SEG getSegment() {
        return this.mSegment;
    }

    public void requestFocus() {

    }

    public boolean hasChild(View child) {
        return isChildOf(this.itemView, child);
    }

    boolean isChildOf(View view, View child) {
        if (view == child) {
            return true;
        }

        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup)view;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                boolean value = isChildOf(parent.getChildAt(i), child);
                if (value) {
                    return value;
                }
            }
        }

        return false;
    }

    @CallSuper
    protected void onBind(int position, SEG segment) {

    }
}
