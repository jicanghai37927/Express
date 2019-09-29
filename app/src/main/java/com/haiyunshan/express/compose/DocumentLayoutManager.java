package com.haiyunshan.express.compose;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.ComposeLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haiyunshan.express.app.SoftKeyboardUtils;

/**
 *
 *
 */
public class DocumentLayoutManager extends ComposeLayoutManager {

    static final String TAG = "DocumentLayoutManager";

    NoteComposeFragment mParent;

    public DocumentLayoutManager(Context context, NoteComposeFragment parent) {
        super(context);

        this.mParent = parent;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    @Override
    public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
//        return super.onRequestChildFocus(parent, state, child, focused);

        return true;
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
//        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);

        if (SoftKeyboardUtils.isVisible((Activity)parent.getContext())) {
            return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
        }

        return false;
    }
}
