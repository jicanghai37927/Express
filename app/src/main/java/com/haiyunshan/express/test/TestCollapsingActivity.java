package com.haiyunshan.express.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.haiyunshan.express.BaseActivity;
import com.haiyunshan.express.R;
import com.haiyunshan.express.fragment.BaseFragment;

public class TestCollapsingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setNaviVisible(true);

        setContentView(R.layout.activity_test_collapsing);
    }
}
