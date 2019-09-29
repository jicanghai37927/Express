package com.haiyunshan.express.fragment;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.haiyunshan.express.BaseActivity;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE;

/**
 *
 */
public class BaseFragment extends Fragment {

    public boolean onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return true;
        }

        return false;
    }

    public void setNaviVisible(boolean visible) {
        View view = getActivity().getWindow().getDecorView();

        int newVis = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_STABLE;

        if (!visible) {
            newVis |= SYSTEM_UI_FLAG_LOW_PROFILE | SYSTEM_UI_FLAG_FULLSCREEN;
        }

        view.setSystemUiVisibility(newVis);
    }

    public void setTabVisible(boolean value) {
        Fragment parent = this;
        while (parent.getParentFragment() != null) {
            parent = parent.getParentFragment();
        }

        Activity context = parent.getActivity();
        if (context instanceof BaseActivity) {
            ((BaseActivity)context).setTabVisible(value);
        }
    }

}
