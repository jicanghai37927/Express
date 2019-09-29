package com.haiyunshan.express.fragment.note;


import android.app.Activity;
import android.graphics.Typeface;
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
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.note.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.typeface.TypefaceDataset;
import com.haiyunshan.express.typeface.TypefaceEntry;
import com.haiyunshan.express.typeface.TypefaceManager;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class TypefaceFragment extends Fragment implements View.OnClickListener {

    View mBackBtn;
    View mDoneBtn;

    RecyclerView mRecyclerView;
    StyleAdapter mAdapter;

    TextFormatFragment mParent;
    ParagraphSegment mSegment;

    public TypefaceFragment() {

    }

    void setArguments(TextFormatFragment parent, ParagraphSegment segment) {
        this.mParent = parent;
        this.mSegment = segment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_typeface, container, false);
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

        {
            this.mAdapter = new StyleAdapter(getActivity(), getList());
            mRecyclerView.setAdapter(mAdapter);
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
        if (mParent == null || mSegment == null) {
            return;
        }

        StyleEntity en = mSegment.getStyle();
        String oldId = ParagraphStyleUtils.getTypeface(en, mSegment.getDefaultStyle());

        if (!id.equalsIgnoreCase(oldId)) {

            en.setTypeface(id);

            ParagraphSegmentHolder holder = mParent.getParent().findParagraphHolder(mSegment);
            if (holder != null) {
                holder.applyStyle();
            }

            int index = 0;
            while (true) {
                index = mAdapter.indexOf(oldId, index);
                if (index >= 0) {
                    mAdapter.notifyItemChanged(index);

                    index += 1;
                } else {
                    break;
                }
            }

            index = 0;
            while (true) {
                index = mAdapter.indexOf(id, index);
                if (index >= 0) {
                    mAdapter.notifyItemChanged(index);

                    index += 1;
                } else {
                    break;
                }
            }

        }
    }

    /**
     *
     * @return
     */
    List<TypefaceEntry> getList() {
        ArrayList<TypefaceEntry> list = new ArrayList<>();

        // 内置的分类
        {
            TypefaceManager dm = TypefaceManager.instance();
            list.addAll(0, dm.getAsset().getList());
        }

        // 用户定义的分类
        {
            TypefaceDataset ds = TypefaceManager.instance().getDataset();
            list.addAll(ds.getList());

        }

        {
            final Collator collator = Collator.getInstance(Locale.CHINA);

            Collections.sort(list, new Comparator<TypefaceEntry>() {
                @Override
                public int compare(TypefaceEntry o1, TypefaceEntry o2) {
                    int r = 0;

                    int t1 = o1.getType();
                    int t2 = o2.getType();
                    if (t1 != t2) {
                        r = t2 - t1;
                    } else {
                        r = collator.compare(o1.getAlias(), o2.getAlias());
                    }

                    return r;
                }
            });
        }

        return list;
    }

    /**
     *
     */
    private class StyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<TypefaceEntry> mList;

        LayoutInflater mInflater;

        StyleAdapter(Activity context, List<TypefaceEntry> list) {
            this.mInflater = context.getLayoutInflater();

            this.mList = list;
        }

        int indexOf(String id) {
            return indexOf(id, 0);
        }

        int indexOf(String id, int fromIndex) {
            int size = mList.size();
            for (int i = fromIndex; i < size; i++) {
                TypefaceEntry e = mList.get(i);
                if (e.getId().equalsIgnoreCase(id)) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int resource = R.layout.layout_typeface_item;
            View view = mInflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TypefaceEntry e = mList.get(position);

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

        TypefaceEntry mEntry;

        public ItemHolder(View itemView) {
            super(itemView);

            this.mNameView = (TextView)itemView.findViewById(R.id.tv_name);
            this.mMarkView = itemView.findViewById(R.id.iv_mark);

            itemView.setOnClickListener(this);
        }

        void bind(int position, TypefaceEntry s) {
            this.mEntry = s;

            mNameView.setText(s.getAlias());

            Typeface tf = TypefaceManager.instance().getTypeface(s, false, false);
            mNameView.setTypeface(tf);

            if (mSegment != null) {
                StyleEntity style = mSegment.getStyle();
                StyleEntity defaultStyle = mSegment.getDefaultStyle();
                String typeface = ParagraphStyleUtils.getTypeface(style, defaultStyle);
                TypefaceEntry en = TypefaceManager.instance().obtain(typeface);

                boolean checked = (s.getId().equalsIgnoreCase(en.getId()));
                mMarkView.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                setChecked(mEntry.getId());
            }
        }
    }

}
