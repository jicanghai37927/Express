package com.haiyunshan.express.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.CheckedMap;
import com.haiyunshan.express.app.Utils;
import com.haiyunshan.express.dataset.catalog.CatalogEntry;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.decoration.MarginDividerDecoration;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.dialog.ConfirmDialog;
import com.haiyunshan.express.dialog.MessageDialog;
import com.haiyunshan.express.widget.MyRecyclerView;

import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrashFragment extends BaseFragment implements View.OnClickListener {

    public static final String argCatalog = "catalog";

    RecyclerView mRecyclerView;
    NoteAdapter mAdapter;
    CheckedMap<NoteEntry> mCheckedMap;
    SortedList<NoteEntry> mList;
    SimpleDividerDecoration mDividerDecor;

    View mTopNormalBar;
    View mTopEditBar;
    View mBottomBar;

    View mBackBtn;
    TextView mTitleView;
    View mCreateBtn;
    View mEditBtn;

    TextView mSelectBtn;
    TextView mEditTitleView;
    View mDoneBtn;

    TextView mRestoreView;
    TextView mDeleteView;

    CatalogEntry mCatalog;

    boolean mEditMode = false;

    Handler mHandler;

    public TrashFragment() {
        this.mHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trash, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = (RecyclerView) (view.findViewById(R.id.rv_list));
            LinearLayoutManager layout = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mTopNormalBar = view.findViewById(R.id.top_normal_bar);

            this.mTopEditBar = view.findViewById(R.id.top_edit_bar);

            this.mBottomBar = view.findViewById(R.id.bottom_bar);
            mBottomBar.setVisibility(View.GONE);
        }

        {
            this.mBackBtn = view.findViewById(R.id.btn_back);
            mBackBtn.setOnClickListener(this);

            this.mTitleView = (TextView) (view.findViewById(R.id.tv_title));

            this.mCreateBtn = view.findViewById(R.id.btn_create);
            mCreateBtn.setOnClickListener(this);
            mCreateBtn.setEnabled(false);

            this.mEditBtn = view.findViewById(R.id.btn_edit);
            mEditBtn.setOnClickListener(this);

        }

        {
            this.mSelectBtn = (TextView) view.findViewById(R.id.btn_select);
            mSelectBtn.setOnClickListener(this);

            this.mEditTitleView = (TextView) (view.findViewById(R.id.tv_edit_title));

            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);
        }

        {
            this.mRestoreView = (TextView)view.findViewById(R.id.btn_restore);
            mRestoreView.setOnClickListener(this);

            this.mDeleteView = (TextView)view.findViewById(R.id.btn_delete);
            mDeleteView.setOnClickListener(this);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.mCatalog = CatalogManager.instance().obtain(CatalogManager.idTrash);
        }

        {
            String name = mCatalog.getName();
            this.mTitleView.setText(name);
        }

        {
            this.mCheckedMap = new CheckedMap<>();

            this.mAdapter = new NoteAdapter(this.getActivity());
            PoemSortedListAdapterCallback callback = new PoemSortedListAdapterCallback(mAdapter);
            this.mList = new SortedList<>(NoteEntry.class, callback);
            mList.addAll(getList(this.mCatalog));
            mRecyclerView.setAdapter(mAdapter);

            // item decor

            {
                MarginDividerDecoration decor = new MarginDividerDecoration(this.getActivity());
                int margin = getResources().getDimensionPixelSize(R.dimen.inset_4l);
                margin = 2 * margin;
                decor.setMargin(0, margin);
                mRecyclerView.addItemDecoration(decor);
            }

            {
                SimpleDividerDecoration decor = new SimpleDividerDecoration(this.getActivity());
                decor.setMargin(getResources().getDimensionPixelSize(R.dimen.inset_2l), 0);
                mRecyclerView.addItemDecoration(decor);
                this.mDividerDecor = decor;
            }
        }

        this.updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.setEditMode(false, true);
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            getActivity().onBackPressed();
        } else if (v == mCreateBtn) {
            this.requestCreate();
        } else if (v == mEditBtn) {
            this.setEditMode(true, true);
        } else if (v == mSelectBtn) {
            this.requestSelect();
        } else if (v == mRestoreView) {
            this.requestRestore();
        } else if (v == mDeleteView) {
            this.requestDelete();
        } else if (v == mDoneBtn) {
            this.setEditMode(false, true);
        }
    }

    List<NoteEntry> getList(CatalogEntry catalog) {
        List<NoteEntry> list = CatalogManager.instance().getList(catalog);

        return list;
    }

    void setEditMode(boolean value, boolean notify) {
        if (!(mEditMode ^ value)) {
            return;
        }

        this.mEditMode = value;
        if (mEditMode) {
            this.setTabVisible(false);

            mTopNormalBar.setVisibility(View.INVISIBLE);
            mTopEditBar.setVisibility(View.VISIBLE);

            mBottomBar.setVisibility(View.VISIBLE);

            mCheckedMap.clear();

            mEditTitleView.setText("选择项目");

            boolean isEmpty = (CatalogManager.instance().getCount(this.mCatalog) == 0);
            mSelectBtn.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);

            String name = "全选";
            mSelectBtn.setText(name);

            name = (mCheckedMap.isEmpty()) ? "全部恢复" : "恢复";
            mRestoreView.setText(name);

            name = (mCheckedMap.isEmpty()) ? "全部删除" : "删除";
            mDeleteView.setText(name);

            boolean enable = !isEmpty;
            mRestoreView.setEnabled(enable);
            mDeleteView.setEnabled(enable);

        } else {
            this.setTabVisible(true);

            mTopNormalBar.setVisibility(View.VISIBLE);
            mTopEditBar.setVisibility(View.INVISIBLE);

            mBottomBar.setVisibility(View.GONE);
        }

        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
    }

    void closeEditMode() {
        this.setEditMode(false, true);
    }

    void onCheckedChanged() {

        int count = mCheckedMap.size();
        String name = (count == 0) ? "选择项目" : count + " 项";
        mEditTitleView.setText(name);

        name = (count == mList.size()) ? "取消全选" : "全选";
        mSelectBtn.setText(name);

        name = (mCheckedMap.isEmpty()) ? "全部恢复" : "恢复";
        mRestoreView.setText(name);

        name = (mCheckedMap.isEmpty()) ? "全部删除" : "删除";
        mDeleteView.setText(name);
    }

    void requestSelect() {
        int count = mList.size();
        boolean all = (count == mCheckedMap.size());
        if (all) {
            mCheckedMap.clear();
        } else {
            for (int i = 0; i < count; i++) {
                NoteEntry e = mList.get(i);
                mCheckedMap.setChecked(e.getId(), e, true);
            }
        }

        mAdapter.notifyDataSetChanged();
        this.onCheckedChanged();
    }

    void requestRestore() {
        int size = mList.size();

        if (size == 0) {
            return;
        }

        doRestore();
    }

    void doRestore() {

        this.closeEditMode();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doRestoreRunnable();
            }
        });
    }

    void doRestoreRunnable() {

        int checkedCount = mCheckedMap.size();
        if (checkedCount == 0) {
            SortedList<NoteEntry> array = this.mList;
            int size = array.size();
            for (int i = size - 1; i >= 0; i--) {
                NoteEntry entry = array.get(i);

                mList.remove(entry);

                this.restore(entry);
            }
        } else {
            List<NoteEntry> list = mCheckedMap.getList();
            for (NoteEntry entry : list) {
                mList.remove(entry);

                this.restore(entry);
            }
        }

        // 存档
        CatalogManager.instance().save();
        NoteManager.instance().save();

        // 更新UI
        this.updateUI();

    }

    void restore(NoteEntry entry) {

        // 先恢复目录
        CatalogManager.instance().restore(entry);

        // 在恢复笔记
        NoteManager.instance().restore(entry);
    }

    void requestMove() {
        int count = mCheckedMap.size();
        if (count == 0) {
            return;
        }
    }

    void requestCreate() {
        CatalogManager mgr = CatalogManager.instance();
        CatalogEntry folder = this.mCatalog;
        if (mgr.isAll(folder)) {
            folder = mgr.getDefault();
        }

        NoteEntry entry = NoteManager.instance().create(folder, "笔记");
        mList.add(entry);

        this.updateUI();
    }

    void requestDelete() {

        int size = mList.size();
        int checkedCount = mCheckedMap.size();

        if (size == 0) {
            return;
        }

        CharSequence title;
        CharSequence msg;
        CharSequence btnCancel = "取消";
        CharSequence confirmBtn = "删除";
        if (checkedCount == 0) { // 删除全部

            if (size == 1) {
                title = "您确定要删除此项吗？";
                msg = "此项目将立即从您的所有设备上删除。\n您不能撤销此操作。";
            } else {
                title = String.format("您确定要删除 %1$d 项吗？", size);
                msg = "这些项目将立即从您的所有设备上删除。\n您不能撤销此操作。";
            }

        } else if (checkedCount == 1) { // 删除选定

            NoteEntry entry = mCheckedMap.peek();
            title = String.format("您确定要删除“%1$s”吗？", entry.getName());
            msg = "此项目将立即从您的所有设备上删除。\n您不能撤销此操作。";

        } else {

            title = String.format("您确定要删除 %1$d 个项目吗？", checkedCount);
            msg = "这些项目将立即从您的所有设备上删除。\n您不能撤销此操作。";

        }

        Context context = this.getActivity();
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    doDelete();
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                }
            }
        };

        ConfirmDialog.showWarning(context, title, msg,
                btnCancel, confirmBtn,
                listener);

    }

    void doDelete() {

        this.closeEditMode();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doDeleteRunnable();
            }
        });
    }

    void doDeleteRunnable() {

        NoteManager mgr = NoteManager.instance();

        int checkedCount = mCheckedMap.size();
        if (checkedCount == 0) {
            SortedList<NoteEntry> array = this.mList;
            int size = array.size();
            for (int i = size - 1; i >= 0; i--) {
                NoteEntry entry = array.get(i);

                mList.remove(entry);
                mgr.remove(entry);
            }
        } else {
            List<NoteEntry> list = mCheckedMap.getList();
            for (NoteEntry entry : list) {
                mList.remove(entry);
                mgr.remove(entry);
            }
        }

        // 存档
        NoteManager.instance().save();

        // 更新UI
        this.updateUI();
    }

    void updateUI() {
        if (mAdapter.getHeaderCount() == mAdapter.getItemCount()) {
            mDividerDecor.setEnable(false);
        } else {
            mDividerDecor.setEnable(true);
        }

        boolean enable = CatalogManager.instance().getCount(this.mCatalog) != 0;
        mRestoreView.setEnabled(enable);
        mDeleteView.setEnabled(enable);
    }

    /**
     *
     */
    private class PoemSortedListAdapterCallback extends SortedListAdapterCallback<NoteEntry> {

        Collator mCollator;

        public PoemSortedListAdapterCallback(RecyclerView.Adapter adapter) {
            super(adapter);

            this.mCollator = Collator.getInstance(Locale.CHINA);
        }

        @Override
        public int compare(NoteEntry o1, NoteEntry o2) {
            int r = mCollator.compare(o1.getName(), o2.getName());

            r = 0;
            if (r == 0) {
                long v = o1.getCreated() - o2.getCreated();
                r = 0;
                if (v > 0) {
                    r = -1;
                } else if (v < 0) {
                    r = 1;
                }
            }
            return r;
        }

        @Override
        public boolean areContentsTheSame(NoteEntry oldItem, NoteEntry newItem) {
            boolean name = oldItem.getName().equals(newItem.getName());
            boolean desc = oldItem.getDesc().equals(newItem.getDesc());

            return (name && desc);
        }

        @Override
        public boolean areItemsTheSame(NoteEntry item1, NoteEntry item2) {
            return item1.getId().equals(item2.getId());
        }

        @Override
        public void onInserted(int position, int count) {
            position += mAdapter.getHeaderCount();

            super.onInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            position += mAdapter.getHeaderCount();

            super.onRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            fromPosition += mAdapter.getHeaderCount();
            toPosition += mAdapter.getHeaderCount();

            super.onMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            position += mAdapter.getHeaderCount();

            super.onChanged(position, count);
        }
    }

    /**
     *
     */
    private class NoteAdapter extends MyRecyclerView.RecyclerAdapter<RecyclerView.ViewHolder> {

        static final int TYPE_HEADER_DESC = 1001;
        static final int TYPE_ITEM = 2001;

        int[] mHeader;

        LayoutInflater mInflater;

        NoteAdapter(Activity context) {
            this.mInflater = context.getLayoutInflater();

            this.mHeader = new int[]{
                    TYPE_HEADER_DESC
            };
        }

        @Override
        public int getHeaderCount() {
            if (mHeader == null) {
                return 0;
            }

            return mHeader.length;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < this.getHeaderCount()) {
                return mHeader[position];
            }

            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            if (viewType == TYPE_HEADER_DESC) {
                holder = DescHolder.create(TrashFragment.this, mInflater, parent);
            } else if (viewType == TYPE_ITEM) {
                holder = ItemHolder.create(TrashFragment.this, mInflater, parent);
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position < getHeaderCount()) {
                int viewType = holder.getItemViewType();
                if (viewType == TYPE_HEADER_DESC) {

                }
            } else {
                position = position - mHeader.length;

                NoteEntry e = mList.get(position);

                ItemHolder itemHolder = (ItemHolder) holder;
                itemHolder.bind(position, e);
            }
        }

        @Override
        public int getItemCount() {
            int size = mHeader.length;
            size += mList.size();

            return size;
        }
    }

    /**
     *
     */
    private static class DescHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TrashFragment mFragment;

        static DescHolder create(TrashFragment fragment, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_trash_header_item;
            View view = inflater.inflate(resource, parent, false);

            DescHolder holder = new DescHolder(fragment, view);
            return holder;
        }

        public DescHolder(TrashFragment fragment, View itemView) {
            super(itemView);
            this.mFragment = fragment;

        }

        @Override
        public void onClick(View v) {

        }
    }

    /**
     *
     */
    private static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CheckBox.OnCheckedChangeListener {

        CheckBox mCheckBox;
        TextView mNameView;
        TextView mDayView;
        TextView mDescView;
        TextView mCatalogView;

        int mPosition;
        NoteEntry mEntry;

        TrashFragment mFragment;

        static ItemHolder create(TrashFragment fragment, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_note_item;
            View view = inflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(fragment, view);
            return holder;
        }

        public ItemHolder(TrashFragment fragment, View itemView) {
            super(itemView);
            this.mFragment = fragment;

            this.mCheckBox = (CheckBox) (itemView.findViewById(R.id.cb_check));
            this.mNameView = (TextView) (itemView.findViewById(R.id.tv_name));
            this.mDayView = (TextView) (itemView.findViewById(R.id.tv_day));
            this.mDescView = (TextView) (itemView.findViewById(R.id.tv_desc));
            this.mCatalogView = (TextView) (itemView.findViewById(R.id.tv_catalog));
            mCatalogView.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        void bind(int position, NoteEntry entry) {
            this.mPosition = position;
            this.mEntry = entry;

            String name = entry.getName();
            name = name.replace("\n", " ");
            String day = Utils.getDay(itemView.getContext(), entry.getCreated());
            String desc = entry.getDesc();

            mNameView.setText(name);
            mDayView.setText(day);
            mDescView.setText(desc);

            this.setEditMode(mFragment.mEditMode);
        }

        void setEditMode(boolean value) {
            NoteEntry entry = this.mEntry;

            if (value) {
                itemView.setSelected(mFragment.mCheckedMap.isChecked(entry));

                mCheckBox.setVisibility(View.VISIBLE);
                mCheckBox.setOnCheckedChangeListener(null);
                mCheckBox.setChecked(mFragment.mCheckedMap.isChecked(entry));
                mCheckBox.setOnCheckedChangeListener(this);

            } else {
                itemView.setSelected(false);

                mCheckBox.setVisibility(View.GONE);
                mCheckBox.setOnCheckedChangeListener(null);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                if (mFragment.mEditMode) {
                    mCheckBox.setChecked(!mCheckBox.isChecked());
                } else {
                    this.showMessage(mEntry);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mCheckBox == buttonView) {
                mFragment.mCheckedMap.setChecked(mEntry.getId(), mEntry, isChecked);
                itemView.setSelected(isChecked);

                mFragment.onCheckedChanged();
            }
        }

        void showMessage(NoteEntry entry) {
            Context context = mFragment.getActivity();
            CharSequence title = String.format("不能打开笔记“%1$s”，因为它在“最近删除”中。", entry.getName());
            CharSequence msg = "若要使用此项目，请先恢复此项目。";
            CharSequence btn = "好";

            MessageDialog.show(context, title, msg, btn);
        }
    }
}
