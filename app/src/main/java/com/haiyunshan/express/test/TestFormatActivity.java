package com.haiyunshan.express.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.haiyunshan.express.BaseActivity;
import com.haiyunshan.express.R;

public class TestFormatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNaviVisible(false);
        setContentView(R.layout.activity_test_format);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
