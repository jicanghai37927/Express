/**
 * 文件名	: ChoiceDialog.java
 * 作者		: 陈振磊
 * 创建日期	: 2016年2月17日
 * 版权    	:  
 * 描述    	: 
 * 修改历史	: 
 */

package com.haiyunshan.express.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.haiyunshan.express.R;

/**
 *
 */
public class ChoiceDialog {

	/**
	 * 显示为2个选项
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param btn1
	 * @param btnCancel
	 * @param listener
	 * @return
	 */
	public static final Dialog showTwoChoice(Context context, 
			CharSequence title, CharSequence msg, 
			CharSequence btn1, CharSequence btnCancel, 
			final DialogInterface.OnClickListener listener) {

		final Dialog localDialog = new Dialog(context, R.style.message_dialog);
		localDialog.setCancelable(true);
		localDialog.setCanceledOnTouchOutside(true);
		
		localDialog.setContentView(R.layout.dialog_two_choice);
		
		TextView dialogTitle = (TextView) localDialog.findViewById(R.id.tv_title);
		if (dialogTitle != null) {
			dialogTitle.setText(title);
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
	 * 显示为3个选项
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param btn1
	 * @param btn2
	 * @param btnCancel
	 * @param listener
	 * @return
	 */
	public static Dialog showThreeChoice(Context context, 
			CharSequence title, CharSequence msg, 
			CharSequence btn1, CharSequence btn2, CharSequence btnCancel, 
			final DialogInterface.OnClickListener listener) {
		
		final Dialog localDialog = new Dialog(context, R.style.message_dialog);
		localDialog.setCancelable(true);
		localDialog.setCanceledOnTouchOutside(true);
		
		localDialog.setContentView(R.layout.dialog_tri_choice);
		
		TextView dialogTitle = (TextView) localDialog.findViewById(R.id.tv_title);
		if (dialogTitle != null) {
			dialogTitle.setText(title);
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
	
}
