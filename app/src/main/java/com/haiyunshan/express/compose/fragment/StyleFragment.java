package com.haiyunshan.express.compose.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.ParagraphStyleUtils;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.segment.ParagraphSegment;

import java.util.List;

/**
 * 段落样式
 *
 */
public class StyleFragment extends Fragment implements View.OnClickListener {

    View mBackBtn;
    View mDoneBtn;

    RecyclerView mRecyclerView;
    StyleAdapter mAdapter;

    TextFormatFragment mParent;
    ParagraphSegment mSegment;

    public StyleFragment() {

    }

    public void setArguments(TextFormatFragment parent, ParagraphSegment segment) {
        this.mParent = parent;
        this.mSegment = segment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_style, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mBackBtn = view.findViewById(R.id.btn_back);
            mBackBtn.setOnClickListener(this);

            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);
        }

        {
            this.mRecyclerView = (RecyclerView) (view.findViewById(R.id.rv_list));
            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NoteComposeFragment context = mParent.getParent().getFragment();
        Document doc = context.getDocument();

        {
            this.mAdapter = new StyleAdapter(getActivity(), doc.getStyle().getList());
            mRecyclerView.setAdapter(mAdapter);

            // item decor
            SimpleDividerDecoration decor = new SimpleDividerDecoration(getActivity());
            decor.setMargin(getResources().getDimensionPixelSize(R.dimen.inset_2l), 0);
            mRecyclerView.addItemDecoration(decor);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            mParent.back();
        } else if (v == mDoneBtn) {
            mParent.close();
        }
    }

    void setChecked(String id) {
        StyleEntity en = mSegment.getStyle();
        String oldId = en.getId();
        en.setId(id);

        if (id.equalsIgnoreCase(oldId)) {
            if (en.isSet()) {
                en.clear();

                mParent.mParent.applyStyle(mSegment);

            }

        } else {
            en.clear();

            mParent.mParent.applyStyle(mSegment);

            int index = mAdapter.indexOf(oldId);
            if (index >= 0) {
                mAdapter.notifyItemChanged(index);
            }

            index = mAdapter.indexOf(id);
            if (index >= 0) {
                mAdapter.notifyItemChanged(index);
            }
        }
    }

    /**
     *
     */
    private class StyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<StyleEntity> mList;

        LayoutInflater mInflater;

        StyleAdapter(Activity context, List<StyleEntity> list) {
            this.mInflater = context.getLayoutInflater();

            this.mList = list;
        }

        int indexOf(String id) {
            int size = mList.size();
            for (int i = 0; i < size; i++) {
                StyleEntity e = mList.get(i);
                if (e.getId().equalsIgnoreCase(id)) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int resource = R.layout.layout_style_item;
            View view = mInflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            StyleEntity e = mList.get(position);

            ItemHolder itemHolder = (ItemHolder)holder;
            itemHolder.bind(position, e);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    /**
     *
     */
    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameView;
        View mMarkView;

        StyleEntity mStyle;

        public ItemHolder(View itemView) {
            super(itemView);

            this.mNameView = (TextView)itemView.findViewById(R.id.tv_name);
            this.mMarkView = itemView.findViewById(R.id.iv_mark);

            itemView.setOnClickListener(this);
        }

        void bind(int position, StyleEntity s) {
            this.mStyle = s;

            mNameView.setText(s.getName());
            ParagraphStyleUtils.apply(mNameView, s);
            mNameView.setTextAlignment(StyleEntity.ALIGN_START);

            boolean checked = (s.getId().equalsIgnoreCase(mSegment.getStyle().getId()));
            mMarkView.setVisibility(checked? View.VISIBLE: View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                setChecked(mStyle.getId());
            }
        }
    }
}
