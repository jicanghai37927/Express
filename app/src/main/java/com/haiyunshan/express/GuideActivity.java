package com.haiyunshan.express;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;

import com.haiyunshan.express.dataset.DataManager;

public class GuideActivity extends BaseActivity {

    private static final long timeDELAY     = 240; // 启动时间
    private static final long timeDURATION  = 2000;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        this.setNaviVisible(true);

        this.mHandler = new Handler();

        GuideRunnable r = new GuideRunnable(this);
        mHandler.postDelayed(r, timeDELAY);
    }

    @Override
    public void onBackPressed() {

    }

    /**
     *
     */
    private class GuideRunnable implements Runnable {

        Activity mContext;

        GuideRunnable(Activity context) {
            this.mContext = context;
        }

        @Override
        public void run() {

            long time = System.currentTimeMillis();
            this.init();
            long ellapse = System.currentTimeMillis() - time;

            long duration = (timeDURATION - timeDELAY);
            if (ellapse >= duration) {
                this.start(mContext);
            } else {
                try {
                    Thread.sleep(duration - ellapse);
                } catch (InterruptedException e) {
                }

                this.start(mContext);
            }
        }

        void init() {
            DataManager mgr = DataManager.instance();
            mgr.getCatalogManager();
            mgr.getNoteManager();

            mgr.getTypefaceManager();
        }

        void start(Activity context) {
            MainActivity.start(context);

            context.finish();
            context.overridePendingTransition(0, 0);
        }
    }
}
