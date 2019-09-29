package com.haiyunshan.express.compose.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.ComposeView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.note.Segment;

/**
 *
 */
public class DocumentView extends ComposeView {

    static final String TAG = "DocumentView";

    int mDisplayHeight = 0;

    Drawable mFixBackground;
    boolean mBackgroundSizeChanged = true;

    NestedScrollingParent mNestedScrollingParent;

    public DocumentView(Context context) {
        this(context, null);
    }

    public DocumentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DocumentView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.init(context);
    }

    void init(Context context) {
        this.mDisplayHeight = WindowUtils.getDisplayHeight((Activity)context);

        this.mBackgroundSizeChanged = true;
        this.mFixBackground = context.getDrawable(R.drawable.shape_paper);
    }

    public void setNestedScrollingParent(NestedScrollingParent parent) {
        this.mNestedScrollingParent = parent;
    }

    public SegmentHolder getChildViewHolder(View child) {
        ViewHolder holder = super.getChildViewHolder(child);
        return (SegmentHolder)holder;
    }

    public SegmentHolder getViewHolder(int index) {
        if (index < 0 || index >= this.getChildCount()) {
            return null;
        }

        View child = this.getChildAt(index);
        return this.getChildViewHolder(child);
    }

    public <T> T peekTarget(Class<T> kind) {

        if (this.getChildCount() == 0) {
            return null;
        }

        Object first = null;

        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);

            RecyclerView.ViewHolder holder = this.getChildViewHolder(child);
            Object h = null;
            if (kind.isInstance(holder)) {
                h = holder;
            }

            if (h == null) {
                continue;
            }

            first = h;
            break;

        }

        return (T)first;
    }

    public <T> T getTarget(Class<T> kind) {

        if (this.getChildCount() == 0) {
            return null;
        }

        Object first = null;
        Object top = null;
        Object target = null;

        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);

            RecyclerView.ViewHolder holder = this.getChildViewHolder(child);
            Object h = null;
            if (kind.isInstance(holder)) {
                h = holder;
            }

            if (h == null) {
                continue;
            }

            // 最适合的一个
            if (child.getTop() >= 0
                    && child.getBottom() <= this.getHeight()) {
                target = h;
                break;
            }

            // 出现的第一个
            if (first == null) {
                first = h;
            }

            // 范围内的第一个
            if ((top == null)) {
                if ((child.getTop() >= 0)) {
                    top = h;
                }
            }
        }

        if (target != null) {
            return (T)target;
        }

        if (top != null) {
            return (T)top;
        }

        return (T)first;
    }

    public SegmentHolder obtainHolder(Segment segment) {
        if (segment == null) {
            return null;
        }

        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            SegmentHolder holder = this.getChildViewHolder(child);
            if (holder.getSegment() == segment) {
                return holder;
            }
        }

        return null;
    }

    public Drawable getFixBackground() {
        return this.mFixBackground;
    }

    public void drawBackground(Canvas canvas) {
        if (mFixBackground == null) {
            return;
        }

        // 重新计算背景
        if (mBackgroundSizeChanged) {
            mBackgroundSizeChanged = false;

            int width = this.getWidth();

            int range = this.computeVerticalScrollRange();

            int height = mFixBackground.getBounds().height();
            height -= 2 * mDisplayHeight;
            if (height != range) {
                mFixBackground.setBounds(0, 0, width, range + 2 * mDisplayHeight);
            }
        }

        {
            int offset = this.computeVerticalScrollOffset();
            int dy = -offset;
            dy -= mDisplayHeight;

            canvas.save();
            canvas.translate(0, dy);

            mFixBackground.draw(canvas);

            canvas.restore();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.drawBackground(canvas);

        super.onDraw(canvas);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        this.mBackgroundSizeChanged = true;
    }

    @Override
    public boolean startNestedScroll(int axes) {
        super.startNestedScroll(axes);

        return true;
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {

        if (mNestedScrollingParent != null) {
            mNestedScrollingParent.onNestedScroll(this, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        }

        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {

        if (mNestedScrollingParent != null) {
            mNestedScrollingParent.onNestedPreScroll(this, dy, dy, consumed);
        }

        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {

        if (mNestedScrollingParent != null) {
            mNestedScrollingParent.onNestedFling(this, velocityX, velocityY, consumed);
        }

        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {

        if (mNestedScrollingParent != null) {
            mNestedScrollingParent.onNestedPreFling(this, velocityX, velocityY);
        }

        return super.dispatchNestedPreFling(velocityX, velocityY);
    }

}
