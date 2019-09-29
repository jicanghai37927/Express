package com.haiyunshan.express.test;

import android.app.Activity;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.CheckedMap;
import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.app.Utils;
import com.haiyunshan.express.dataset.catalog.CatalogEntry;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.fragment.TrashFragment;
import com.haiyunshan.express.widget.MyRecyclerView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestItemTouchActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    NoteAdapter mAdapter;
    CheckedMap<NoteEntry> mCheckedMap;
    ArrayList<NoteEntry> mList;
    SimpleDividerDecoration mDividerDecor;
    ItemTouchHelper mItemTouchHelper;

    CatalogEntry mCatalog;

    boolean mEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_item_touch);

        {
            this.mRecyclerView = (RecyclerView) (this.findViewById(R.id.rv_list));
            LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
        }


        {
            this.mCatalog = CatalogManager.instance().obtain(CatalogManager.idTrash);
        }

        {
            String name = mCatalog.getName();
        }

        {
            this.mCheckedMap = new CheckedMap<>();

            this.mAdapter = new NoteAdapter(this);
            this.mList = new ArrayList<>();
            mList.addAll(getList(this.mCatalog));
            mRecyclerView.setAdapter(mAdapter);

            // item decor
            SimpleDividerDecoration decor = new SimpleDividerDecoration(this);
            decor.setMargin(getResources().getDimensionPixelSize(R.dimen.inset_2l), 0);
            mRecyclerView.addItemDecoration(decor);
            this.mDividerDecor = decor;

            this.mItemTouchHelper = new ItemTouchHelper(new NoteItemTouchHelperCallback());
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        }

    }

    List<NoteEntry> getList(CatalogEntry catalog) {
        List<NoteEntry> list = new ArrayList<>();

        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            list.add(new NoteEntry(UUIDUtils.next(), "笔记 " + i, "", time + i));
        }

        return list;
    }

    void onCheckedChanged() {

    }

    public void move(int from, int to) {

        NoteEntry prev = mList.remove(from);
        mList.add(to, prev);

        mAdapter.notifyItemMoved(from + mAdapter.getHeaderCount(), to + mAdapter.getHeaderCount());
    }

    private class MyItemTouchHelper extends ItemTouchHelper {

        /**
         * Creates an ItemTouchHelper that will work with the given Callback.
         * <p>
         * You can attach ItemTouchHelper to a RecyclerView via
         * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
         * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
         *
         * @param callback The Callback which controls the behavior of this touch helper.
         */
        public MyItemTouchHelper(Callback callback) {
            super(callback);
        }

    }

    private class NoteItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return super.isItemViewSwipeEnabled();
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof DescHolder) {
                return 0;
            }

            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 100.f;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            int headerCount = mAdapter.getHeaderCount();

            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();

            if (from < headerCount || to < headerCount) {
                return false;
            }

            from -= headerCount;
            to -= headerCount;
            move(from, to);

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void onChildDraw(Canvas c,
                                RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState,
                                boolean isCurrentlyActive) {
            if (isCurrentlyActive) {
                viewHolder.itemView.setElevation(20.f);
                viewHolder.itemView.setAlpha(0.9f);
//                viewHolder.itemView.setTranslationZ(20.f);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        }

        @Override
        public void onChildDrawOver(Canvas c,
                                    RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState,
                                    boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setElevation(0.f);
            viewHolder.itemView.setAlpha(1.0f);
//            viewHolder.itemView.setTranslationZ(0.f);
        }
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
            int pos1 = mList.indexOf(o1);
            int pos2 = mList.indexOf(o2);

            return (pos1 - pos2);
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
                holder = DescHolder.create(TestItemTouchActivity.this, mInflater, parent);
            } else if (viewType == TYPE_ITEM) {
                holder = ItemHolder.create(TestItemTouchActivity.this, mInflater, parent);
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

        TestItemTouchActivity mFragment;

        static DescHolder create(TestItemTouchActivity fragment, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_trash_header_item;
            View view = inflater.inflate(resource, parent, false);

            DescHolder holder = new DescHolder(fragment, view);
            return holder;
        }

        public DescHolder(TestItemTouchActivity fragment, View itemView) {
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
        View mDragView;

        int mPosition;
        NoteEntry mEntry;

        TestItemTouchActivity mFragment;

        static ItemHolder create(TestItemTouchActivity fragment, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_test_touch_item;
            View view = inflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(fragment, view);
            return holder;
        }

        public ItemHolder(TestItemTouchActivity fragment, View itemView) {
            super(itemView);
            this.mFragment = fragment;

            this.mCheckBox = (CheckBox) (itemView.findViewById(R.id.cb_check));
            this.mNameView = (TextView) (itemView.findViewById(R.id.tv_name));
            this.mDayView = (TextView) (itemView.findViewById(R.id.tv_day));
            this.mDescView = (TextView) (itemView.findViewById(R.id.tv_desc));
            this.mCatalogView = (TextView) (itemView.findViewById(R.id.tv_catalog));
            mCatalogView.setVisibility(View.GONE);

            this.mDragView = itemView.findViewById(R.id.iv_drag);
            mDragView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        mFragment.mItemTouchHelper.startDrag(ItemHolder.this);
                        mFragment.mRecyclerView.postInvalidateOnAnimation();
                    }
                    return false;
                }
            });
            mDragView.setClickable(true);
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
