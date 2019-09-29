package com.haiyunshan.express.note.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haiyunshan.express.note.holder.SegmentHolder;

/**
 *
 */
public class EntityItemDecoration extends RecyclerView.ItemDecoration {

    private final Rect mBounds = new Rect();

    public EntityItemDecoration(Context context) {

    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }

        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom;

            mBounds.set(left, top, right, bottom);

            RecyclerView.ViewHolder holder = parent.findContainingViewHolder(child);
            if (holder instanceof SegmentHolder) {
                SegmentHolder itemHolder = (SegmentHolder)holder;
                itemHolder.onDrawDecoration(canvas, parent, state);
            }
        }
        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }

        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom;

            mBounds.set(left, top, right, bottom);

            RecyclerView.ViewHolder holder = parent.findContainingViewHolder(child);
            if (holder instanceof SegmentHolder) {
                SegmentHolder itemHolder = (SegmentHolder)holder;
                itemHolder.onDrawDecorationOver(canvas, parent, state);
            }
        }

        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)(view.getLayoutParams());
        params.getViewAdapterPosition();

        RecyclerView.ViewHolder holder = parent.findContainingViewHolder(view);
        if (holder instanceof SegmentHolder) {
            SegmentHolder itemHolder = (SegmentHolder)holder;
            itemHolder.getItemDecorationOffsets(outRect, view, parent, state);
        }
    }
}
