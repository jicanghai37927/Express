package com.haiyunshan.express.test;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.widget.HighlightEditText;

import java.lang.reflect.Method;

public class TestParagraphActivity extends AppCompatActivity {

    boolean mEditable = true;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    TextView mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_paragraph);

        if (false) {
            LinearLayout layout = findViewById(R.id.sv_content);
            for (int i = 0; i < 1000; i++) {
//                fillEdit(layout);
                fillParagraph(layout);
            }
        }

        if (false) {
            mRecyclerView = findViewById(R.id.rv_list);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(manager);

//        mAdapter = new ParagraphAdapter();
            mAdapter = new EditTextAdapter();

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemViewCacheSize(0);

            this.mBtn = findViewById(R.id.btn_edit);
            mBtn.setText(mEditable ? "退出编辑" : "进入编辑");
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditable = !mEditable;

                    mBtn.setText(mEditable ? "退出编辑" : "进入编辑");

                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        if (true) {
            mRecyclerView = findViewById(R.id.rv_list);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(manager);

            mAdapter = new EditTextAdapter();

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemViewCacheSize(0);


            this.mBtn = findViewById(R.id.btn_edit);
            mBtn.setVisibility(View.GONE);
            mBtn.setText(mEditable ? "退出编辑" : "进入编辑");
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditable = !mEditable;

                    mBtn.setText(mEditable ? "退出编辑" : "进入编辑");

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    void fillParagraph(LinearLayout layout) {
        ParagraphView view = new ParagraphView(TestParagraphActivity.this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        String text = "\n半神半圣亦半仙,\n全儒全道是全贤;\n脑中真书藏万卷,\n掌握文武半边天。\n";
        view.setText(text);

        layout.addView(view);
    }

    void fillEdit(LinearLayout layout) {
        EditText view = createEditText(TestParagraphActivity.this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        String text = "\n半神半圣亦半仙,\n全儒全道是全贤;\n脑中真书藏万卷,\n掌握文武半边天。\n";
        view.setText(text);

        layout.addView(view);
    }

    EditText createEditText(Context context) {
        EditText edit = new EditText(context);
        edit.setId(R.id.edit_paragraph);

        edit.setMinWidth(320);

        edit.setTextColor(Color.BLACK);
        edit.setBackgroundColor(Color.TRANSPARENT);
        edit.setGravity(Gravity.TOP | Gravity.LEFT);
        edit.setPadding(0, 0, 0, 0);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        edit.setLayoutParams(params);


        return edit;
    }

    private class EditTextAdapter extends RecyclerView.Adapter<ParagraphHolder> {

        @NonNull
        @Override
        public ParagraphHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            EditText view = createEditText(TestParagraphActivity.this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ParagraphHolder holder = new ParagraphHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ParagraphHolder holder, int position) {
            EditText view = (EditText)(holder.itemView);
            String text = "\n半神半圣亦半仙,\n全儒全道是全贤;\n脑中真书藏万卷,\n掌握文武半边天。\n";
            view.setText(text);

//            view.setEditable(mEditable);
            view.setEnabled(false);
            view.setEnabled(mEditable);
//            nullLayouts(view);

//            view.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);


//            view.setCursorVisible(true);
//            view.setSaveEnabled(false);
//            view.setFocusable(true);
//            view.setFocusableInTouchMode(true);
//            view.setShowSoftInputOnFocus(true);
//            view.setTextIsSelectable(true);
//            view.setFreezesText(false);
//            view.setFilterTouchesWhenObscured(false);
//            view.setDuplicateParentStateEnabled(false);
//            view.setSaveFromParentEnabled(false);

            LogUtils.w("EditTextAdapter", "onBindViewHolder");
        }

        @Override
        public int getItemCount() {
            return 1000;
        }
    }

    private void nullLayouts(TextView view) {
        try {
            Method textCanBeSelected = TextView.class.getDeclaredMethod("nullLayouts");
            textCanBeSelected.setAccessible(true);
            textCanBeSelected.invoke(view);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ParagraphAdapter extends RecyclerView.Adapter<ParagraphHolder> {

        @NonNull
        @Override
        public ParagraphHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ParagraphView view = new ParagraphView(TestParagraphActivity.this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ParagraphHolder holder = new ParagraphHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ParagraphHolder holder, int position) {
            ParagraphView view = (ParagraphView)(holder.itemView);
            String text = "\n半神半圣亦半仙,\n全儒全道是全贤;\n脑中真书藏万卷,\n掌握文武半边天。\n";
            view.setText(text);

            view.setEditable(mEditable);
        }

        @Override
        public int getItemCount() {
            return 1000;
        }
    }

    private class ParagraphHolder extends RecyclerView.ViewHolder {

        public ParagraphHolder(View itemView) {
            super(itemView);
        }
    }
}
