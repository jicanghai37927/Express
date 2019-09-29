package com.haiyunshan.express.compose.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haiyunshan.express.R;

/**
 *
 */
public class EntityDividerDecoration extends RecyclerView.ItemDecoration {

    Drawable mDivider;
    Rect mBounds = new Rect();

    int mMarginLeft = 0;
    int mMarginRight = 0;

    boolean mEnable = true;

    public EntityDividerDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.shape_divider, null);
    }

    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    public void setMargin(int left, int right) {
        this.mMarginLeft = left;
        this.mMarginRight = right;
    }

    public void setEnable(boolean enable) {
        if (!(mEnable ^ enable)) {
            return;
        }

        this.mEnable = enable;
    }

    public boolean isEnable() {
        return this.mEnable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }

        if (!mEnable) {
            return;
        }

        canvas.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        left += mMarginLeft;
        right -= mMarginRight;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);

            if (pos + 1 != parent.getAdapter().getItemCount()) {
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                int top = bottom - mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }

        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (!mEnable) {
            return;
        }
    }
}
