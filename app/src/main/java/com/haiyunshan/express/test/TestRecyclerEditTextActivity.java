package com.haiyunshan.express.test;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.compose.widget.ParagraphView;

import java.util.ArrayList;
import java.util.List;

public class TestRecyclerEditTextActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    int mScrollY;

    boolean mEditable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recycler_edit_text);

        this.mRecyclerView = (RecyclerView) (this.findViewById(R.id.rv_list));
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);

        TestAdapter adapter = new TestAdapter(this, this.getList());
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mScrollY = dy;
            }
        });

    }

    private class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Entry> mList;
        LayoutInflater mInflater;

        TestAdapter(Activity context, List<Entry> list) {
            this.mInflater = context.getLayoutInflater();
            this.mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int resource = R.layout.layout_test_edittext;
            View view = mInflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Entry e = mList.get(position);

            ItemHolder itemHolder = (ItemHolder)holder;
            itemHolder.bind(position, e);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);

            ItemHolder itemHolder = (ItemHolder)holder;
            itemHolder.onDetached();

            if (itemHolder.hasFocus()) {
                int pos;

                if (mScrollY > 0) {
                    pos = itemHolder.mPosition + 1;
                } else {
                    pos = itemHolder.mPosition - 1;
                }

                if (pos < this.getItemCount() && pos >= 0) {
                    itemHolder = (ItemHolder)mRecyclerView.findViewHolderForAdapterPosition(pos);
                    if (itemHolder != null) {
                        itemHolder.mEditText.requestFocus();
//                        itemHolder.mParagraphView.selectAll();
                    }
                }
            }
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ParagraphView mEditText;

        int mPosition;
        Entry mEntry;

        public ItemHolder(View itemView) {
            super(itemView);

            this.mEditText = (ParagraphView)itemView.findViewById(R.id.edit_content);
            itemView.setOnLongClickListener(this);
        }

        void bind(int position, Entry e) {
            this.mPosition = position;
            this.mEntry = e;

            mEditText.setText(e.mText);
            mEditText.setEditable(mEditable);

        }

        void onDetached() {
            mEntry.mText = mEditText.getText().toString();
        }

        boolean hasFocus() {
            return mEditText.hasFocus();
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == itemView) {
                mEditable = !mEditable;
                mRecyclerView.getAdapter().notifyDataSetChanged();

                return true;
            }

            return false;
        }
    }

    private class Entry {
        String mId;
        String mText;

        public Entry(String str) {
            this.mId = UUIDUtils.next();
            mText = str;
        }
    }


    List<Entry> getList() {
        ArrayList<Entry> list = new ArrayList<>();

        list.add(new Entry("缺月挂疏桐\n漏断人初静\n谁见幽人独往来\n飘缈孤鸿影\n惊起却回头\n\n有恨无人省\n拣尽寒枝不肯栖\n寂寞沙洲冷\n"));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry("惊起却回头"));
        list.add(new Entry("有恨无人省"));
        list.add(new Entry("拣尽寒枝不肯栖"));
        list.add(new Entry("寂寞沙洲冷"));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry("惊起却回头"));
        list.add(new Entry("有恨无人省"));
        list.add(new Entry("拣尽寒枝不肯栖"));
        list.add(new Entry("寂寞沙洲冷"));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry("惊起却回头"));
        list.add(new Entry("有恨无人省"));
        list.add(new Entry("拣尽寒枝不肯栖"));
        list.add(new Entry("寂寞沙洲冷"));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));
        list.add(new Entry(""));

        for (int i = 0; i < 30; i++) {
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry("惊起却回头"));
            list.add(new Entry("有恨无人省"));
            list.add(new Entry("拣尽寒枝不肯栖"));
            list.add(new Entry("寂寞沙洲冷"));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));
            list.add(new Entry(""));

        }

        return list;
    }

}
