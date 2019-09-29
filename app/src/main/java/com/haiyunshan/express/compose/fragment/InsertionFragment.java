package com.haiyunshan.express.compose.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.R;
import com.haiyunshan.express.compose.state.InsertState;
import com.haiyunshan.express.fragment.BaseFragment;

/**
 *
 */
public class InsertionFragment extends BaseFragment implements View.OnClickListener {

    public static final int eventBackspace  = 0x11;
    public static final int eventSplit      = 0x12;
    public static final int eventStop       = 0x13;

    View mDoneBtn;

    View mFullSpaceItem;
    View mTabItem;
    View mReturnItem;

    View mSplitItem;
    View mStopItem;

    View mBackspaceItem;

    InsertState mParent;

    public InsertionFragment() {

    }

    public void setArguments(InsertState parent, Bundle args) {
        this.mParent = parent;

        if (args != null) {
            super.setArguments(args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insertion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);
        }

        {
            this.mFullSpaceItem = view.findViewById(R.id.item_full_space);
            mFullSpaceItem.setOnClickListener(this);

            this.mTabItem = view.findViewById(R.id.item_tab);
            mTabItem.setOnClickListener(this);

            this.mReturnItem = view.findViewById(R.id.item_return);
            mReturnItem.setOnClickListener(this);
        }

        {
            this.mSplitItem = view.findViewById(R.id.item_split);
            mSplitItem.setOnClickListener(this);

            this.mStopItem = view.findViewById(R.id.item_stop);
            mStopItem.setOnClickListener(this);
        }

        {
            this.mBackspaceItem = view.findViewById(R.id.item_backspace);
            mBackspaceItem.setOnClickListener(this);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v == mDoneBtn) {
            mParent.pop();
        } else if (v == mFullSpaceItem) {
            String str = getString(R.string.full_space);
            char c = str.charAt(0);

            mParent.onCharacter(c);
        } else if (v == mTabItem) {
            char c = '\t';

            mParent.onCharacter(c);
        } else if (v == mReturnItem) {
            char c = '\n';

            mParent.onCharacter(c);
        } else if (v == mBackspaceItem) {
            mParent.onEvent(eventBackspace);
        } else if (v == mSplitItem) {
            mParent.onEvent(eventSplit);
        } else if (v == mStopItem) {
            mParent.onEvent(eventStop);
        }
    }
}
