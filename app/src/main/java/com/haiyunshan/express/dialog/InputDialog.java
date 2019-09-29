package com.haiyunshan.express.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;

import com.haiyunshan.express.R;

/**
 * 消息提示框
 *
 */
public class InputDialog extends Dialog implements Dialog.OnShowListener, View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    TextView mTitleView;
    TextView mMessageView;
    EditText mEditText;
    View mCancelBtn;
    View mSaveBtn;

    OnClickListener mListener;

    public static final InputDialog create(Context context,
                                           CharSequence title,
                                           CharSequence msg,
                                           String text,
                                           String hint,
                                           OnClickListener listener) {
        InputDialog dialog = new InputDialog(context);
        dialog.set(title, msg, text, hint, listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    InputDialog(Context context) {
        super(context, R.style.message_dialog);

        this.setContentView(R.layout.dialog_input);

        this.mTitleView = (TextView)(this.findViewById(R.id.tv_title));
        this.mMessageView = (TextView)(this.findViewById(R.id.tv_message));

        this.mEditText = (EditText)(this.findViewById(R.id.et_input));
        mEditText.addTextChangedListener(this);
        mEditText.setOnEditorActionListener(this);

        this.mCancelBtn = this.findViewById(R.id.btn_cancel);
        mCancelBtn.setOnClickListener(this);
        this.mSaveBtn = this.findViewById(R.id.btn_save);
        this.mSaveBtn.setOnClickListener(this);
        this.mSaveBtn.setEnabled(false);

        this.setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        InputMethodManager imm = (InputMethodManager) (getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.showSoftInput(this.mEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onClick(View v) {
        if (v == this.mCancelBtn) {
            if (mListener != null) {
                mListener.onClick(this, DialogInterface.BUTTON_NEGATIVE);
            }

            this.cancel();
        } else if (v == this.mSaveBtn) {
            if (mListener != null) {
                mListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
            }

            this.dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        str = str.trim();

        boolean enable = !str.isEmpty();
        this.mSaveBtn.setEnabled(enable);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean result = (actionId == EditorInfo.IME_ACTION_DONE);
        if (result) {
            if (mListener != null) {
                mListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
            }

            this.dismiss();
        }

        return result;
    }

    /**
     *
     * @param title
     * @param msg
     * @param text
     * @param listener
     */
    void set(CharSequence title,
             CharSequence msg,
             String text,
             String hint,
             OnClickListener listener) {
        this.mTitleView.setText(title);
        this.mMessageView.setText(msg);
        this.mEditText.setText(text);
        mEditText.setHint(hint);
        this.mListener = listener;

        // 设置为选中
        if (!TextUtils.isEmpty(text)) {
            mEditText.setSelection(text.length() - 1);
            mEditText.setSelection(0, text.length());
        }
    }

    /**
     *
     * @return
     */
    public String getText() {
        String text = mEditText.getText().toString();
        return text;
    }

}
