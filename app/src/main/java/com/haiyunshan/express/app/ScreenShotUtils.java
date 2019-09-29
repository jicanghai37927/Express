package com.haiyunshan.express.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanshibro on 2018/2/5.
 */

public class ScreenShotUtils {

    /**
     *
     * @param view
     * @return
     */
    public static final Bitmap capture(RecyclerView view) {
        Bitmap bigBitmap = null;
        if (view == null || view.getAdapter() == null) {
            return bigBitmap;
        }

        RecyclerView.Adapter adapter = view.getAdapter();

        int size = adapter.getItemCount();
        int height = 0;
        Paint paint = new Paint();
        int iHeight = 0;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
        for (int i = 0; i < size; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);

            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                    holder.itemView.getMeasuredHeight());

            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();

            Bitmap drawingCache = holder.itemView.getDrawingCache();
            if (drawingCache != null) {
                bitmaCache.put(String.valueOf(i), drawingCache);
            }
            height += holder.itemView.getMeasuredHeight();
        }

        bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        Drawable lBackground = view.getBackground();
        if (lBackground instanceof ColorDrawable) {
            ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
            int lColor = lColorDrawable.getColor();
            bigCanvas.drawColor(lColor);
        }

        for (int i = 0; i < size; i++) {
            Bitmap bitmap = bitmaCache.get(String.valueOf(i));
            bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
            iHeight += bitmap.getHeight();
            bitmap.recycle();
        }

        return bigBitmap;
    }

    /**
     *
     * @param listview
     * @return
     */
    public static final Bitmap capture(ListView listview) {

        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;

        List<Bitmap> bmps = new ArrayList<Bitmap>();
        for (int i = 0; i < itemscount; i++) {

            View childView = adapter.getView(i, null, listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());

            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }

        Bitmap bigbitmap =
                Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();

            bmp.recycle();
        }

        return bigbitmap;
    }

    /**
     *
     * @param scrollView
     * @return
     */
    public static final Bitmap capture(ScrollView scrollView) {

        int h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            View child = scrollView.getChildAt(i);
            h += child.getHeight();
        }

        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        scrollView.draw(canvas);

        return bitmap;
    }

    /**
     *
     * @param v
     * @return
     */
    public static final Bitmap capture(View v) {

        if (null == v) {
            return null;
        }

        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();

        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY)
                    , View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));

            v.layout((int) v.getX(), (int) v.getY(),
                    (int) v.getX() + v.getMeasuredWidth(),
                    (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            v.layout(0, 0,
                    v.getMeasuredWidth(), v.getMeasuredHeight());
        }

        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(),
                0, 0,
                v.getMeasuredWidth(), v.getMeasuredHeight());

        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();

        return b;
    }

    /**
     *
     * @param context
     * @return
     */
    public static final Bitmap capture(Activity context) {

        View view = context.getWindow().getDecorView();

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bp = Bitmap.createBitmap(view.getDrawingCache(),
                0, 0,
                view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return bp;
    }
}
