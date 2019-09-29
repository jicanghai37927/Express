package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

/**
 *
 *
 */
public class ComposeLayoutManager extends LinearLayoutManager {

    static final String TAG = "ComposeLayoutManager";

    public ComposeLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
        return super.onRequestChildFocus(parent, state, child, focused);
    }

    @Override
    public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.onFocusSearchFailed(focused, focusDirection, recycler, state);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }

}

