package com.haiyunshan.express.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haiyunshan.express.NoteActivity;
import com.haiyunshan.express.R;
import com.haiyunshan.express.TrashActivity;
import com.haiyunshan.express.app.CheckedMap;
import com.haiyunshan.express.dataset.catalog.CatalogEntry;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.decoration.MarginDividerDecoration;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.dialog.ChoiceDialog;
import com.haiyunshan.express.dialog.InputDialog;
import com.haiyunshan.express.dialog.MessageDialog;

import java.text.Collator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CatalogFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_NOTE   = 1003;
    private static final int REQUEST_TRASH  = 1004;

    private static final int DELETE_EMPTY   = 1;    // 删除空的目录
    private static final int DELETE_ONLY    = 2;    // 删除目录，数据移动到备忘录
    private static final int DELETE_ALL     = 3;    // 目录，数据同时删除

    RecyclerView mRecyclerView;
    CatalogAdapter mAdapter;
    SortedList<CatalogEntry> mList;
    CheckedMap<CatalogEntry> mCheckedMap;

    View mTopNormalBar;
    View mTopEditBar;

    View mCreateFolderBtn;
    View mEditBtn;

    TextView mEditTitleView;
    TextView mDeleteBtn;
    View mDoneBtn;

    boolean mEditMode = false;

    Handler mHandler;

    public CatalogFragment() {
        this.mHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = (RecyclerView)(view.findViewById(R.id.rv_list));
            LinearLayoutManager layout = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mTopNormalBar = view.findViewById(R.id.top_normal_bar);
            this.mTopEditBar = view.findViewById(R.id.top_edit_bar);
        }

        {
            this.mCreateFolderBtn = view.findViewById(R.id.btn_create_folder);
            mCreateFolderBtn.setOnClickListener(this);

            this.mEditBtn = view.findViewById(R.id.btn_more);
            mEditBtn.setOnClickListener(this);

        }

        {
            this.mEditTitleView = (TextView) view.findViewById(R.id.tv_edit_title);
            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);

            this.mDeleteBtn = (TextView) view.findViewById(R.id.btn_delete);
            mDeleteBtn.setOnClickListener(this);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.mCheckedMap = new CheckedMap<>();

            CatalogAdapter adapter = new CatalogAdapter(this.getActivity());
            this.mAdapter = adapter;
            CatalogSortedListAdapterCallback callback = new CatalogSortedListAdapterCallback(adapter);
            this.mList = new SortedList<>(CatalogEntry.class, callback);

            mList.addAll(CatalogManager.instance().getList());
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
            }

        }

        {
            this.mEditBtn.setEnabled(!CatalogManager.instance().isEmpty());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NOTE || requestCode == REQUEST_TRASH) {

            // 添加用户新创建的目录
            {
                List<CatalogEntry> list = CatalogManager.instance().getUserList();
                for (CatalogEntry e : list) {

                    if (mList.indexOf(e) < 0) {
                        mList.add(e);
                    }
                }
            }

            // 全部笔记
            {
                CatalogEntry catalog = CatalogManager.instance().getAllEntry();

                if (NoteManager.instance().getCount() == 0) {
                    if (mList.indexOf(catalog) >= 0) {
                        mList.remove(catalog);
                    }
                } else {
                    if (mList.indexOf(catalog) < 0) {
                        mList.add(catalog);
                    }
                }
            }

            // 最近删除
            {
                CatalogEntry catalog = CatalogManager.instance().getTrashEntry();

                if (NoteManager.instance().getTrashCount() == 0) {
                    if (mList.indexOf(catalog) >= 0) {
                        mList.remove(catalog);
                    }
                } else {
                    if (mList.indexOf(catalog) < 0) {
                        mList.add(catalog);
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            this.setEditMode(false, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        this.setEditMode(false, true);
    }

    @Override
    public void onClick(View v) {
        if (v == mCreateFolderBtn) {
            this.requestCreateFolder();
        } else if (v == mEditBtn) {
            this.setEditMode(true, true);
        } else if (v == mDoneBtn) {
            this.setEditMode(false, true);
        } else if (v == mDeleteBtn) {
            this.requestDelete();
        }
    }

    void requestCreateFolder() {
        Context context = this.getActivity();
        String title = getString(R.string.catalog_create_folder_title);
        String msg = getString(R.string.catalog_create_folder_msg);
        String text = "";
        String hint = getString(R.string.catalog_name_hint);

        InputDialog dialog = InputDialog.create(context, title, msg, text, hint, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    InputDialog inputDialog = (InputDialog)dialog;
                    String text = inputDialog.getText();

                    doCreateFolder(text);
                }
            }
        });
        dialog.show();
    }

    void doCreateFolder(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        String name = text.trim();
        if (TextUtils.isEmpty(name)) {
            return;
        }

        // 判断文件是否存在
        CatalogManager dm = CatalogManager.instance();
        boolean exist = (dm.find(name) != null);
        if (exist) {
            Context context = this.getActivity();
            CharSequence title = getString(R.string.catalog_folder_exist_title);
            CharSequence msg = getString(R.string.catalog_folder_exist_msg);
            CharSequence btn = getString(R.string.btn_good);

            MessageDialog.show(context, title, msg, btn);
            return;
        }

        // 创建文件
        CatalogEntry entry = CatalogManager.instance().create(name);
        if (entry == null) { // 创建失败
            Context context = this.getActivity();
            CharSequence title = getString(R.string.catalog_folder_fail_title);
            CharSequence msg = getString(R.string.catalog_folder_fail_msg);
            CharSequence btn = getString(R.string.btn_good);

            MessageDialog.show(context, title, msg, btn);
            return;
        }

        // 保存
        CatalogManager.instance().save();

        // 更新界面
        mList.add(entry);

        // 判断是否可以编辑
        this.mEditBtn.setEnabled(!CatalogManager.instance().isEmpty());
    }

    void requestDelete() {

        CatalogManager mgr = CatalogManager.instance();

        int count = 0;
        List<CatalogEntry> list = mCheckedMap.getList();
        for (CatalogEntry e : list) {
            count += mgr.getCount(e);
        }

        if (count == 0) {

            this.doDelete(DELETE_EMPTY);

        } else {

            Context context = this.getActivity();
            CharSequence title = "删除文件夹？";
            CharSequence msg = "如果仅删除这些文件夹，其笔记将移至“备忘录”文件夹。子文件夹也将同时删除。";
            if (list.size() == 1) {
                msg = "如果仅删除这一文件夹，其笔记将移至“备忘录”文件夹。子文件夹也将同时删除。";
            }
            CharSequence btn1 = "删除文件夹和笔记";
            CharSequence btn2 = "仅删除文件夹";
            CharSequence btnCancel = "取消";
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == DialogInterface.BUTTON_POSITIVE) {

                        doDelete(DELETE_ALL);

                    } else if (which == DialogInterface.BUTTON_NEUTRAL) {

                        doDelete(DELETE_ONLY);

                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                    }
                }
            };

            ChoiceDialog.showThreeChoice(context, title, msg,
                    btn1, btn2, btnCancel,
                    listener);
        }

    }

    void doDelete(final int mode) {

        this.closeEditMode();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doDeleteRunnable(mode);
            }
        });
    }

    void doDeleteRunnable(final int mode) {

        CatalogManager mgr = CatalogManager.instance();

        Iterator<CatalogEntry> iterator= mCheckedMap.values().iterator();
        while (iterator.hasNext()) {
            CatalogEntry entry = iterator.next();

            mgr.trash(entry);
            mList.remove(entry);

            if (mode != DELETE_EMPTY) {
                List<NoteEntry> list = NoteManager.instance().getList(entry.getId());
                if (mode == DELETE_ALL) { // 同时删除note
                    for (NoteEntry e : list) {
                        NoteManager.instance().trash(e);
                    }
                } else if (mode == DELETE_ONLY) { // note移动到备忘录
                    for (NoteEntry e : list) {
                        e.setCatalog(mgr.getDefault().getId());
                    }
                }
            }
        }

        // 更新内置的Catalog，所有、备忘录、最近删除
        {
            CatalogEntry entry = mgr.getAllEntry();
            int pos = mList.indexOf(entry);
            if (pos >= 0) {
                int count = NoteManager.instance().getCount();
                if (count == 0) {
                    mList.removeItemAt(pos);
                } else {
                    mAdapter.notifyItemChanged(pos);
                }
            }
        }
        {
            CatalogEntry entry = mgr.getTrashEntry();
            int pos = mList.indexOf(entry);
            if (pos < 0) {
                int count = NoteManager.instance().getTrashCount();
                if (count != 0) {
                    mList.add(entry);
                }
            } else {
                mAdapter.notifyItemChanged(pos);
            }
        }
        {
            CatalogEntry entry = mgr.getDefault();
            int pos = mList.indexOf(entry);
            if (pos >= 0) {
                mAdapter.notifyItemChanged(pos);
            }
        }

        // 存档
        mgr.save();

        if (mode != DELETE_EMPTY) {
            NoteManager.instance().save();
        }

        // 设置可编辑
        mEditBtn.setEnabled(!CatalogManager.instance().isEmpty());
    }

    void requestRename(final CatalogEntry entry) {
        Context context = this.getActivity();
        String title = getString(R.string.catalog_rename_folder_title);
        String msg = getString(R.string.catalog_rename_folder_msg);
        String text = entry.getName();
        String hint = getString(R.string.catalog_name_hint);

        InputDialog dialog = InputDialog.create(context, title, msg, text, hint, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    InputDialog inputDialog = (InputDialog)dialog;
                    String text = inputDialog.getText();

                    doRename(entry.getId(), text);
                }
            }
        });
        dialog.show();
    }

    void doRename(final String catalogId, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        CatalogEntry entry = CatalogManager.instance().obtain(catalogId);

        String name = text.trim();
        if (TextUtils.isEmpty(name)) {
            return;
        }

        if (entry.getName().compareTo(name) == 0) {
            return;
        }

        // 判断文件是否存在
        CatalogManager dm = CatalogManager.instance();
        boolean exist = (dm.find(name) != null);
        if (exist) {
            Context context = this.getActivity();
            CharSequence title = getString(R.string.catalog_folder_exist_title);
            CharSequence msg = getString(R.string.catalog_folder_exist_msg);
            CharSequence btn = getString(R.string.btn_good);

            MessageDialog.show(context, title, msg, btn);
            return;
        }

        int index = mList.indexOf(entry);

        // 修改名称
        entry.setName(name);

        // 保存
        CatalogManager.instance().save();

        // 更新界面
        if (index >= 0) {
            mList.updateItemAt(index, entry);
        }

    }

    void setEditMode(boolean value, boolean notify) {
        if (!(mEditMode ^ value)) {
            return;
        }

        this.mEditMode = value;

        if (value) {

            mTopNormalBar.setVisibility(View.INVISIBLE);
            mTopEditBar.setVisibility(View.VISIBLE);

            mEditTitleView.setText(R.string.catalog_title);

            mDeleteBtn.setText(R.string.btn_delete);
            mDeleteBtn.setEnabled(false);

            mCheckedMap.clear();

        } else {

            mTopNormalBar.setVisibility(View.VISIBLE);
            mTopEditBar.setVisibility(View.INVISIBLE);

            // 判断是否可以编辑
            this.mEditBtn.setEnabled(!CatalogManager.instance().isEmpty());
        }

        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
    }

    void closeEditMode() {
        this.setEditMode(false, false);

        mAdapter.notifyDataSetChanged();
    }

    void onItemCheckedChanged(int position, CatalogEntry entry, boolean isChecked) {
        int count = mCheckedMap.size();

        mDeleteBtn.setEnabled(count != 0);

        if (count == 0) {
            mEditTitleView.setText(R.string.catalog_title);
        } else {
            String str = getString(R.string.catalog_checked_title_fmt, count);
            mEditTitleView.setText(str);
        }

    }

    /**
     *
     */
    private class CatalogSortedListAdapterCallback extends SortedListAdapterCallback<CatalogEntry> {

        Collator mCollator;

        public CatalogSortedListAdapterCallback(RecyclerView.Adapter adapter) {
            super(adapter);

            this.mCollator = Collator.getInstance(Locale.CHINA);
        }

        @Override
        public int compare(CatalogEntry o1, CatalogEntry o2) {
            int result = 0;

            int s1 = o1.getSort();
            int s2 = o2.getSort();
            if (s1 == s2) {
                result = mCollator.compare(o1.getName(), o2.getName());
            } else {
                result = s1 - s2;
            }

            return result;
        }

        @Override
        public boolean areContentsTheSame(CatalogEntry oldItem, CatalogEntry newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areItemsTheSame(CatalogEntry item1, CatalogEntry item2) {
            return item1.getId().equals(item2.getId());
        }

    }

    /**
     *
     */
    private class CatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        LayoutInflater mInflater;

        CatalogAdapter(Activity context) {

            this.mInflater = context.getLayoutInflater();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return ItemHolder.create(CatalogFragment.this, mInflater, parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CatalogEntry entry = mList.get(position);

            ItemHolder itemHolder = (ItemHolder)holder;
            itemHolder.bind(position, entry);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    /**
     *
     */
    private static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CheckBox.OnCheckedChangeListener {

        CheckBox mCheckBox;
        TextView mNameView;
        TextView mNumView;
        View mArrowView;

        int mPosition;
        CatalogEntry mEntry;

        CatalogFragment mFragment;

        static final ItemHolder create(CatalogFragment fragment, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_catalog_item;
            View view = inflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(fragment, view);
            return holder;
        }

        public ItemHolder(CatalogFragment fragment, View itemView) {
            super(itemView);
            this.mFragment = fragment;

            this.mCheckBox = (CheckBox)(itemView.findViewById(R.id.cb_check));
            this.mNameView = (TextView)(itemView.findViewById(R.id.tv_name));
            mNameView.setOnClickListener(this);

            this.mNumView = (TextView)(itemView.findViewById(R.id.tv_num));
            this.mArrowView = itemView.findViewById(R.id.iv_arrow);

            itemView.setOnClickListener(this);
        }

        void bind(int position, CatalogEntry entry) {
            this.mPosition = position;
            this.mEntry = entry;

            String name = entry.getName();
            mNameView.setText(name);

            int num = CatalogManager.instance().getCount(entry);
            mNumView.setText(String.valueOf(num));

            this.setEditMode(mFragment.mEditMode);
        }

        void setEditMode(boolean value) {
            CatalogEntry entry = this.mEntry;
            boolean editable = entry.isEditable();

            if (value) {
                itemView.setClickable(true);
                itemView.setSelected(mFragment.mCheckedMap.isChecked(entry.getId()));

                mCheckBox.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(mFragment.mCheckedMap.isChecked(entry.getId()));
                mCheckBox.setOnCheckedChangeListener(this);

                mNameView.setEnabled(true);
                mNameView.setClickable(true);

                // 不可编辑
                if (!editable) {
                    itemView.setClickable(false);           // 不可编辑

                    mCheckBox.setVisibility(View.GONE);

                    mNameView.setEnabled(false);
                    mNameView.setClickable(false);

                    mNumView.setEnabled(false);
                } else {
                    mNumView.setEnabled(true);
                }

                mArrowView.setVisibility(View.GONE);

            } else {
                itemView.setClickable(true);
                itemView.setSelected(false);

                mCheckBox.setVisibility(View.GONE);
                mCheckBox.setOnCheckedChangeListener(null);

                mNameView.setEnabled(true);
                mNameView.setClickable(false);

                mNumView.setEnabled(true);

                mArrowView.setVisibility(View.VISIBLE);

            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                if (mFragment.mEditMode) {
                    mCheckBox.setChecked(!mCheckBox.isChecked());

                } else {
                    if (CatalogManager.instance().isTrash(mEntry)) {
                        TrashActivity.start(mFragment, REQUEST_TRASH);
                    } else {
                        String catalogId = mEntry.getId();
                        NoteActivity.start(mFragment, REQUEST_NOTE, catalogId);
                    }
                }
            } else if (v == mNameView) {
                if (mFragment.mEditMode) {
                    mFragment.requestRename(mEntry);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == mCheckBox) {
                mFragment.mCheckedMap.setChecked(mEntry.getId(), mEntry, isChecked);
                itemView.setSelected(isChecked);

                mFragment.onItemCheckedChanged(mPosition, mEntry, isChecked);
            }
        }
    }

}
