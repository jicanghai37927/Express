package com.haiyunshan.express;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

import com.haiyunshan.express.decoration.MarginDividerDecoration;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.dialog.ConfirmDialog;
import com.haiyunshan.express.dialog.InputDialog;
import com.haiyunshan.express.dialog.PopupDeleteDialog;
import com.haiyunshan.express.typeface.TypefaceDataset;
import com.haiyunshan.express.typeface.TypefaceEntry;
import com.haiyunshan.express.typeface.TypefaceManager;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class TypefaceActivity extends BaseActivity implements View.OnClickListener {

    static final int REQUEST_ADD = 1003;
    static final int REQUEST_PREVIEW = 1004;

    View mTopNormalBar;
    View mBackBtn;
    View mEditBtn;

    View mTopEditBar;
    TextView mEditTitleView;
    View mDoneBtn;

    View mBottomNormalBar;
    View mMoreBtn;

    View mBottomEditBar;
    TextView mDeleteBtn;

    RecyclerView mRecyclerView;
    FontAdapter mAdapter;
    SortedList<TypefaceEntry> mList;

    boolean mEditMode = false;

    public static final void start(Fragment f) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, TypefaceActivity.class);
        f.startActivity(intent);
    }

    public static final void start(Activity f) {
        Activity context = f;

        Intent intent = new Intent(context, TypefaceActivity.class);
        f.startActivity(intent);

        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typeface);

        onViewCreated(findViewById(R.id.root_container), savedInstanceState);
        onActivityCreated(savedInstanceState);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        this.mTopNormalBar = view.findViewById(R.id.top_normal_bar);

        this.mBackBtn = view.findViewById(R.id.btn_back);
        mBackBtn.setOnClickListener(this);

        this.mEditBtn = view.findViewById(R.id.btn_edit);
        mEditBtn.setOnClickListener(this);

        this.mTopEditBar = view.findViewById(R.id.top_edit_bar);
        this.mEditTitleView = (TextView)view.findViewById(R.id.tv_edit_title);
        this.mDoneBtn = view.findViewById(R.id.btn_done);
        mDoneBtn.setOnClickListener(this);

        this.mBottomNormalBar = view.findViewById(R.id.bottom_normal_bar);

        this.mMoreBtn = view.findViewById(R.id.btn_more);
        mMoreBtn.setOnClickListener(this);

        this.mBottomEditBar = view.findViewById(R.id.bottom_edit_bar);
        this.mDeleteBtn = (TextView)view.findViewById(R.id.btn_delete);
        mDeleteBtn.setOnClickListener(this);

        this.mRecyclerView = (RecyclerView)(view.findViewById(R.id.rv_list));
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        {
            this.mAdapter = new FontAdapter(this);
            FontSortedListAdapterCallback callback = new FontSortedListAdapterCallback(mAdapter);
            this.mList = new SortedList<>(TypefaceEntry.class, callback);
            mRecyclerView.setAdapter(mAdapter);

            // item decor

            {
                MarginDividerDecoration decor = new MarginDividerDecoration(this);
                int margin = getResources().getDimensionPixelSize(R.dimen.inset_4l);
                margin = 2 * margin;
                decor.setMargin(0, margin);
                mRecyclerView.addItemDecoration(decor);
            }

            {
                SimpleDividerDecoration decor = new SimpleDividerDecoration(this);
                decor.setMargin(getResources().getDimensionPixelSize(R.dimen.inset_2l), 0);
                mRecyclerView.addItemDecoration(decor);
            }
        }

        {
            TypefaceDataset ds = TypefaceManager.instance().getAsset();
            List<TypefaceEntry> list = ds.getList();
            mList.addAll(list);

            ds = TypefaceManager.instance().getDataset();
            list = ds.getList();
            mList.addAll(list);

        }

        {
            this.mEditBtn.setEnabled(!TypefaceManager.instance().getDataset().isEmpty());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                TypefaceDataset ds = TypefaceManager.instance().getDataset();
                List<TypefaceEntry> list = ds.getList();
                for (TypefaceEntry e : list) {
                    if (mList.indexOf(e) < 0) {
                        mList.add(e);
                    }
                }

                ds = TypefaceManager.instance().getDataset();
                list = ds.getList();
                for (TypefaceEntry e : list) {
                    if (mList.indexOf(e) < 0) {
                        mList.add(e);
                    }
                }

                //
                mEditBtn.setEnabled(!ds.isEmpty());
            }
        } else if (requestCode == REQUEST_PREVIEW) {
            if (resultCode == RESULT_OK && data != null) {
                String id = data.getStringExtra("id");
                if (!TextUtils.isEmpty(id)) {
                    TypefaceEntry e = TypefaceManager.instance().obtain(id);
                    int index = mAdapter.indexOf(id);
                    if (index >= 0 && (e == null)) {
                        mList.removeItemAt(index);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.onBackPressed();
        } else if (v == mMoreBtn) {
            LocalTypefaceActivity.startForResult(this, REQUEST_ADD);
        } else if (v == mEditBtn) {
            this.setEditMode(true, true);
        } else if (v == mDoneBtn) {
            this.setEditMode(false, true);
        } else if (v == mDeleteBtn) {
            this.requestDelete();
        }
    }

    void requestDelete() {
        CharSequence btn = null;
        int count = mAdapter.getCheckedCount();
        if (0 == count) {
            btn = getString(R.string.btn_delete_all_items);
        } else {
            btn = getString(R.string.btn_delete_fmt, count);
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    showDeleteConfirm();
                }
            }
        };

        PopupDeleteDialog dialog = PopupDeleteDialog.create(this, false);
        dialog.setButton(btn, listener);
        dialog.show();
    }

    void showDeleteConfirm() {
        Context context = this;
        CharSequence title = "删除字体可能导致一些笔记使用默认字体显示";
        CharSequence msg = "";
        CharSequence cancelBtn = "取消";
        CharSequence confirmBtn = "删除";
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    doDelete();
                }
            }
        };

        ConfirmDialog.showWarning(context, title, msg, cancelBtn, confirmBtn, listener);
    }

    void doDelete() {

        //
        this.closeEditMode();

        //
        TypefaceManager mgr = TypefaceManager.instance();
        TypefaceDataset ds = mgr.getDataset();

        if (mAdapter.getCheckedCount() == 0) {

            // 删除数据文件
            List<TypefaceEntry> list = ds.getList();
            for (TypefaceEntry e : list) {
                mgr.removeFontFile(e);
            }
            ds.clear();

            //
            mList.clear();
            mList.addAll(getList());

            mRecyclerView.scrollToPosition(0);

        } else {

            Iterator<TypefaceEntry> iterator= mAdapter.mCheckedMap.values().iterator();
            while (iterator.hasNext()) {
                TypefaceEntry entry = iterator.next();

                // 删除数据文件
                mgr.removeFontFile(entry);
                ds.remove(entry);

                mList.remove(entry);
            }
        }

        // 存档
        mgr.save();

    }

    void requestRename(final String id) {
        TypefaceEntry entry = TypefaceManager.instance().obtain(id);

        Context context = this;
        String title = "给字体重新命名";
        String msg = "请为此字体输入新名称。";
        String text = entry.getAlias();
        String hint = "名称";

        InputDialog dialog = InputDialog.create(context, title, msg, text, hint, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    InputDialog inputDialog = (InputDialog)dialog;
                    String text = inputDialog.getText();

                    doRename(id, text);
                }
            }
        });
        dialog.show();
    }

    void doRename(String id, String text) {
        TypefaceEntry entry = TypefaceManager.instance().obtain(id);

        if (TextUtils.isEmpty(text)) {
            return;
        }

        String name = text.trim();
        if (TextUtils.isEmpty(name)) {
            return;
        }

        if (entry.getAlias().compareTo(name) == 0) {
            return;
        }

        // 必须先检索位置，然后再更新数据
        int index = mList.indexOf(entry);

        // 修改名称
        entry.setAlias(name);

        // 保存
        TypefaceManager.instance().save();

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

            mEditTitleView.setText("字体");

            mBottomNormalBar.setVisibility(View.INVISIBLE);
            mBottomEditBar.setVisibility(View.VISIBLE);

            mDeleteBtn.setText(R.string.btn_delete_all);
            mDeleteBtn.setEnabled(!TypefaceManager.instance().getDataset().isEmpty());

            mAdapter.clearChecked();

        } else {
            mTopNormalBar.setVisibility(View.VISIBLE);
            mTopEditBar.setVisibility(View.INVISIBLE);

            mBottomNormalBar.setVisibility(View.VISIBLE);
            mBottomEditBar.setVisibility(View.INVISIBLE);

            // 判断是否可以编辑
            this.mEditBtn.setEnabled(!TypefaceManager.instance().getDataset().isEmpty());
        }

        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
    }

    void closeEditMode() {
        this.setEditMode(false, false);

        if (true) {
            if (mAdapter.getCheckedCount() != 0) {
                int size = mAdapter.getItemCount();
                for (int i = 0; i < size; i++) {
                    TypefaceEntry entry = mList.get(i);
                    if (!mAdapter.isChecked(entry)) {
                        mAdapter.notifyItemChanged(i);
                    }
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

        return list;
    }

    void onItemCheckedChanged(int position, TypefaceEntry entry, boolean isChecked) {
        int count = mAdapter.getCheckedCount();
        if (count == 0) {
            mEditTitleView.setText("字体");
            mDeleteBtn.setText(R.string.btn_delete_all);
        } else {
            String str = String.format(Locale.CHINA, "已选定 %1$d 个项", count);
            mEditTitleView.setText(str);
            mDeleteBtn.setText(R.string.btn_delete);
        }
    }

    /**
     *
     */
    private class FontSortedListAdapterCallback extends SortedListAdapterCallback<TypefaceEntry> {

        Collator mCollator;

        /**
         * Creates a {@link SortedList.Callback} that will forward data change events to the provided
         * Adapter.
         *
         * @param adapter The Adapter instance which should receive events from the SortedList.
         */
        public FontSortedListAdapterCallback(RecyclerView.Adapter adapter) {
            super(adapter);

            this.mCollator = Collator.getInstance(Locale.CHINA);
        }

        @Override
        public int compare(TypefaceEntry o1, TypefaceEntry o2) {
            int r = 0;

            int t1 = o1.getType();
            int t2 = o2.getType();
            if (t1 != t2) {
                r = t2 - t1;
            } else {
                r = mCollator.compare(o1.getAlias(), o2.getAlias());
            }

            return r;
        }

        @Override
        public boolean areContentsTheSame(TypefaceEntry oldItem, TypefaceEntry newItem) {
            String d1 = oldItem.getAlias();
            String d2 = newItem.getAlias();

            boolean r = d1.equals(d2);
            return r;
        }

        @Override
        public boolean areItemsTheSame(TypefaceEntry item1, TypefaceEntry item2) {
            String id1 = item1.getId();
            String id2 = item2.getId();

            boolean r = id1.equals(id2);
            return r;
        }
    }

    /**
     *
     */
    private class FontAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        HashMap<String, TypefaceEntry> mCheckedMap;

        LayoutInflater mInflater;

        FontAdapter(Activity context) {
            this.mInflater = context.getLayoutInflater();

            this.mCheckedMap = new HashMap<>();
        }

        int indexOf(String id) {
            int size = mList.size();
            for (int i = 0; i < size; i++) {
                TypefaceEntry e = mList.get(i);
                if (e.getId().equalsIgnoreCase(id)) {
                    return i;
                }
            }

            return -1;
        }
        void clearChecked() {
            mCheckedMap.clear();
        }

        void setChecked(TypefaceEntry entry, boolean checked) {
            if (checked) {
                mCheckedMap.put(entry.getId(), entry);
            } else {
                mCheckedMap.remove(entry.getId());
            }
        }

        boolean isChecked(String id) {
            return (mCheckedMap.containsKey(id));
        }

        boolean isChecked(TypefaceEntry entry) {
            return mCheckedMap.containsValue(entry);
        }

        int getCheckedCount() {
            return mCheckedMap.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int resource = R.layout.layout_font_item;
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
    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CheckBox.OnCheckedChangeListener {

        CheckBox mCheckBox;
        TextView mNameView;
        View mArrowView;

        int mPosition;
        TypefaceEntry mEntry;

        public ItemHolder(View itemView) {
            super(itemView);

            this.mCheckBox = (CheckBox)(itemView.findViewById(R.id.cb_check));
            this.mNameView = (TextView)(itemView.findViewById(R.id.tv_name));
            mNameView.setOnClickListener(this);

            this.mArrowView = itemView.findViewById(R.id.iv_arrow);

            itemView.setOnClickListener(this);
        }

        void bind(int position, TypefaceEntry entry) {
            this.mPosition = position;
            this.mEntry = entry;

            mArrowView.setVisibility(mEditMode? View.INVISIBLE: View.VISIBLE);

            String name = entry.getAlias();
            Typeface font = TypefaceManager.instance().getTypeface(entry, false, false);

            mNameView.setText(name);
            mNameView.setTypeface(font);

            boolean editable = (TypefaceManager.instance().isEditable(entry));

            if (mEditMode) {
                itemView.setClickable(true);
                itemView.setSelected(mAdapter.isChecked(entry.getId()));

                mCheckBox.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(mAdapter.isChecked(entry.getId()));
                mCheckBox.setOnCheckedChangeListener(this);

                mNameView.setEnabled(true);
                mNameView.setClickable(true);

                // 不可编辑
                if (!editable) {
                    itemView.setClickable(false);           // 不可编辑

                    mCheckBox.setVisibility(View.GONE);

                    mNameView.setEnabled(false);
                    mNameView.setClickable(false);
                }
            } else {
                itemView.setClickable(true);
                itemView.setSelected(false);

                mCheckBox.setVisibility(View.GONE);
                mCheckBox.setOnCheckedChangeListener(null);

                mNameView.setEnabled(true);
                mNameView.setClickable(false);
            }
        }

        @Override
        public void onClick(View v) {
            if (itemView == v) {
                if (mEditMode) {

                    mCheckBox.setChecked(!mCheckBox.isChecked());

                } else {
                    TypefacePreviewActivity.startForResult(TypefaceActivity.this, REQUEST_PREVIEW, mEntry.getId());
                }
            } else if (v == mNameView) {
                requestRename(mEntry.getId());
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == mCheckBox) {
                mAdapter.setChecked(mEntry, isChecked);
                itemView.setSelected(isChecked);

                onItemCheckedChanged(mPosition, mEntry, isChecked);
            }
        }
    }
}
