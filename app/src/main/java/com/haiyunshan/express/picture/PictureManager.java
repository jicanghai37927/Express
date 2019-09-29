package com.haiyunshan.express.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.dataset.DataManager;
import com.haiyunshan.express.dataset.DataSource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;

/**
 * 图片管理器
 */
public class PictureManager {

    public static final PictureManager instance() {
        return DataManager.instance().getPictureManager();
    }

    public PictureManager() {

    }

    public Bitmap getBitmap(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        File file = DataSource.getPicture(uri);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return bitmap;
    }

    public void removeBitmap(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }

        File file = DataSource.getPicture(uri);
        file.delete();
    }

    public String replaceBitmap(Context context, Uri fromUri, String toUri) {
        if (TextUtils.isEmpty(toUri)) {
            toUri = UUIDUtils.next();
        }

        File file = DataSource.getPicture(toUri);
        try {

            file.delete();
            file.createNewFile();

            InputStream is = context.getContentResolver().openInputStream(fromUri);
            FileUtils.copyInputStreamToFile(is, file);

            is.close();
        } catch (Exception e) {

        }

        return toUri;
    }

}
