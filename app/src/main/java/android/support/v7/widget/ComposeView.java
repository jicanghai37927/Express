package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 *
 *
 */
public class ComposeView extends RecyclerView {

    static final String TAG = "ComposeView";

    public ComposeView(Context context) {
        this(context, null);
    }

    public ComposeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComposeView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        return super.focusSearch(focused, direction);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
//        Log.w(TAG, "requestChildFocus = " + child);

        super.requestChildFocus(child, focused);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        Log.w(TAG, "requestChildRectangleOnScreen = " + child + ", rect = " + rect.toShortString());

        return super.requestChildRectangleOnScreen(child, rect, immediate);
    }
}
