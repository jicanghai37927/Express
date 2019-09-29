package com.haiyunshan.express;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.haiyunshan.express.dataset.DataManager;

/**
 * Created by sanshibro on 2017/11/12.
 */

public class App extends Application {

    private static App sInstance;

    DataManager mDataManager;
    int mActivityCount;

    public static final App instance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        this.mDataManager = new DataManager();
        this.mActivityCount = 0;

        this.registerActivityLifecycleCallbacks(mCallback);
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public int getActivityCount() {
        return mActivityCount;
    }

    ActivityLifecycleCallbacks mCallback = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            ++mActivityCount;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            --mActivityCount;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };
}
