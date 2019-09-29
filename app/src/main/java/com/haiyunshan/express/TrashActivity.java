package com.haiyunshan.express;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.haiyunshan.express.fragment.TrashFragment;

public class TrashActivity extends AppCompatActivity {

    public static final void start(Fragment f, int requestCode) {
        Intent intent = new Intent(f.getActivity(), TrashActivity.class);

        f.startActivityForResult(intent, requestCode);
        f.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Fragment f = new TrashFragment();
        f.setArguments(getIntent().getExtras());

        {
            FrameLayout container = (FrameLayout) (findViewById(R.id.root_container));
            FragmentManager fm = this.getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.replace(container.getId(), f);
            t.commit();
        }
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_OK);

        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
