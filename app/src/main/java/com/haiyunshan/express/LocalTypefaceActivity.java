package com.haiyunshan.express;

import android.app.Activity;
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
import android.widget.TextView;

import com.haiyunshan.express.decoration.MarginDividerDecoration;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.typeface.LocalTypefaceDataset;
import com.haiyunshan.express.typeface.LocalTypefaceEntry;
import com.haiyunshan.express.typeface.LocalTypefaceScanner;
import com.haiyunshan.express.typeface.OnTypefaceScanListener;
import com.haiyunshan.express.typeface.TypefaceManager;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocalTypefaceActivity extends BaseActivity  implements View.OnClickListener, OnTypefaceScanListener {

    private static final int REQUEST_PREVIEW = 1003;

    RecyclerView mRecyclerView;
    LocalFontAdapter mAdapter;
    SortedList<LocalTypefaceEntry> mList;

    View mBackBtn;
    View mDoneBtn;

    TextView mEmptyView;

    View mProgressBar;
    TextView mInfoView;

    LocalTypefaceScanner mScanner;

    boolean mAlwaysTop = true;

    HashMap<String, WeakReference<Typeface>> mTypefaceMap;


    public static final void start(Fragment f) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, LocalTypefaceActivity.class);
        f.startActivity(intent);
    }

    public static void startForResult(Fragment f, int requestCode) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, LocalTypefaceActivity.class);
        f.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, LocalTypefaceActivity.class);
        context.startActivityForResult(intent, requestCode);

        context.overridePendingTransition(R.anim.push_in_up, R.anim.standby);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_typeface);

        onViewCreated(findViewById(R.id.root_container), savedInstanceState);
        onActivityCreated(savedInstanceState);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        this.mBackBtn = view.findViewById(R.id.btn_back);
        mBackBtn.setOnClickListener(this);

        this.mDoneBtn = view.findViewById(R.id.btn_done);
        mDoneBtn.setOnClickListener(this);

        this.mRecyclerView = (RecyclerView)(view.findViewById(R.id.rv_list));
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.addOnScrollListener(this.mScrollListener);

        this.mEmptyView = (TextView)(view.findViewById(R.id.tv_empty));

        this.mProgressBar = view.findViewById(R.id.pb_progress);
        this.mInfoView = (TextView)(view.findViewById(R.id.tv_info));
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        {
            this.mAdapter = new LocalFontAdapter(this);
            LocalFontSortedListAdapterCallback callback = new LocalFontSortedListAdapterCallback(mAdapter);
            this.mList = new SortedList<>(LocalTypefaceEntry.class, callback);
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

        LocalTypefaceDataset ds = TypefaceManager.instance().getLocalTypefaceDataset();
        if (ds != null) {
            List<LocalTypefaceEntry> list = ds.getList();
            mList.addAll(list);

            mEmptyView.setVisibility((mAdapter.getItemCount() == 0)? View.VISIBLE: View.INVISIBLE);

            mProgressBar.setVisibility(View.GONE);
            mInfoView.setText(String.format(Locale.CHINA, "共 %1$d 项", mAdapter.getItemCount()));

            mRecyclerView.scrollToPosition(0);

        } else {
            mEmptyView.setVisibility((mAdapter.getItemCount() == 0)? View.VISIBLE: View.INVISIBLE);

            this.mProgressBar.setVisibility(View.VISIBLE);
            mInfoView.setText("搜索中...");

            this.mScanner = new LocalTypefaceScanner(this);
            mScanner.setOnTypefaceScanListener(this);
            mScanner.asyncScan();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PREVIEW) {
            if (resultCode == RESULT_OK && data != null) {
                String path = data.getStringExtra("path");
                if (!TextUtils.isEmpty(path)) {
                    int index = mAdapter.indexOf(path);
                    if (index >= 0) {
                        mAdapter.notifyItemChanged(index);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        mRecyclerView.removeOnScrollListener(this.mScrollListener);

        if (mScanner != null) {
            mScanner.cancel();
            mScanner = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_OK);

        super.onBackPressed();

        this.overridePendingTransition(R.anim.standby, R.anim.push_out_down);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.onBackPressed();
        } else if (v == mDoneBtn) {
            this.onBackPressed();
        }
    }

    @Override
    public void onSeek(LocalTypefaceScanner scanner, LocalTypefaceEntry entry) {

        mList.add(entry);

        if (mEmptyView.getVisibility() == View.VISIBLE) {
            mEmptyView.setVisibility(View.INVISIBLE);
        }

        if (mAlwaysTop) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onComplete(LocalTypefaceScanner scanner) {
        {
            LocalTypefaceDataset ds = scanner.getResult();
            TypefaceManager.instance().setLocalTypefaceDataset(ds);
        }

        int size = mAdapter.getItemCount();

        if (size == 0) {
            mEmptyView.setText("搜索完毕\n没有找到字体");
        }

        {
            String text = null;
            text = String.format(Locale.CHINA, "共 %1$d 项", size);

            mProgressBar.setVisibility(View.GONE);
            mInfoView.setText(text);
        }

    }

    @Override
    public void onCancelled(LocalTypefaceScanner scanner) {

    }

    void requestRefresh() {

        mList.clear();

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setText("正在搜索手机上的字体\\n请稍候...");

        mProgressBar.setVisibility(View.VISIBLE);
        mInfoView.setText("搜索中...");

        this.mScanner = new LocalTypefaceScanner(this);
        mScanner.setOnTypefaceScanListener(this);
        mScanner.asyncScan();
    }

    /**
     *
     * @param path
     * @return
     */
    Typeface getTypeface(String path) {
        if (mTypefaceMap == null) {
            this.mTypefaceMap = new HashMap<>();
        }

        Typeface tf = null;

        // 访问缓存
        WeakReference<Typeface> ref = mTypefaceMap.get(path);
        if (ref != null) {
            tf = ref.get();
            if (tf == null) {
                mTypefaceMap.remove(path);
            }
        }

        // 创建字体
        if (tf == null) {
            tf = Typeface.createFromFile(path);
            if (tf != null) {
                mTypefaceMap.put(path, new WeakReference<>(tf));
            }

        }

        return tf;
    }

    /**
     *
     */
    private class LocalFontSortedListAdapterCallback extends SortedListAdapterCallback<LocalTypefaceEntry> {

        Collator mCollator;

        /**
         * Creates a {@link SortedList.Callback} that will forward data change events to the provided
         * Adapter.
         *
         * @param adapter The Adapter instance which should receive events from the SortedList.
         */
        public LocalFontSortedListAdapterCallback(RecyclerView.Adapter adapter) {
            super(adapter);

            this.mCollator = Collator.getInstance(Locale.CHINA);
        }

        @Override
        public int compare(LocalTypefaceEntry o1, LocalTypefaceEntry o2) {
            int r = 0;

            boolean timeCompare = false;
            if (timeCompare) {
                long diff = o1.getModified() - o2.getModified();
                if (diff > 0) {
                    r = -1;
                } else if (diff < 0) {
                    r = 1;
                } else {
                    r = mCollator.compare(o1.getName(), o2.getName());
                }
            } else {
                r = mCollator.compare(o1.getName(), o2.getName());
            }

            return r;
        }

        @Override
        public boolean areContentsTheSame(LocalTypefaceEntry oldItem, LocalTypefaceEntry newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areItemsTheSame(LocalTypefaceEntry item1, LocalTypefaceEntry item2) {
            return item1.getId().equals(item2.getId());
        }
    }

    /**
     *
     */
    private class LocalFontAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        LayoutInflater mInflater;

        LocalFontAdapter(Activity context) {
            this.mInflater = context.getLayoutInflater();
        }

        int indexOf(String path) {
            int size = mList.size();
            for (int i = 0; i < size; i++) {
                LocalTypefaceEntry e = mList.get(i);
                if (e.getPath().equalsIgnoreCase(path)) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int resource = R.layout.layout_local_font_item;
            View view = mInflater.inflate(resource, parent, false);
            ItemHolder holder = new ItemHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LocalTypefaceEntry e = mList.get(position);
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

        View mCheckView;

        LocalTypefaceEntry mEntry;

        public ItemHolder(View itemView) {
            super(itemView);

            this.mNameView = (TextView)(itemView.findViewById(R.id.tv_name));
            this.mCheckView = itemView.findViewById(R.id.iv_check);

            itemView.setOnClickListener(this);
        }

        void bind(int position, LocalTypefaceEntry e) {
            this.mEntry = e;

            this.mNameView.setText(e.getName());

            Typeface tf = getTypeface(e.getPath());
            mNameView.setTypeface(tf);

            TypefaceManager mgr = TypefaceManager.instance();
            if (mgr.obtainBySource(e.getPath()) != null) {
                mCheckView.setVisibility(View.VISIBLE);
            } else {
                mCheckView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                LocalTypefacePreviewActivity.startForResult(LocalTypefaceActivity.this, REQUEST_PREVIEW, mEntry.getPath());
            }
        }
    }

    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                mAlwaysTop = false;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
}
