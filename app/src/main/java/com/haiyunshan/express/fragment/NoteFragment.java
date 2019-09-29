package com.haiyunshan.express.fragment;

import android.app.Activity;
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

import com.haiyunshan.express.MoveToActivity;
import com.haiyunshan.express.NoteComposeActivity;
import com.haiyunshan.express.decoration.MarginDividerDecoration;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.R;
import com.haiyunshan.express.app.CheckedMap;
import com.haiyunshan.express.app.Utils;
import com.haiyunshan.express.dataset.catalog.CatalogEntry;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.dataset.template.TemplateEntry;
import com.haiyunshan.express.dataset.template.TemplateManager;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NoteFragment extends BaseFragment implements View.OnClickListener {

    public static final int REQUEST_COMPOSE = 1003;
    public static final int REQUEST_MOVE    = 1004;

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

    View mPopupBtn;
    View mCopyBtn;
    View mFolderBtn;
    View mTrashBtn;

    CatalogEntry mCatalog;

    boolean mEditMode = false;

    Handler mHandler;

    public NoteFragment() {
        this.mHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
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

            this.mEditBtn = view.findViewById(R.id.btn_edit);
            mEditBtn.setOnClickListener(this);

        }

        {
            this.mSelectBtn = (TextView)view.findViewById(R.id.btn_select);
            mSelectBtn.setOnClickListener(this);

            this.mEditTitleView = (TextView)(view.findViewById(R.id.tv_edit_title));

            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);
        }

        {
            this.mPopupBtn = view.findViewById(R.id.btn_popup);
            mPopupBtn.setOnClickListener(this);

            this.mCopyBtn = view.findViewById(R.id.btn_copy);
            mCopyBtn.setOnClickListener(this);

            this.mFolderBtn = view.findViewById(R.id.btn_folder);
            mFolderBtn.setOnClickListener(this);

            this.mTrashBtn = view.findViewById(R.id.btn_trash);
            mTrashBtn.setOnClickListener(this);

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String catalogId = args == null? "": args.getString(argCatalog);
            this.mCatalog = CatalogManager.instance().obtain(catalogId);
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

        if (requestCode == REQUEST_COMPOSE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String noteId = data.getStringExtra(NoteComposeFragment.argNoteId);
                if (!TextUtils.isEmpty(noteId)) {

                    // 更新
                    int pos = mAdapter.indexOf(noteId);
                    if (pos >= 0) {
                        mAdapter.notifyItemChanged(pos);
                    } else {
                        NoteEntry entry = NoteManager.instance().obtain(noteId);
                        if (entry != null) {
                            mList.add(entry);
                        }
                    }

                    // 添加
                    List<NoteEntry> list = this.getList(mCatalog);
                    for (NoteEntry e : list) {
                        if (mList.indexOf(e) < 0) {
                            mList.add(e);
                        }
                    }
                }

                // 更新UI
                this.updateUI();
            }
        } else if (requestCode == REQUEST_MOVE) {
            if (resultCode == Activity.RESULT_OK) {

                if (data == null) {
                    this.setEditMode(false, true);
                } else {

                    String catalogId = data.getStringExtra("catalogId");
                    if (TextUtils.isEmpty(catalogId)) {
                        this.setEditMode(false, true);
                    } else {
                        this.doMove(catalogId);
                    }

                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        } else if (v == mFolderBtn) {
            this.requestMove();
        } else if (v == mTrashBtn) {
            this.requestTrash();
        } else if (v == mDoneBtn) {
            this.setEditMode(false, true);
        } else if (v == mCopyBtn) {
            this.requestCopy();
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
            mSelectBtn.setVisibility(isEmpty? View.INVISIBLE: View.VISIBLE);
            mSelectBtn.setText("全选");

            boolean enable = !mCheckedMap.isEmpty();
            mPopupBtn.setEnabled(enable);
            mCopyBtn.setEnabled(enable);
            mFolderBtn.setEnabled(enable);
            mTrashBtn.setEnabled(enable);

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
        String name = (count == 0)? "选择项目": count + " 项";
        mEditTitleView.setText(name);

        name = (count == mList.size())? "取消全选": "全选";
        mSelectBtn.setText(name);

        boolean enable = !mCheckedMap.isEmpty();
        mPopupBtn.setEnabled(enable);
        mCopyBtn.setEnabled(enable);
        mFolderBtn.setEnabled(enable);
        mTrashBtn.setEnabled(enable);

        // FIXME 暂不开启更多功能
        mPopupBtn.setEnabled(false);
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

    void requestCopy() {
        this.closeEditMode();

        if (mCheckedMap.isEmpty()) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doCopyRunnable();
            }
        });
    }

    void doCopyRunnable() {
        NoteManager mgr = NoteManager.instance();

        ArrayList<NoteEntry> list = new ArrayList<>(mCheckedMap.size());

        int size = mList.size();
        for (int i = size - 1; i >= 0; i--) {
            NoteEntry e = mList.get(i);

            if (mCheckedMap.isChecked(e)) {
                list.add(e);
            }
        }

        for (NoteEntry e : list) {

            NoteEntry dest = mgr.copy(e);
            if (dest != null) {
                mList.add(dest);
            }
        }

        // 保存
        mgr.save();

        // 更新UI
        this.updateUI();
    }

    void requestMove() {
        int count = mCheckedMap.size();
        if (count == 0) {
            return;
        }

        ArrayList<String> idList = new ArrayList<>(count);
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            NoteEntry e = mList.get(i);
            if (mCheckedMap.isChecked(e.getId())) {
                idList.add(e.getId());
            }
        }

        MoveToActivity.startForResult(this, REQUEST_MOVE, this.mCatalog.getId(), idList);
    }

    void doMove(final String catalogId) {
        this.setEditMode(false, true);

        if (CatalogManager.instance().isAll(mCatalog)) {

            List<NoteEntry> list = mCheckedMap.getList();
            for (NoteEntry e : list) {
                e.setCatalog(catalogId);
            }

            NoteManager.instance().save();

        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    doMoveRunnable(catalogId);
                }
            });

        }
    }

    void doMoveRunnable(String catalogId) {
        List<NoteEntry> list = mCheckedMap.getList();
        for (NoteEntry e : list) {
            e.setCatalog(catalogId);

            mList.remove(e);
        }

        NoteManager.instance().save();

        this.updateUI();
    }

    void requestCreate() {
        CatalogManager mgr = CatalogManager.instance();
        CatalogEntry folder = this.mCatalog;
        if (mgr.isAll(folder)) {
            folder = mgr.getDefault();
        }

        {
            TemplateEntry template = TemplateManager.instance().getDefault();
            NoteEntry entry = NoteManager.instance().create(folder, template.getName());

            TemplateManager.instance().createNote(entry.getId(), template);

            NoteComposeActivity.startForCompose(this, REQUEST_COMPOSE, entry.getId());
        }

        // 保存
        NoteManager.instance().save();
    }

    void requestTrash() {

        // 关闭编辑模式
        this.setEditMode(false, true);
        
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doTrashRunnable();
            }
        });
    }

    void doTrashRunnable() {

        NoteManager mgr = NoteManager.instance();
        SortedList<NoteEntry> array = this.mList;
        int size = array.size();
        for (int i = size - 1; i >= 0; i--) {
            NoteEntry entry = array.get(i);

            if (mCheckedMap.isChecked(entry.getId())) {
                mList.remove(entry);

                mgr.trash(entry);
            }
        }

        // 存档
        NoteManager.instance().save();

        // 更新引导
        this.updateUI();

    }

    void updateUI() {
        if (mAdapter.getHeaderCount() == mAdapter.getItemCount()) {
            mDividerDecor.setEnable(false);
        } else {
            mDividerDecor.setEnable(true);
        }

        boolean enable = CatalogManager.instance().getCount(this.mCatalog) != 0;
        mPopupBtn.setEnabled(enable);
        mCopyBtn.setEnabled(enable);
        mFolderBtn.setEnabled(enable);
        mTrashBtn.setEnabled(enable);

        // FIXME 暂不开启更多功能
        mPopupBtn.setEnabled(false);
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
    private class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        static final int TYPE_HEADER_CREATE = 1001;
        static final int TYPE_ITEM = 2001;

        int[] mHeader;

        LayoutInflater mInflater;

        NoteAdapter(Activity context) {
            this.mInflater = context.getLayoutInflater();

            this.mHeader = new int[] {
                TYPE_HEADER_CREATE
            };
        }

        public int getHeaderCount() {
            if (mHeader == null) {
                return 0;
            }

            return mHeader.length;
        }

        public int indexOf(String id) {
            int size = mList.size();
            for (int i = 0; i < size; i++) {
                NoteEntry e = mList.get(i);
                if (e.getId().equalsIgnoreCase(id)) {
                    return i + this.getHeaderCount();
                }
            }

            return -1;
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
            if (viewType == TYPE_HEADER_CREATE) {
                holder = CreateHolder.create(NoteFragment.this, mInflater, parent);
            } else if (viewType == TYPE_ITEM) {
                holder = ItemHolder.create(NoteFragment.this, mInflater, parent);
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position < getHeaderCount()) {
                int viewType = holder.getItemViewType();
                if (viewType == TYPE_HEADER_CREATE) {
                    ((CreateHolder)holder).bind(position);
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
            int size = getHeaderCount();
            size += mList.size();

            return size;
        }
    }

    /**
     *
     */
    private static class CreateHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mCheckBox;
        View mIconView;
        View mNameView;

        NoteFragment mFragment;

        static CreateHolder create(NoteFragment fragment, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_create_note_header_item;
            View view = inflater.inflate(resource, parent, false);

            CreateHolder holder = new CreateHolder(fragment, view);
            return holder;
        }

        public CreateHolder(NoteFragment fragment, View itemView) {
            super(itemView);
            this.mFragment = fragment;

            this.mCheckBox = itemView.findViewById(R.id.cb_check);
            this.mIconView = itemView.findViewById(R.id.iv_icon);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            if (mFragment.mEditMode) {
                mCheckBox.setVisibility(View.INVISIBLE);
                mIconView.setEnabled(false);
                mNameView.setEnabled(false);

                itemView.setClickable(false);
            } else {
                mCheckBox.setVisibility(View.GONE);
                mIconView.setEnabled(true);
                mNameView.setEnabled(true);

                itemView.setClickable(true);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                mFragment.requestCreate();
            }
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

        NoteFragment mFragment;

        static ItemHolder create(NoteFragment fragment, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_note_item;
            View view = inflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(fragment, view);
            return holder;
        }

        public ItemHolder(NoteFragment fragment, View itemView) {
            super(itemView);
            this.mFragment = fragment;

            this.mCheckBox = (CheckBox)(itemView.findViewById(R.id.cb_check));
            this.mNameView = (TextView)(itemView.findViewById(R.id.tv_name));
            this.mDayView = (TextView)(itemView.findViewById(R.id.tv_day));
            this.mDescView = (TextView)(itemView.findViewById(R.id.tv_desc));
            this.mCatalogView = (TextView) (itemView.findViewById(R.id.tv_catalog));
            if (!CatalogManager.instance().isAll(fragment.mCatalog)) {
                mCatalogView.setVisibility(View.GONE);
            }

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

            if (mCatalogView.getVisibility() == View.VISIBLE) {
                CatalogEntry e = CatalogManager.instance().obtain(entry.getCatalog());
                if (e == null) {
                    e = CatalogManager.instance().getDefault();
                }

                mCatalogView.setText(e.getName());
            }

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
                    NoteComposeActivity.startForCompose(mFragment, REQUEST_COMPOSE, mEntry.getId());
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
    }

}
