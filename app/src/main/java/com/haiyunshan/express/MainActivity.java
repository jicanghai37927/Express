package com.haiyunshan.express;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.haiyunshan.express.fragment.CatalogFragment;
import com.haiyunshan.express.widget.MyFragmentTabHost;
import com.haiyunshan.express.fragment.BaseFragment;
import com.haiyunshan.express.fragment.RecentFragment;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class MainActivity extends BaseActivity {

    MyFragmentTabHost mTabHost;
    TabWidget mTabWidget;

    public static final void start(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        {
            View view = this.getWindow().getDecorView();
            int newVis = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_STABLE;
            view.setSystemUiVisibility(newVis);
        }

        this.mTabWidget = (TabWidget) (findViewById(android.R.id.tabs));

        mTabHost = (MyFragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);

        mTabHost.addTab(newTabSpec("recent", "最近使用", R.drawable.selector_tab_recent),
                RecentFragment.class, null);

        mTabHost.addTab(newTabSpec("express", "浏览", R.drawable.selector_tab_express),
                CatalogFragment.class, null);

        {
            String tag = null;
            if (savedInstanceState != null) {
                tag = savedInstanceState.getString("tab", "express");
            }

            if (TextUtils.isEmpty(tag)) {
                tag = "express";
            }

            mTabHost.setCurrentTabByTag(tag);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    @Override
    public void onBackPressed() {

        String tag = mTabHost.getCurrentTabTag();
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag(tag);
        if (f != null && f instanceof BaseFragment) {
            boolean result = ((BaseFragment)f).onBackPressed();
            if (result) {

                if (!isTabVisible()) {
                    this.setTabVisible(true);
                }

                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public void setTabVisible(boolean visible) {
        mTabWidget.setVisibility(visible? View.VISIBLE: View.GONE);
    }

    @Override
    public boolean isTabVisible() {
        return (mTabWidget.getVisibility() == View.VISIBLE);
    }

    TabHost.TabSpec newTabSpec(String tag, CharSequence name, int iconResId) {
        TabHost.TabSpec tab = mTabHost.newTabSpec(tag);

        {
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_main_tab_widget, this.mTabWidget, false);

            ImageView iconView = (ImageView) (view.findViewById(R.id.iv_icon));
            TextView nameView = (TextView) (view.findViewById(R.id.tv_name));

            iconView.setImageResource(iconResId);
            nameView.setText(name);

            tab.setIndicator(view);
        }

        return tab;
    }

}
