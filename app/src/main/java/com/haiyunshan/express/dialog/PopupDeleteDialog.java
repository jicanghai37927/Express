/**
 * 文件名	: PopupDeleteDialog.java
 * 作者		: 陈振磊
 * 创建日期	: 2016年1月17日
 * 版权    	:  
 * 描述    	: 
 * 修改历史	: 
 */
package com.haiyunshan.express.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiyunshan.express.R;


/**
 * 删除确认对话框
 *
 */
public class PopupDeleteDialog extends Dialog implements View.OnClickListener {

	private TranslateAnimation mAnimation;

	private Context mContext;
	private Handler mHandler;

	private View mDialogView;
	private View mActionView;
	private TextView mDeleteView;
	private View mCancelView;

	boolean mBuildComplete;
	boolean mCallSuperDismiss; 
	
	private OnClickListener mOnHelpListener;

	public static PopupDeleteDialog create(Context paramContext) {
		PopupDeleteDialog localActionSheet = new PopupDeleteDialog(paramContext, false);
		localActionSheet.getWindow().setWindowAnimations(R.style.MenuDialogAnimation);
		return localActionSheet;
	}

	public static PopupDeleteDialog create(Context paramContext, boolean callSuperDismiss) {
		PopupDeleteDialog localActionSheet = new PopupDeleteDialog(paramContext, false);
		localActionSheet.mCallSuperDismiss = callSuperDismiss; 
		
		localActionSheet.getWindow().setWindowAnimations(R.style.MenuDialogAnimation);
		return localActionSheet;
	}

	public static PopupDeleteDialog createMenuSheet(Context paramContext) {
		PopupDeleteDialog localActionSheet = new PopupDeleteDialog(paramContext, true);
		localActionSheet.getWindow().setWindowAnimations(R.style.MenuDialogAnimation);
		return localActionSheet;
	}

	protected PopupDeleteDialog(Context context) {
		this(context, false);
	}

	protected PopupDeleteDialog(Context context, boolean hasBuild) {
		super(context, R.style.MenuDialogStyle);
		
		this.mContext = context;
		this.mBuildComplete = hasBuild;
		this.mCallSuperDismiss = false; 
		
		this.mHandler = new Handler(Looper.getMainLooper());

		LayoutInflater inflater = LayoutInflater.from(context);
		this.mDialogView = inflater.inflate(R.layout.dialog_delete, null);
		super.setContentView(this.mDialogView);

		this.mActionView = ((LinearLayout) this.mDialogView.findViewById(R.id.action_sheet_actionView));

		this.mDialogView.setOnClickListener(new hgh(this));

		this.mDeleteView = (TextView) mDialogView.findViewById(R.id.btn_delete);
		mDeleteView.setOnClickListener(this);

		this.mCancelView = mDialogView.findViewById(R.id.btn_cancel);
		mCancelView.setOnClickListener(this);
	}

	public void setButton(CharSequence btn, OnClickListener listener) {
		this.mDeleteView.setText(btn);

		this.mOnHelpListener = listener;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try {
			dismiss();
			return true;
		} catch (Exception localException) {
			return super.onPrepareOptionsMenu(menu);
		}
	}

	@Override
	public void dismiss() {
		this.mHandler.postDelayed(new hge(this), 0L);
	}

	@Override
	public void show() {
		super.show();

		this.mHandler.postDelayed(new hgd(this), 0L);
	}

	@Override
	public void onClick(View v) {
		
		if (v == mDeleteView) {

			if (mOnHelpListener != null) {
				mOnHelpListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
			}

			if (mCallSuperDismiss) {
				super.dismiss();
				return; 
			}
			
		} else if (v == mCancelView) {

		}
		
		dismiss();
	}

	public void dismissSuper() {
		try {
			super.dismiss();
		} catch (Exception localException) {
		}
	}

	public class hgd implements Runnable {
		public hgd(Dialog dialog) {
		}

		public void run() {
			mAnimation = new TranslateAnimation(0.0F, 0.0F, mActionView.getHeight(), 0.0F);
			mAnimation.setFillEnabled(true);
			mAnimation
					.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.decelerate_interpolator));
			mAnimation.setDuration(300);
			mActionView.startAnimation(mAnimation);
		}
	}

	public class hge implements Runnable {
		public hge(PopupDeleteDialog dialog) {
		}

		public void run() {
			mAnimation = new TranslateAnimation(0.0F, 0.0F, 0.0F, mActionView.getHeight());

			mAnimation.setFillAfter(true);
			mAnimation
					.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.decelerate_interpolator));
			mAnimation.setDuration(200);
			mActionView.startAnimation(mAnimation);
			mAnimation.setAnimationListener(new hgf(this));

		}
	}

	class hgf implements Animation.AnimationListener {
		hgf(hge paramhge) {
		}

		public void onAnimationEnd(Animation paramAnimation) {
			try {
				dismissSuper();
				return;
			} catch (Exception localException) {
			}
		}

		public void onAnimationRepeat(Animation paramAnimation) {
		}

		public void onAnimationStart(Animation paramAnimation) {
		}
	}

	public class hgh implements View.OnClickListener {
		public hgh(PopupDeleteDialog dialog) {
		}

		public void onClick(View paramView) {
			PopupDeleteDialog.this.dismiss();
		}
	}

}
