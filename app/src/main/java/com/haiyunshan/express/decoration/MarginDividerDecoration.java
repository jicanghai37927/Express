package com.haiyunshan.express.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 */
public class MarginDividerDecoration extends RecyclerView.ItemDecoration {

    int mMarginHeader = 100;
    int mMarginFooter = 100;

    boolean mEnable = true;

    public MarginDividerDecoration(Context context) {

    }

    public void setMargin(int header, int footer) {
        this.mMarginHeader = header;
        this.mMarginFooter = footer;
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
        super.onDrawOver(canvas, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (!mEnable) {
            return;
        }

        int pos = parent.getChildAdapterPosition(view);
        if (pos == 0) {
            outRect.top = this.mMarginHeader;
        }

        if (pos == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = this.mMarginFooter;
        }
    }
}
