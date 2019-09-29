package com.haiyunshan.express.app;

import android.content.Context;
import android.widget.Toast;

/**
 *
 */
public final class ToastUtils {

	/**
	 * 
	 * @param context
	 * @param text
	 */
	public static final void show(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show(); 
	}
	
	/**
	 * @param context
	 * @param resId
	 */
	public static final void show(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show(); 
	}
}
