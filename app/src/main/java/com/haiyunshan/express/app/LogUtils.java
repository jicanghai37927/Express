package com.haiyunshan.express.app;

import android.util.Log;

/**
 *
 */
public class LogUtils {

    public static final void w(Object obj) {
        Log.w("Express", obj.toString());
    }

    public static final void w(String tag, Object obj) {
        if (obj == null) {
            Log.w(tag, "object is null");
        } else {
            Log.w(tag, obj.toString());
        }
    }
}
