package com.haiyunshan.express.compose.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.widget.DocumentView;

import java.util.ArrayList;

/**
 *
 */
public class DocumentShot {

    static final String TAG = "DocumentShot";

    Activity mContext;
    NoteComposeFragment mParent;

    Bitmap mBitmap;
    Mode mMode;

    public DocumentShot(NoteComposeFragment parent) {
        this.mParent = parent;
        this.mContext = parent.getActivity();
    }

    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public Mode getMode() {
        return this.mMode;
    }

    public String getName() {
        String name;
        String id = mParent.getDocument().getId();
        if (this.getMode().mConfig == Bitmap.Config.ARGB_8888) {
            name = id + ".png";
        } else {
            name = id + ".jpg";
        }

        return name;
    }

    public void capture() {
        Bitmap target = null;

        DocumentView view = mParent.mRecyclerView;
        if (view == null || view.getAdapter() == null) {
            this.mBitmap = target;
            return;
        }

        RecyclerView.Adapter adapter = view.getAdapter();
        int count = adapter.getItemCount();
        int[] array = new int[count]; // 每个item高度
        ArrayList<View> list = new ArrayList<>(count);

        // 计算内容高度
        int width = view.getMeasuredWidth();
        int height = 0; // 内容高度

        for (int i = 0; i < count; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));

            View itemView = holder.itemView;
            itemView.setWillNotCacheDrawing(true);
            itemView.setDrawingCacheEnabled(false);

            adapter.onBindViewHolder(holder, i);

            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            itemView.layout(0, 0,
                    itemView.getMeasuredWidth(), itemView.getMeasuredHeight());

            // 计算高度
            array[i] = itemView.getMeasuredHeight();
            height += array[i];

            // 缓存View
            list.add(itemView);
        }

        // 至少是屏幕高度
        int displayHeight = WindowUtils.getRealHeight(mContext);
        height = (height < displayHeight)? displayHeight: height;

        // 选择模式
        Mode mode = this.chooseMode(width, height);
        LogUtils.w(TAG, mode);

        // 创建Bitmap
        target = Bitmap.createBitmap(mode.mWidth, mode.mHeight, mode.mConfig);
        Canvas canvas = new Canvas(target);
        canvas.scale(mode.mFactor, mode.mFactor);

        // 背景
        Drawable d = view.getFixBackground();
        if (d == null) {
            d = view.getBackground();
        }
        if (d != null) {
            d.setBounds(0, 0, width, height);
            d.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        // 绘制Child
        if (false)
        {
            for (int i = 0; i < count - 1; i++) {
                canvas.translate(0, array[i]);
            }

            for (int i = count - 1; i >= 0; i--) {
                View itemView = list.get(i);

                LogUtils.w(TAG, "index = " + i + ", measuredHeight = " + itemView.getMeasuredHeight());

                // 绘图
                itemView.draw(canvas);

                if (i > 0) {
                    canvas.translate(0, -array[i - 1]);
                }
            }
        }

        if (true)
        {
            for (int i = 0; i < count; i++) {
                View itemView = list.get(i);

                // 绘图
                itemView.draw(canvas);

                // 移动
                int dy = itemView.getMeasuredHeight();
                canvas.translate(0, dy);
            }

            // 回到原点
            for (int i = 0; i < count; i++) {
                canvas.translate(0, -array[i]);
            }
        }

        // 绘制圆角
        if (mode.mConfig == Bitmap.Config.ARGB_8888) {
            float scale = 1 / mode.mFactor;
            canvas.scale(scale, scale);

            this.rounded(canvas, 1.f / 30);
        }

        this.mBitmap = target;
        this.mMode = mode;
    }

    void rounded(Canvas canvas, float factor) {

        int width = canvas.getWidth();
        int radius = (int)(width * factor);
        radius = radius / 2 * 2;

        int color = Color.BLACK;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        Bitmap corner = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(corner);

        // 左上
        {
            corner.eraseColor(Color.TRANSPARENT);
            c.drawColor(color);
            c.drawCircle(radius, radius, radius, paint);

            canvas.drawBitmap(corner, 0, 0, paint);
        }

        // 右上
        {
            corner.eraseColor(Color.TRANSPARENT);
            c.drawColor(color);
            c.drawCircle(0, radius, radius, paint);

            canvas.drawBitmap(corner, canvas.getWidth() - radius, 0, paint);
        }

        // 左下
        {
            corner.eraseColor(Color.TRANSPARENT);
            c.drawColor(color);
            c.drawCircle(radius, 0, radius, paint);

            canvas.drawBitmap(corner, 0, canvas.getHeight() - radius, paint);
        }

        // 右下
        {
            corner.eraseColor(Color.TRANSPARENT);
            c.drawColor(color);
            c.drawCircle(0, 0, radius, paint);

            canvas.drawBitmap(corner, canvas.getWidth() - radius, canvas.getHeight() - radius, paint);
        }

        // 释放
        corner.recycle();
    }

    Mode chooseMode(int width, int height) {
        Mode mode = null;

        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long remain = max - total; // 剩余可用内存
        remain = (long)(0.5f * remain);

        int w = width;
        int h = height;
        while (true) {

            // 尝试4个字节
            long memory = 4 * w * h;
            if (memory <= remain) {
                mode = new Mode(w, h, Bitmap.Config.ARGB_8888, true);
                break;
            }

            // 尝试2个字节
            memory = 2 * w * h;
            if (memory <= remain) {
                mode = new Mode(w, h, Bitmap.Config.RGB_565, true);
                break;
            }

            // 判断是否可以继续
            if (w % 3 != 0) {
                h = (int)(remain / 2 / w); // 计算出最大高度
                h = h / 2 * 2; // 喜欢偶数

                mode = new Mode(w, h, Bitmap.Config.RGB_565, false);
                break;
            }

            // 缩减到原来的2/3
            w = w * 2 / 3;
            h = h * 2 / 3;
        }

        mode.mOriginWidth = width;
        mode.mOriginHeight = height;
        mode.mFactor = 1.f;
        if (w != width) {
            mode.mFactor = 1.f * w / width;
        }

        return mode;
    }

    /**
     *
     */
    private static class Mode {

        int mWidth;
        int mHeight;
        Bitmap.Config mConfig;

        float mFactor;
        boolean mComplete;

        int mOriginWidth;
        int mOriginHeight;

        Mode(int width, int height, Bitmap.Config config, boolean complete) {
            this.mWidth = width;
            this.mHeight = height;
            this.mConfig = config;

            this.mComplete = complete;
        }

        @Override
        public String toString() {
            String text = "Config = " + mConfig + ", Factor = " + mFactor;
            text += ", width = " + mWidth + ", height = " + mHeight;
            text += ", complete = " + mComplete;
            text += ", oWidth = " + mOriginWidth + ", oHeight = " + mOriginHeight;

            return text;
        }
    }
}
