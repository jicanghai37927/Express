package com.haiyunshan.express.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.View;

import com.haiyunshan.express.dataset.DataSource;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by sanshibro on 2018/2/26.
 */

public class ScreenShot {

    static final String TAG = "ScreenShot";

    Activity mContext;
    View mView;
    String mName;

    Bitmap mBitmap;
    Mode mMode;

    public ScreenShot(Activity context, View view, String mName) {
        this.mContext = context;
        this.mView = view;
        this.mName = mName;
    }

    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

    }

    public File saveTemp() {
        Bitmap bitmap = this.mBitmap;
        String name = this.getName();

        File file = DataSource.getShareCacheDir();
        file = new File(file, name);

        FileOutputStream fos;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            Bitmap.CompressFormat fmt = Bitmap.CompressFormat.PNG;
            if (!name.endsWith("png")) {
                fmt = Bitmap.CompressFormat.JPEG;
            }

            bitmap.compress(fmt, 100, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();

            file = null;
        }

        return file;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public Mode getMode() {
        return this.mMode;
    }

    public String getName() {
        String name;
        String id = this.mName;
        if (this.getMode().mConfig == Bitmap.Config.ARGB_8888
                || this.getMode().mConfig == Bitmap.Config.ARGB_4444) {
            name = id + ".png";
        } else {
            name = id + ".jpg";
        }

        return name;
    }

    public void capture() {
        Bitmap target = null;

        View view = this.mView;
        if (view == null) {
            this.mBitmap = target;
            return;
        }

        // 计算内容高度
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight(); // 内容高度

        // 选择模式
        Mode mode = this.chooseMode(width, height);
        LogUtils.w(TAG, mode);

        // 创建Bitmap
        target = Bitmap.createBitmap(mode.mWidth, mode.mHeight, mode.mConfig);
        Canvas canvas = new Canvas(target);
        canvas.scale(mode.mFactor, mode.mFactor);

        if (true) {
            View itemView = view;

            itemView.draw(canvas);
        }

        // 绘制圆角
        if (mode.mConfig == Bitmap.Config.ARGB_8888
                || mode.mConfig == Bitmap.Config.ARGB_4444) {
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
        LogUtils.w(TAG, "input width, height = " + width + ", " + height);

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
                mode = new Mode(w, h, Bitmap.Config.ARGB_4444, true);
                break;
            }

            // 判断是否可以继续
            if (w % 3 != 0) {
                h = (int)(remain / 2 / w); // 计算出最大高度
                h = h / 2 * 2; // 喜欢偶数

                mode = new Mode(w, h, Bitmap.Config.ARGB_4444, false);
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
