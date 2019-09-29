package com.haiyunshan.express;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.express.dialog.ConfirmDialog;
import com.haiyunshan.express.typeface.TypefaceDataset;
import com.haiyunshan.express.typeface.TypefaceEntry;
import com.haiyunshan.express.typeface.TypefaceManager;

import java.util.Iterator;
import java.util.List;

public class TypefacePreviewActivity extends BaseActivity implements View.OnClickListener{

    TextView mTitleView;
    View mBackBtn;
    TextView mDeleteBtn;

    TextView mPreviewView;

    public static final void startForResult(Activity context, int requestCode, String id) {
        Intent intent = new Intent(context, TypefacePreviewActivity.class);
        intent.putExtra("id", id);

        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typeface_preview);

        this.mTitleView = (TextView)findViewById(R.id.tv_title);

        this.mBackBtn = findViewById(R.id.btn_back);
        mBackBtn.setOnClickListener(this);

        this.mDeleteBtn = (TextView)findViewById(R.id.btn_delete);
        mDeleteBtn.setOnClickListener(this);

        this.mPreviewView = (TextView)(findViewById(R.id.tv_preview));

        {
            String id = getIntent().getStringExtra("id");
            if (TextUtils.isEmpty(id)) {
                mDeleteBtn.setEnabled(false);
            } else {
                TypefaceEntry entry = TypefaceManager.instance().obtain(id);
                if (entry == null || entry.getType() != TypefaceEntry.typeFile) {
                    mDeleteBtn.setEnabled(false);
                } else {
                    mTitleView.setText(entry.getAlias());

                    Typeface tf = TypefaceManager.instance().getTypeface(entry, false, false);
                    mTitleView.setTypeface(tf);
                    mPreviewView.setTypeface(tf);
                }
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.onBackPressed();
        } else if (v == mDeleteBtn) {
            this.showDeleteConfirm();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("id", getIntent().getStringExtra("id"));
        this.setResult(RESULT_OK, intent);

        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    void showDeleteConfirm() {
        Context context = this;
        CharSequence title = "删除字体可能导致一些笔记使用默认字体显示";
        CharSequence msg = "";
        CharSequence cancelBtn = "取消";
        CharSequence confirmBtn = "删除";
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    doDelete();
                }
            }
        };

        ConfirmDialog.showWarning(context, title, msg, cancelBtn, confirmBtn, listener);
    }

    void doDelete() {

        TypefaceManager mgr = TypefaceManager.instance();
        TypefaceDataset ds = mgr.getDataset();

        TypefaceEntry entry = TypefaceManager.instance().obtain(getIntent().getStringExtra("id"));

        // 删除数据文件
        mgr.removeFontFile(entry);
        ds.remove(entry);

        // 存档
        mgr.save();

        // 返回
        this.onBackPressed();
    }

}
