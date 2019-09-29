package com.haiyunshan.express.compose.action;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.haiyunshan.express.R;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.widget.OnDoubleClickListener;

/**
 *
 * @param <VH>
 */
public class SegmentAction<VH extends SegmentHolder> implements View.OnClickListener, View.OnTouchListener, View.OnKeyListener, View.OnLongClickListener, OnDoubleClickListener {

    @Override
    public final void onClick(View v) {
        VH holder = this.getHolder(v);

        this.onClick(holder, v);
    }

    @Override
    public final boolean onKey(View v, int keyCode, KeyEvent event) {
        VH holder = this.getHolder(v);

        return this.onKey(holder, v, keyCode, event);
    }

    @Override
    public final boolean onLongClick(View v) {
        VH holder = this.getHolder(v);

        return this.onLongClick(holder, v);
    }

    @Override
    public final boolean onTouch(View v, MotionEvent event) {
        VH holder = this.getHolder(v);

        return this.onTouch(holder, v, event);
    }

    @Override
    public final void onDoubleClick(View v) {
        VH holder = this.getHolder(v);

        this.onDoubleClick(holder, v);
    }

    protected void onClick(VH holder, View v) {};

    protected boolean onKey(VH holder, View v, int keyCode, KeyEvent event) { return false; };

    protected boolean onLongClick(VH holder, View v) { return false; };

    protected boolean onTouch(VH holder, View v, MotionEvent event) { return false; };

    protected void onDoubleClick(VH holder, View v) {};

    VH getHolder(View v) {
        Object obj = v.getTag(R.id.tag_segment);

        VH holder = (VH) obj;
        return holder;
    }
}
