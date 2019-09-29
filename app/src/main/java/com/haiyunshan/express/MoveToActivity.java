package com.haiyunshan.express;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiyunshan.express.dataset.catalog.CatalogEntry;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.decoration.MarginDividerDecoration;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.dialog.InputDialog;
import com.haiyunshan.express.dialog.MessageDialog;
import com.haiyunshan.express.fragment.NoteFragment;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MoveToActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    CatalogAdapter mAdapter;
    SortedList<CatalogEntry> mList;

    View mCancelBtn;

    ImageView mIconView;
    TextView mNameView;
    TextView mCountView;

    CatalogEntry mCatalog;

    Intent mResultIntent;

    public static final void startForResult(Fragment fragment, int requestCode, String catalogId, ArrayList<String> idArray) {
        Intent intent = new Intent(fragment.getActivity(), MoveToActivity.class);
        intent.putExtra("catalogId", catalogId);
        intent.putStringArrayListExtra("idArray", idArray);

        fragment.startActivityForResult(intent, requestCode);
        fragment.getActivity().overridePendingTransition(R.anim.push_in_up, R.anim.standby);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_to);

        {
            this.mRecyclerView = (RecyclerView)(findViewById(R.id.rv_list));
            LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mCancelBtn = findViewById(R.id.btn_cancel);
            mCancelBtn.setOnClickListener(this);

            mIconView = (ImageView)(findViewById(R.id.iv_note_icon));
            this.mNameView = (TextView)(findViewById(R.id.tv_note_name));
            this.mCountView = (TextView)(findViewById(R.id.tv_note_count));
        }

        {
            Intent intent = this.getIntent();
            String catalogId = intent.getStringExtra("catalogId");
            this.mCatalog = CatalogManager.instance().obtain(catalogId, false);

            ArrayList<String> idArray = intent.getStringArrayListExtra("idArray");

            {
                int iconResId = R.drawable.ic_note_group1;
                if (idArray != null) {
                    iconResId = (idArray.size() == 2) ? R.drawable.ic_note_group2 : iconResId;
                    iconResId = (idArray.size() >= 3) ? R.drawable.ic_note_group3 : iconResId;
                }

                mIconView.setImageResource(iconResId);
            }

            {
                int size = (idArray == null) ? 0 : idArray.size();
                String text = String.format("%1$d 个笔记", size);
                mCountView.setText(text);
            }


            mNameView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateNameView();
                }
            }, 10);

        }

        {

            CatalogAdapter adapter = new CatalogAdapter(this);
            this.mAdapter = adapter;
            CatalogSortedListAdapterCallback callback = new CatalogSortedListAdapterCallback(adapter);
            this.mList = new SortedList<>(CatalogEntry.class, callback);

            mList.addAll(this.getList());
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

    }

    @Override
    public void onBackPressed() {
        if (mResultIntent == null) {
            this.setResult(RESULT_CANCELED);
        } else {
            this.setResult(RESULT_OK, mResultIntent);
        }

        super.onBackPressed();
        this.overridePendingTransition(R.anim.standby, R.anim.push_out_down);
    }

    @Override
    public void onClick(View v) {
        if (v == mCancelBtn) {
            this.onBackPressed();
        }
    }

    void setResult(String catalogId) {
        this.mResultIntent = new Intent();
        mResultIntent.putExtra("catalogId", catalogId);

        this.onBackPressed();
    }

    void updateNameView() {
        Intent intent = this.getIntent();
        ArrayList<String> idArray = intent.getStringArrayListExtra("idArray");

        if (idArray != null) {
            int charWidth = (int) (Math.ceil(mNameView.getPaint().measureText("国")));
            int width = mNameView.getWidth() - mNameView.getPaddingLeft() - mNameView.getPaddingRight();
            int maxCount = width / charWidth;

            ArrayList<String> nameArray = new ArrayList<>();

            NoteManager mgr = NoteManager.instance();
            for (String id : idArray) {
                NoteEntry e = mgr.obtain(id);
                if (e != null) {
                    nameArray.add(e.getName());
                }
            }

            CharSequence text = null;
            if (nameArray.size() > 0) {
                int count = nameArray.size() - 1;
                for (String name : nameArray) {
                    count += name.length();
                }

                StringBuilder builder = new StringBuilder(maxCount);

                if (count <= maxCount) {
                    for (String name : nameArray) {
                        builder.append(name);
                        builder.append("，");
                    }

                    builder.deleteCharAt(builder.length() - 1);
                } else {
                    int length = 0;
                    for (String name : nameArray) {
                        if (builder.length() + name.length() > maxCount) {
                            break;
                        }

                        builder.append(name);
                        builder.append("，");

                        ++length;
                    }

                    if (length == 0) {
                        builder.append(nameArray.get(0));

                        ++length;
                    } else {
                        builder.deleteCharAt(builder.length() - 1);
                    }

                    String postFix = String.format(" 还有 %1$d 个", idArray.size() - length);
                    builder.append(postFix);

                }

                text = builder;
            }

            mNameView.setText(text);
        }
    }

    List<CatalogEntry> getList() {
        CatalogManager mgr = CatalogManager.instance();

        List<CatalogEntry> list = mgr.getList();
        list.remove(mgr.getAllEntry());
        list.remove(mgr.getTrashEntry());

        return list;
    }

    void requestCreateFolder() {
        Context context = this;
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
            Context context = this;
            CharSequence title = getString(R.string.catalog_folder_exist_title);
            CharSequence msg = getString(R.string.catalog_folder_exist_msg);
            CharSequence btn = getString(R.string.btn_good);

            MessageDialog.show(context, title, msg, btn);
            return;
        }

        // 创建文件
        CatalogEntry entry = CatalogManager.instance().create(name);
        if (entry == null) { // 创建失败
            Context context = this;
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

        // 设置结果
        this.setResult(entry.getId());
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
    private class CatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        static final int TYPE_HEADER_CREATE = 1001;
        static final int TYPE_ITEM = 2001;

        int[] mHeader;

        LayoutInflater mInflater;

        CatalogAdapter(Activity context) {
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
                holder = CreateHolder.create(MoveToActivity.this, mInflater, parent);
            } else if (viewType == TYPE_ITEM) {
                holder = ItemHolder.create(MoveToActivity.this, mInflater, parent);;
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

                CatalogEntry entry = mList.get(position);

                ItemHolder itemHolder = (ItemHolder)holder;
                itemHolder.bind(position, entry);
            }

        }

        @Override
        public int getItemCount() {
            int size = getHeaderCount();
            size += mList.size();

            return size;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    /**
     *
     */
    private static class CreateHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mCheckBox;
        View mIconView;
        View mNameView;

        MoveToActivity mContext;

        static CreateHolder create(MoveToActivity context, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_catalog_create_header;
            View view = inflater.inflate(resource, parent, false);

            CreateHolder holder = new CreateHolder(context, view);
            return holder;
        }

        public CreateHolder(MoveToActivity context, View itemView) {
            super(itemView);
            this.mContext = context;

            this.mCheckBox = itemView.findViewById(R.id.cb_check);
            this.mIconView = itemView.findViewById(R.id.iv_icon);
            this.mNameView = itemView.findViewById(R.id.tv_name);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {

        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                mContext.requestCreateFolder();
            }
        }
    }

    /**
     *
     */
    private static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox mCheckBox;
        TextView mNameView;
        TextView mNumView;
        View mArrowView;

        int mPosition;
        CatalogEntry mEntry;

        MoveToActivity mContext;

        static final ItemHolder create(MoveToActivity context, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_catalog_item;
            View view = inflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(context, view);
            return holder;
        }

        public ItemHolder(MoveToActivity context, View itemView) {
            super(itemView);
            this.mContext = context;

            this.mCheckBox = (CheckBox)(itemView.findViewById(R.id.cb_check));
            mCheckBox.setVisibility(View.GONE);

            this.mNameView = (TextView)(itemView.findViewById(R.id.tv_name));
            mNameView.setClickable(false);

            this.mNumView = (TextView)(itemView.findViewById(R.id.tv_num));
            this.mArrowView = itemView.findViewById(R.id.iv_arrow);
            mArrowView.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(this);
        }

        void bind(int position, CatalogEntry entry) {
            this.mPosition = position;
            this.mEntry = entry;

            String name = entry.getName();
            mNameView.setText(name);

            int num = CatalogManager.instance().getCount(entry);
            mNumView.setText(String.valueOf(num));

            boolean enable = (mEntry != mContext.mCatalog);
            itemView.setClickable(enable);
            mNameView.setEnabled(enable);
            mNumView.setEnabled(enable);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                mContext.setResult(mEntry.getId());
            }
        }

    }

}
