package com.haiyunshan.express.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.haiyunshan.express.App;
import com.haiyunshan.express.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_MM;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Created by sanshibro on 2017/8/4.
 */

public final class Utils {

    /**
     * 判断是否中文
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        boolean v = (c >= 0x4e00 && c <= 0x9fa5);
        return v;
    }

    /**
     *
     * @param c
     * @return
     */
    public static boolean isAlphabet(char c) {
        boolean v = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        return v;
    }

    /**
     *
     * @param c
     * @return
     */
    public static boolean isDigit(char c) {
        boolean v = (c >= '0' && c <= '9');
        return v;
    }

    /**
     *
     * @param text
     * @return
     */
    public static String getChineseString(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }

        StringBuilder sb = new StringBuilder(text.length());

        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (isChinese(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * @param t1
     * @param t2
     * @return
     */
    public static boolean isSameDay(long t1, long t2) {
        int aDay = (1000 * 60 * 60 * 24);

        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTimeInMillis(t1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTimeInMillis(t2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int gap = ((int) ((now.getTime().getTime() - c.getTime().getTime()) / aDay));
        if (gap == 0) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {

        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        String versionCode = "0";

        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {

        }

        return versionCode;
    }

    /**
     *
     * @return
     */
    public static final String uuid() {
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");

        return id;
    }

    /**
     *
     * @param context
     * @param time
     * @return
     */
    public static final String getDay(Context context, long time) {
        String text = null;

        int gap = getGapCount(time, System.currentTimeMillis());
        if (gap < 0 || gap >= 7) {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(time);

            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);

            text = year + "/" + month + "/" + day;

        } else if (gap == 0) {
            text = context.getString(R.string.day_today);
        } else if (gap == 1) {
            text = context.getString(R.string.day_yesterday);
        } else {

            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(time);

            String[] array = context.getResources().getStringArray(R.array.day_of_week);
            int day = now.get(Calendar.DAY_OF_WEEK);
            day -= 1;

            text = array[day];
        }

        return text;
    }
    /**
     *
     * 获取两个日期之间的间隔天数
     * @return
     */
    public static int getGapCount(long startDate, long endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTimeInMillis(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTimeInMillis(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     *
     * @param context
     * @return
     */
    public static int getDisplayWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /**
     *
     * @return
     */
    public static float getScale() {
        return 0.8f;
    }

    public static int getTextSize() {
        Context context = App.instance();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float value = TypedValue.applyDimension(COMPLEX_UNIT_MM, 15, dm);

        return (int)value;
    }

    public static final int dip2px(Context context, int dip) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int value = (int)TypedValue.applyDimension(COMPLEX_UNIT_DIP, dip, dm);
        return value;
    }

    public static final int sp2px(Context context, int dip) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int value = (int)TypedValue.applyDimension(COMPLEX_UNIT_SP, dip, dm);
        return value;
    }

    public static final File savePicture(Context context, String fileName, Bitmap bitmap) {

        String name = context.getString(R.string.app_name);
        File mScreenshotDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), name);
        mScreenshotDir.mkdirs();

        long mImageTime = System.currentTimeMillis();
        String mImageFileName = fileName;

        File mImageFilePath = new File(mScreenshotDir, mImageFileName);
        mImageFilePath.delete();

        // 删除系统图库数据
        deleteMediaStore(context, mImageFilePath.getAbsolutePath());

        FileOutputStream fos = null;
        try {
            mImageFilePath.createNewFile();
            fos = new FileOutputStream(mImageFilePath);
            Bitmap.CompressFormat fmt = Bitmap.CompressFormat.PNG;
            if (!fileName.endsWith("png")) {
                fmt = Bitmap.CompressFormat.JPEG;
            }

            bitmap.compress(fmt, 100, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 更新系统图库
        updateMediaStore(context,
                mImageFilePath.getAbsolutePath(),
                mImageFileName,
                mImageTime,
                bitmap.getWidth(), bitmap.getHeight());

        return mImageFilePath;
    }

    public static final void deleteMediaStore(Context context, String mImageFilePath) {

        try {

            // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // for DATE_TAKEN
            ContentResolver resolver = context.getContentResolver();

            // 删除旧文件
            int count = resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?", new String[] { mImageFilePath});
            Log.e("Media", "count = " + count);

        } catch (Exception e) {

        }

    }

    public static final void updateMediaStore(Context context,
                                              String mImageFilePath,
                                              String mImageFileName,
                                              long mImageTime,
                                              int mImageWidth,
                                              int mImageHeight) {
        try {

            // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // for DATE_TAKEN
            long dateSeconds = mImageTime / 1000;

            String mimeType = "image/png";
            if (!mImageFileName.endsWith("png")) {
                mimeType = "image/jpg";
            }

            // Save the screenshot to the MediaStore
            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            values.put(MediaStore.Images.ImageColumns.DATA, mImageFilePath);
            values.put(MediaStore.Images.ImageColumns.TITLE, mImageFileName);
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, mImageFileName);
            values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, mImageTime);
            values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
            values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType);
            values.put(MediaStore.Images.ImageColumns.WIDTH, mImageWidth);
            values.put(MediaStore.Images.ImageColumns.HEIGHT, mImageHeight);

            // 插入
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // 更新
            values.clear();
            values.put(MediaStore.Images.ImageColumns.SIZE, new File(mImageFilePath).length());
            resolver.update(uri, values, null, null);

        } catch (Exception e) {

        }
    }

}
