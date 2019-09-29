package com.haiyunshan.express.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.WindowUtils;

/**
 *
 */
public class MyRecyclerView extends RecyclerView {

    int mDisplayHeight = 0;
    boolean mBackgroundSizeChanged = true;
    Drawable mBackground;

    public MyRecyclerView(Context context) {
        this(context, null);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.init(context);
    }

    void init(Context context) {
        this.mDisplayHeight = WindowUtils.getDisplayHeight((Activity)context);

        this.mBackgroundSizeChanged = true;
        this.mBackground = context.getDrawable(R.drawable.shape_paper);
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.drawBackground(canvas);

        super.onDraw(canvas);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        this.mBackgroundSizeChanged = true;
    }

    public ViewHolder getFirstViewHolder() {
        if (this.getChildCount() == 0) {
            return null;
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            ViewHolder holder = this.getChildViewHolder(child);
            if (holder != null) {
                return holder;
            }
        }

        return null;
    }

    void drawBackground(Canvas canvas) {

        if (mBackground != null) {
            if (mBackgroundSizeChanged) {
                mBackgroundSizeChanged = false;

                int width = this.getWidth();

                int range = this.computeVerticalScrollRange();

                int height = mBackground.getBounds().height();
                height -= 2 * mDisplayHeight;
                if (height != range) {
                    mBackground.setBounds(0, 0, width, range + 2 * mDisplayHeight);
                }
            }

            int offset = this.computeVerticalScrollOffset();
            int dy = -offset;
            dy -= mDisplayHeight;

            canvas.save();
            canvas.translate(0, dy);

            mBackground.draw(canvas);

            canvas.restore();
        }
    }

    /**
     *
     * @param <VH>
     */
    public abstract static class RecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

        public int getHeaderCount() {
            return 0;
        }

        public int getFooterCount() {
            return 0;
        }

    }

}
