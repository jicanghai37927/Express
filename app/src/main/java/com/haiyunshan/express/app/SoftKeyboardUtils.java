/**
 * 文件名	: SoftKeyboardUtils.java
 * 作者		: 陈振磊
 * 创建日期	: 2016年10月22日
 * 版权    	:  
 * 描述    	: 
 * 修改历史	: 
 */

package com.haiyunshan.express.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.haiyunshan.express.App;
import com.haiyunshan.express.R;

/**
 * 软键盘工具类
 *
 */
public class SoftKeyboardUtils {

    static final String TAG = "SoftKeyboardUtils";

    static int sDecorBottom = 0;     // 有效使用高度，扣除底部三个导航按键

    /**
     * 判断软键盘是否弹出
     *
     * @param context
     * @return
     */
    public static final boolean isVisible(Activity context) {
        return getHeightFromDecor(context) >= 160;
    }

    /**
     *
     * @param context
     */
    public static final void show(Activity context) {
        if (isVisible(context)) {
            return;
        }

        show(context, context.getCurrentFocus());
    }

    /**
     *
     * @param context
     */
    public static final void hide(Activity context) {
        if (!isVisible(context)) {
            return;
        }

        hide(context, context.getCurrentFocus());
    }

    /**
     * 显示软键盘
     * 
     * @param context
     * @param view
     */
    public static final void show(final Context context, final EditText view) {
        if (view == null) {
            return;
        }

        view.requestFocus();
        
        view.post(new Runnable() {
			@Override
			public void run() {
				InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
				manager.showSoftInput(view, 0);
			}
		});
    }

    /**
     *
     * @param context
     * @param view
     */
    public static final void show(final Context context, final View view) {
        if (view == null) {
            return;
        }

        view.requestFocus();

        view.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
                manager.showSoftInput(view, 0);
            }
        });
    }

    /**
     * 隐藏软键盘
     * 
     * @param context
     * @param view
     */
    public static final void hide(Context context, EditText view) {
        if (view == null) {
            return;
        }

        InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     *
     * @param context
     * @param view
     */
    public static final void hide(Context context, View view) {
        if (view == null) {
            return;
        }

        InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static final void markBottom(Activity context) {
        Rect r = new Rect();
        View decorView = context.getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(r);

        int bottom = r.bottom;
        sDecorBottom = bottom;

        LogUtils.w(TAG, "decorBottom = " + bottom);
    }

    public static final int getHeight(Activity context) {

        int height = SoftKeyboardUtils.getHeightFromDecor(context);

        if (height <= 0) {
            height = PrefUtils.getKeyboardHeight();

            if (height <= 0) {
                height = context.getResources().getDimensionPixelSize(R.dimen.soft_keyboard_height);
            }
        } else {
            PrefUtils.setKeyboardHeight(height);
        }


        return height;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    static final int getHeightFromDecor(Activity context) {
        Rect r = new Rect();

        // 应用当前的空间
        View decorView = context.getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(r);
        int decorBottom = r.bottom;

        // 应用可以占用的完整空间
        int fullBottom = sDecorBottom;
        if (fullBottom <= 0) {
            View rootView = decorView.getRootView();
            fullBottom = rootView.getHeight();
        }
        if (fullBottom <= 0) {
            fullBottom = WindowUtils.getDisplayHeight(context);
        }

        //计算软件盘的高度
        int softInputHeight = fullBottom - decorBottom;

//        LogUtils.w(TAG, "fullBottom = " + fullBottom + ", decorBottom = " + r.bottom + ", softInputHeight = " + softInputHeight);

        softInputHeight = (softInputHeight < 0)? 0: softInputHeight;
        return softInputHeight;
    }

    /**
     *
     */
    private static class PrefUtils {

        static final String prefName = "softkeyboard_pref";

        static final String keyKeyboardHeight = "keyboard_height";

        static void setKeyboardHeight(int value) {
            SharedPreferences prefs = getSharedPreferences();
            prefs.edit().putInt(keyKeyboardHeight, value).commit();
        }

        static int getKeyboardHeight() {
            SharedPreferences prefs = getSharedPreferences();
            int value = prefs.getInt(keyKeyboardHeight, -1);

            return value;
        }

        static SharedPreferences getSharedPreferences() {
            Context context = App.instance();

            SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            return prefs;
        }

    }
}
