package com.haiyunshan.express;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haiyunshan.express.typeface.TTFParser;
import com.haiyunshan.express.typeface.TypefaceEntry;
import com.haiyunshan.express.typeface.TypefaceManager;

import java.io.IOException;

public class LocalTypefacePreviewActivity extends BaseActivity implements View.OnClickListener{

    TextView mTitleView;
    View mBackBtn;
    TextView mAddBtn;

    TextView mPreviewView;

    public static final void startForResult(Activity context, int requestCode, String path) {
        Intent intent = new Intent(context, LocalTypefacePreviewActivity.class);
        intent.putExtra("path", path);

        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_typeface_preview);

        this.mTitleView = (TextView)findViewById(R.id.tv_title);

        this.mBackBtn = findViewById(R.id.btn_back);
        mBackBtn.setOnClickListener(this);

        this.mAddBtn = (TextView)findViewById(R.id.btn_add);
        mAddBtn.setOnClickListener(this);

        this.mPreviewView = (TextView)(findViewById(R.id.tv_preview));

        {
            String path = getIntent().getStringExtra("path");
            if (TextUtils.isEmpty(path)) {
                mAddBtn.setEnabled(false);
            } else {
                String name = null;
                TTFParser parser = new TTFParser();
                try {
                    parser.parse(path);
                    name = parser.getFontName();
                } catch (IOException e) {

                }

                if (TextUtils.isEmpty(name)) {
                    mAddBtn.setEnabled(false);
                } else {
                    mTitleView.setText(name);

                    Typeface tf = Typeface.createFromFile(path);
                    mTitleView.setTypeface(tf);
                    mPreviewView.setTypeface(tf);
                }
            }

            if (mAddBtn.isEnabled()) {
                TypefaceEntry entry = TypefaceManager.instance().getDataset().obtainBySource(path);
                if (entry != null) {
                    mAddBtn.setEnabled(false);
                    mAddBtn.setText("(已添加)");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.onBackPressed();
        } else if (v == mAddBtn) {
            mAddBtn.setEnabled(false);
            mAddBtn.setText("(已添加)");

            TypefaceManager.instance().add(mTitleView.getText().toString(), getIntent().getStringExtra("path"));

            this.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("path", getIntent().getStringExtra("path"));
        this.setResult(RESULT_OK, intent);

        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
