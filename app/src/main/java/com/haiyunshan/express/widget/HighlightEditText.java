package com.haiyunshan.express.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.EditText;

import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.style.highlight.HighlightPainter;

/**
 * Created by sanshibro on 2018/3/8.
 */

public class HighlightEditText extends AppCompatEditText {

    static final String TAG = "HighlightEditText";

    HighlightPainter mHighlightPainter;

    public HighlightEditText(Context context) {
        this(context, null);
    }

    public HighlightEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public HighlightEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);

        // 弹出垂直菜单，且不可选择，不是要的效果。

        if (false) {
//        menu.clear();
            int size = menu.size();

            LogUtils.w(TAG, "onCreateContextMenu = " + size);
            for (int i = 0; i < size; i++) {
                MenuItem item = menu.getItem(i);
                LogUtils.w(TAG, item.getTitle());
            }

            menu.add("你好");
            menu.add("马祖");
            menu.add("道一");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getText() instanceof Spanned) {

            if (mHighlightPainter == null) {
                mHighlightPainter = new HighlightPainter(this);
            }

            mHighlightPainter.draw(canvas);
        }

        super.onDraw(canvas);
    }
}
