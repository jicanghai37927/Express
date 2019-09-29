package com.haiyunshan.express.typeface;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.haiyunshan.express.app.StorageManagerHack;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LocalTypefaceScanner {

    LocalTypefaceDataset mFontDs;

    ArrayList<File> mList;

    ScanTask mTask;
    OnTypefaceScanListener mListener;

    boolean mStackFromEnd = true;
    long mMinFileSize = 1 * 1024 * 1024;    // 最小文件大小

    public LocalTypefaceScanner(Context context) {
        this.mFontDs = new LocalTypefaceDataset();

        this.mList = new ArrayList<>();
        List<String> list = StorageManagerHack.getMountedVolume(context);

        if (!list.isEmpty()) {
            int i = (mStackFromEnd) ? (list.size() - 1) : 0;
            while (true) {
                String path = list.get(i);
                File file = new File(path);
                mList.add(file);

                if (mStackFromEnd) {
                    --i;
                    if (i < 0) {
                        break;
                    }
                } else {
                    ++i;
                    if (i >= list.size()) {
                        break;
                    }
                }
            }
        }

    }

    public void setOnTypefaceScanListener(OnTypefaceScanListener listener) {
        this.mListener = listener;
    }

    public void cancel() {
        if (mTask != null && !mTask.isCancelled()) {
            mTask.cancel(false);
        }
    }

    public LocalTypefaceDataset getResult() {
        return this.mFontDs;
    }

    public void asyncScan() {
        if (mTask == null) {
            mTask = new LocalTypefaceScanner.ScanTask();

            File[] array = new File[mList.size()];
            mList.toArray(array);
            mTask.execute(array);
        }
    }

    private class ScanTask extends AsyncTask<File, LocalTypefaceEntry, Integer> {

        @Override
        protected Integer doInBackground(File[] params) {

            for (File file : params) {
                search(file);

                if (isCancelled()) {
                    break;
                }
            }

            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer o) {
            if (mListener != null) {
                mListener.onComplete(LocalTypefaceScanner.this);
            }
        }

        @Override
        protected void onProgressUpdate(LocalTypefaceEntry[] values) {
            if (mListener != null) {
                mListener.onSeek(LocalTypefaceScanner.this, values[0]);
            }
        }

        @Override
        protected void onCancelled(Integer o) {
            if (mListener != null) {
                mListener.onCancelled(LocalTypefaceScanner.this);
            }
        }

        /**
         *
         * @param file
         */
        void search(File file) {
            if (isCancelled()) {
                return;
            }

            if (file.isDirectory()) {

                File[] files = file.listFiles();
                if (files.length > 0) {
                    int i = (mStackFromEnd) ? (files.length - 1) : 0;
                    while (true) {
                        File f = files[i];
                        search(f);

                        if (mStackFromEnd) {
                            --i;
                            if (i < 0) {
                                break;
                            }
                        } else {
                            ++i;
                            if (i >= files.length) {
                                break;
                            }
                        }
                    }
                }

            } else {

                String name = file.getName();
                name = name.toLowerCase();
                if (name.endsWith(".ttf") && file.length() >= mMinFileSize) {
                    name = getTTFName(file);
                    if (!TextUtils.isEmpty(name)) {
                        String path = file.getAbsolutePath();
                        String id = getMD5(path);

                        LocalTypefaceEntry entry = mFontDs.put(id, name, file);
                        if (entry != null) {
                            publishProgress(entry);
                        }
                    }
                }

            }
        }
    }

    public static String getMD5(String str) {
        String md5 = null;

        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算md5函数
            md.update(str.getBytes("utf-8"));

            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            md5 = new BigInteger(1, md.digest()).toString(16);

        } catch (Exception e) {

        }

        return md5;
    }

    /**
     *
     * @param file
     * @return
     */
    public static String getName(File file) {
        return getTTFName(file);
    }

    /**
     *
     * @param file
     * @return
     */
    public static String getTTFName(File file) {

        String name = null;
        try {
            TTFParser parser = new TTFParser();
            parser.parse(file.getAbsolutePath());
            name = parser.getFontName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name;
    }
}
