package com.haiyunshan.express.compose;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.compose.widget.DocumentView;
import com.haiyunshan.express.note.Document;


/**
 * 文档编辑状态
 *
 */
public abstract class DocumentState implements NestedScrollingParent {

    public static final int FLING_TRIGGER = 4200;

    protected DocumentView mRecyclerView;
    protected DocumentAdapter mAdapter;
    protected DocumentLayoutManager mLayoutManager;

    protected Document mDocument;

    protected NoteComposeFragment mParent;
    protected Activity mContext;

    /**
     *
     * @param parent
     */
    public DocumentState(NoteComposeFragment parent) {
        this.mRecyclerView = parent.mRecyclerView;
        this.mAdapter = parent.mAdapter;
        this.mLayoutManager = parent.mLayoutManager;

        this.mDocument = parent.getDocument();

        this.mParent = parent;
        this.mContext = parent.getActivity();
    }

    /**
     *
     * @return
     */
    public NoteComposeFragment getFragment() {
        return mParent;
    }

    /**
     *
     * @return
     */
    public Document getDocument() {
        return mDocument;
    }

    /**
     *
     * @return
     */
    public Handler getHandler() {
        return mParent.mHandler;
    }

    /**
     *
     */
    public void pop() {
        mParent.popState();
    }

    /**
     *
     */
    public final void start() {
        this.onStart();
    }

    /**
     *
     */
    public final void end() {
        this.onEnd();
    }

    /**
     *
     * @param holder
     */
    public final void bind(SegmentHolder holder) {
        int type = holder.getType();
        switch (type) {
            case SegmentHolder.TYPE_TITLE:
            case SegmentHolder.TYPE_SUBTITLE:
            case SegmentHolder.TYPE_PARAGRAPH:
            {
                this.onBindParagraph((ParagraphSegmentHolder)holder);
                break;
            }
        }
    }

    protected void onStart() {

    }

    protected void onEnd() {

    }

    protected void onBindParagraph(ParagraphSegmentHolder holder) {

    }

    public void onViewAttachedToWindow(SegmentHolder holder) {

    }

    public void onViewDetachedFromWindow(SegmentHolder holder) {

    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return false;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

    }

    @Override
    public void onStopNestedScroll(View target) {

    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
