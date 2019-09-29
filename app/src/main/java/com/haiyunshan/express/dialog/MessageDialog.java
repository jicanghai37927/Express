package com.haiyunshan.express.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.express.R;

/**
 * 消息提示框
 *
 */
public class MessageDialog {

    /**
     * @param context
     * @param title
     * @param msg
     * @param btn
     * @return
     */
    public static Dialog show(Context context,
                              CharSequence title, CharSequence msg, CharSequence btn) {
        return show(context,
                title, msg, btn,
                false,
                Gravity.CENTER,
                null);
    }

    /**
     * @param context
     * @param title
     * @param msg
     * @param btn
     * @param cancelable
     * @return
     */
    public static Dialog show(Context context,
                              CharSequence title, CharSequence msg, CharSequence btn,
                              boolean cancelable) {
        return show(context,
                title, msg, btn,
                cancelable,
                Gravity.CENTER,
                null);
    }

    /**
     * @param context
     * @param title
     * @param msg
     * @param btn
     * @param gravity
     * @return
     */
    public static Dialog show(Context context,
                              CharSequence title, CharSequence msg, CharSequence btn,
                              int gravity) {
        return show(context,
                title, msg, btn,
                false,
                gravity,
                null);
    }

    /**
     * @param context
     * @param title
     * @param msg
     * @param btn
     * @param cancelable
     * @param gravity
     * @param listener
     * @return
     */
    public static Dialog show(Context context,
                              CharSequence title, CharSequence msg, CharSequence btn,
                              boolean cancelable, int gravity,
                              final DialogInterface.OnDismissListener listener) {
        final Dialog localDialog = new Dialog(context, R.style.message_dialog);
        localDialog.setCancelable(cancelable);
        localDialog.setCanceledOnTouchOutside(cancelable);
        localDialog.setOnDismissListener(listener);

        localDialog.setContentView(R.layout.dialog_message);

        TextView dialogTitle = (TextView) localDialog.findViewById(R.id.tv_title);
        if (dialogTitle != null) {
            dialogTitle.setText(title);

            if (TextUtils.isEmpty(title)) {
                dialogTitle.setVisibility(View.GONE);
            }
        }

        TextView dialogText = (TextView) localDialog.findViewById(R.id.tv_message);
        if (dialogText != null) {
            dialogText.setGravity(gravity);
            dialogText.setText(msg);

            if (TextUtils.isEmpty(msg)) {
                dialogText.setVisibility(View.GONE);
            }
        }

        TextView dialogLeftBtn = (TextView) localDialog.findViewById(R.id.btn_ok);
        if (dialogLeftBtn != null) {
            dialogLeftBtn.setText(btn);
            dialogLeftBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    localDialog.dismiss();
                }
            });
        }

        localDialog.show();
        return localDialog;
    }
}
