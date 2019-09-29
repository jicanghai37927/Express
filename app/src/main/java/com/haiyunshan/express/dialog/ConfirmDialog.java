/**
 * 文件名	: WarningConfirmDialog.java
 * 作者		: 陈振磊
 * 创建日期	: 2015年9月26日
 * 版权    	:  
 * 描述    	: 
 * 修改历史	: 
 */

package com.haiyunshan.express.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.haiyunshan.express.R;

/**
 * 消息提醒对话框
 * 
 */
public class ConfirmDialog {

	
	/**
	 * @param context
	 * @param title
	 * @param msg
	 * @param btn1
	 * @param btn2
	 * @param btnCancel
	 * @param listener
	 * @return
	 */
	public static Dialog showChoice(Context context, 
			CharSequence title, CharSequence msg, 
			CharSequence btn1, CharSequence btn2, CharSequence btnCancel, 
			final DialogInterface.OnClickListener listener) {
		
		final Dialog localDialog = new Dialog(context, R.style.message_dialog);
		localDialog.setCancelable(false);
		localDialog.setCanceledOnTouchOutside(false);
		
		localDialog.setContentView(R.layout.dialog_tri_choice);
		
		TextView dialogTitle = (TextView) localDialog.findViewById(R.id.tv_title);
		if (dialogTitle != null) {
			dialogTitle.setText(title);
		}
		if (TextUtils.isEmpty(title)) {
			dialogTitle.setVisibility(View.GONE);
		}
		
		TextView dialogText = (TextView) localDialog.findViewById(R.id.tv_msg);
		if (dialogText != null) {
			dialogText.setText(msg);
		}
		
		{
			TextView dialogBtn = (TextView) localDialog.findViewById(R.id.tv_confirm);
			if (dialogBtn != null) {
				dialogBtn.setText(btn1);
				dialogBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						localDialog.dismiss();

						if (listener != null) {
							listener.onClick(localDialog, DialogInterface.BUTTON_POSITIVE);
						}
					}
				});
			}
		}

		{
			TextView dialogBtn = (TextView) localDialog.findViewById(R.id.tv_middle);
			if (dialogBtn != null) {
				dialogBtn.setText(btn2);
				dialogBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						localDialog.dismiss();

						if (listener != null) {
							listener.onClick(localDialog, DialogInterface.BUTTON_NEUTRAL);
						}
					}
				});
			}
		}
		
		{
			TextView dialogBtnBtn = (TextView) localDialog.findViewById(R.id.tv_cancel);
			if (dialogBtnBtn != null) {
				dialogBtnBtn.setText(btnCancel);
				dialogBtnBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						localDialog.dismiss();
						
						if (listener != null) {
							listener.onClick(localDialog, DialogInterface.BUTTON_NEGATIVE);
						}
					}
				});
			}
		}
		
		localDialog.show();
		return localDialog; 
	}
	
	/**
	 * 显示为普通对话框
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param cancelBtn
	 * @param confirmBtn
	 * @param listener
	 * @return
	 */
	public static Dialog showNormal(Context context, 
			CharSequence title, CharSequence msg, 
			CharSequence cancelBtn, CharSequence confirmBtn, boolean confirmBold, 
			final DialogInterface.OnClickListener listener) {
		
		final Dialog localDialog = new Dialog(context, R.style.message_dialog);
		localDialog.setCancelable(false);
		localDialog.setCanceledOnTouchOutside(false);
		
		int resId = confirmBold? R.layout.dialog_normal_confirm_bold: R.layout.dialog_normal_confirm; 
		localDialog.setContentView(resId);
		
		TextView dialogTitle = (TextView) localDialog.findViewById(R.id.tv_title);
		if (dialogTitle != null) {
			dialogTitle.setText(title);
		}
		if (TextUtils.isEmpty(title)) {
			dialogTitle.setVisibility(View.GONE);
		}
		
		TextView dialogText = (TextView) localDialog.findViewById(R.id.tv_msg);
		if (dialogText != null) {
			dialogText.setText(msg);
		}
		if (TextUtils.isEmpty(msg)) {
			dialogText.setVisibility(View.GONE);
		}
		
		{
			TextView dialogBtn = (TextView) localDialog.findViewById(R.id.tv_cancel);
			if (dialogBtn != null) {
				dialogBtn.setText(cancelBtn);
				dialogBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						localDialog.dismiss();

						if (listener != null) {
							listener.onClick(localDialog, DialogInterface.BUTTON_NEGATIVE);
						}
					}
				});
			}
		}
		
		{
			TextView dialogBtnBtn = (TextView) localDialog.findViewById(R.id.tv_confirm);
			if (dialogBtnBtn != null) {
				dialogBtnBtn.setText(confirmBtn);
				dialogBtnBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						localDialog.dismiss();
						
						if (listener != null) {
							listener.onClick(localDialog, DialogInterface.BUTTON_POSITIVE);
						}
					}
				});
			}
		}
		
		localDialog.show();
		return localDialog; 
	}
	
	/**
	 * 显示为警告对话框（红色字）
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @return
	 */
	public static Dialog showWarning(Context context, 
			CharSequence title, CharSequence msg, 
			CharSequence cancelBtn, CharSequence confirmBtn,
			final DialogInterface.OnClickListener listener) {
		
		final Dialog localDialog = new Dialog(context, R.style.message_dialog);
		localDialog.setCancelable(false);
		localDialog.setCanceledOnTouchOutside(false);
		
		localDialog.setContentView(R.layout.dialog_warning_confirm);
		
		TextView dialogTitle = (TextView) localDialog.findViewById(R.id.tv_title);
		if (dialogTitle != null) {
			dialogTitle.setText(title);
		}
		if (TextUtils.isEmpty(title)) {
			dialogTitle.setVisibility(View.GONE);
		}
		
		TextView dialogText = (TextView) localDialog.findViewById(R.id.tv_msg);
		if (dialogText != null) {
			dialogText.setText(msg);
		}
        if (TextUtils.isEmpty(msg)) {
            dialogText.setVisibility(View.GONE);
        }
		
		{
			TextView dialogBtn = (TextView) localDialog.findViewById(R.id.tv_cancel);
			if (dialogBtn != null) {
				dialogBtn.setText(cancelBtn);
				dialogBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						localDialog.dismiss();

						if (listener != null) {
							listener.onClick(localDialog, DialogInterface.BUTTON_NEGATIVE);
						}
					}
				});
			}
		}
		
		{
			TextView dialogBtnBtn = (TextView) localDialog.findViewById(R.id.tv_confirm);
			if (dialogBtnBtn != null) {
				dialogBtnBtn.setText(confirmBtn);
				dialogBtnBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						localDialog.dismiss();
						
						if (listener != null) {
							listener.onClick(localDialog, DialogInterface.BUTTON_POSITIVE);
						}
					}
				});
			}
		}
		
		localDialog.show();
		return localDialog; 
	}
}
