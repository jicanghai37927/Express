package com.haiyunshan.express;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class PreferenceActivity extends BaseActivity implements View.OnClickListener {

    TextView mVersionBtn;

    View mDoneView;

    public static final void start(Activity context) {
        Intent intent = new Intent(context, PreferenceActivity.class);

        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_in_up, R.anim.standby);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        onViewCreated(findViewById(R.id.root_container), savedInstanceState);
        onActivityCreated(savedInstanceState);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        this.mVersionBtn = (TextView)(view.findViewById(R.id.btn_version));

        this.mDoneView = view.findViewById(R.id.btn_done);
        mDoneView.setOnClickListener(this);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mVersionBtn.setText(this.getVersion(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.overridePendingTransition(R.anim.standby, R.anim.push_out_down);
    }

    @Override
    public void onClick(View v) {
        if (v == mDoneView) {
            this.onBackPressed();
        }
    }

    public static CharSequence getVersion(Context context) {

        String name = context.getString(R.string.app_name);
        String versionName = null;

        try {
            String pkName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(pkName, 0);
            versionName = info.versionName;

            int pos = versionName.indexOf('_');
            if (pos > 0) {
                versionName = versionName.substring(0, pos);
            }

            // 调试模式显示准确的versionCode
//            if (PrefUtils.isDebug()) {
//                versionName = versionName + "_" + info.versionCode + "_debug";
//            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (TextUtils.isEmpty(versionName)) {
            return name;
        }

        return context.getString(R.string.pref_version_fmt, name, versionName);

    }
}
