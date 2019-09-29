package com.haiyunshan.express.test;

import android.animation.ValueAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.haiyunshan.express.R;

public class TestActivity extends AppCompatActivity {

    View mRoot;
    View mTopBar;
    View mBottomBar;
    View mContentLayout;

    OnPreDrawListener mOnPreDrawListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.mRoot = findViewById(R.id.root_container);
        this.mTopBar = findViewById(R.id.top_bar);
        this.mBottomBar = findViewById(R.id.bottom_bar);
        this.mContentLayout = findViewById(R.id.nsv_view);

        mOnPreDrawListener = new OnPreDrawListener();
//        View view = this.getWindow().getDecorView();
//        view.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);

        findViewById(R.id.btn_format).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTopBar.setVisibility(View.GONE);
                mBottomBar.setVisibility(View.VISIBLE);
//                hideTopBar();
//                showBottomBar();
            }
        });

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        View view = this.getWindow().getDecorView();
//        view.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
    }

    @Override
    public void onBackPressed() {
        if (mTopBar.getTop() != 0) {
            showTopBar();
            return;
        }

        super.onBackPressed();
    }

    private class OnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

        @Override
        public boolean onPreDraw() {
            if (mTopBar.getVisibility() == View.VISIBLE) {
                int offset = mTopBar.getBottom();

                if (mContentLayout.getTop() != offset) {
                    mContentLayout.setTop(offset);
//                mContentLayout.offsetTopAndBottom(offset);
                }
            }

            if (mBottomBar.getVisibility() == View.VISIBLE) {
                int offset = mBottomBar.getTop();
                if (mContentLayout.getBottom() != offset) {
                    mContentLayout.setBottom(offset);
                }
            }
            return true;
        }
    }

    private void hideTopBar() {
        ValueAnimator animator = new ValueAnimator();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            ViewOffsetHelper helper = new ViewOffsetHelper(mTopBar);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer)animation.getAnimatedValue();
                helper.setTopAndBottomOffset(value);
            }
        });
        animator.setIntValues(mTopBar.getTop(), -mTopBar.getHeight());
        animator.start();
    }

    private void showTopBar() {
        ValueAnimator animator = new ValueAnimator();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            ViewOffsetHelper helper = new ViewOffsetHelper(mTopBar);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer)animation.getAnimatedValue();
                helper.setTopAndBottomOffset(value);
            }
        });
        animator.setIntValues(mTopBar.getTop(), 0);
        animator.start();
    }

    private void showBottomBar() {
        if (mBottomBar.getVisibility() != View.VISIBLE) {
//            mBottomBar.setTop(mRoot.getHeight());
            mBottomBar.setVisibility(View.VISIBLE);
        }

//        final ViewOffsetHelper helper = new ViewOffsetHelper(mBottomBar);
//
//        ValueAnimator animator = new ValueAnimator();
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (Integer)animation.getAnimatedValue();
//                helper.setTopAndBottomOffset(value);
//            }
//        });
//        animator.setIntValues(mBottomBar.getTop(), mRoot.getHeight() - mBottomBar.getHeight());
//        animator.start();

    }

    class ViewOffsetHelper {

        private final View mView;

        private int mLayoutTop;
        private int mLayoutLeft;
        private int mOffsetTop;
        private int mOffsetLeft;

        public ViewOffsetHelper(View view) {
            mView = view;

            mOffsetTop = view.getTop();
            mOffsetLeft = view.getLeft();
        }

        public void onViewLayout() {
            // Now grab the intended top
            mLayoutTop = mView.getTop();
            mLayoutLeft = mView.getLeft();

            // And offset it as needed
            updateOffsets();
        }

        private void updateOffsets() {
            ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.getTop() - mLayoutTop));
            ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.getLeft() - mLayoutLeft));
        }

        /**
         * Set the top and bottom offset for this {@link android.support.design.widget.ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setTopAndBottomOffset(int offset) {
            if (mOffsetTop != offset) {
                mOffsetTop = offset;
                updateOffsets();
                return true;
            }
            return false;
        }

        /**
         * Set the left and right offset for this {@link android.support.design.widget.ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setLeftAndRightOffset(int offset) {
            if (mOffsetLeft != offset) {
                mOffsetLeft = offset;
                updateOffsets();
                return true;
            }
            return false;
        }

        public int getTopAndBottomOffset() {
            return mOffsetTop;
        }

        public int getLeftAndRightOffset() {
            return mOffsetLeft;
        }

        public int getLayoutTop() {
            return mLayoutTop;
        }

        public int getLayoutLeft() {
            return mLayoutLeft;
        }
    }
}
