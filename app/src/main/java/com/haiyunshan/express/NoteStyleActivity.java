package com.haiyunshan.express;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haiyunshan.express.app.ParagraphStyleUtils;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.dataset.note.style.Style;
import com.haiyunshan.express.decoration.MarginDividerDecoration;
import com.haiyunshan.express.decoration.SimpleDividerDecoration;
import com.haiyunshan.express.dialog.InputDialog;
import com.haiyunshan.express.dialog.MessageDialog;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.DocumentManager;

import java.util.List;

public class NoteStyleActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_COMPOSE = 1003;

    RecyclerView mRecyclerView;
    StyleAdapter mAdapter;
    ItemTouchHelper mItemTouchHelper;

    View mTopNormalBar;
    View mTopEditBar;

    View mBackBtn;
    View mAddBtn;
    View mEditBtn;

    View mDoneBtn;

    Document mDocument;

    boolean mEditMode = false;

    public static final void start(Activity context, String noteId) {
        Intent intent = new Intent(context, NoteStyleActivity.class);
        intent.putExtra("noteId", noteId);
        context.startActivity(intent);

        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);

        {
            String noteId = getIntent().getStringExtra("noteId");
            this.mDocument = DocumentManager.instance().obtain(noteId);
        }

        {
            this.mTopNormalBar = findViewById(R.id.top_normal_bar);
            this.mTopEditBar = findViewById(R.id.top_edit_bar);

            this.mBackBtn = findViewById(R.id.btn_back);
            mBackBtn.setOnClickListener(this);

            this.mAddBtn = findViewById(R.id.btn_add);
            mAddBtn.setOnClickListener(this);

            this.mEditBtn = findViewById(R.id.btn_edit);
            mEditBtn.setOnClickListener(this);

            this.mDoneBtn = findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);
        }

        {
            this.mRecyclerView = (RecyclerView) (findViewById(R.id.rv_list));
            LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mAdapter = new StyleAdapter(this, mDocument.getStyle());
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

            this.mItemTouchHelper = new ItemTouchHelper(new StyleItemTouchHelperCallback());
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDocument != null) {
            mDocument.save(Document.saveMaskStyle);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_COMPOSE) {
            if (resultCode == RESULT_OK && data != null) {
                String entityId = data.getStringExtra("entityId");
                if (!TextUtils.isEmpty(entityId)) {
                    int index = mAdapter.indexOf(entityId);
                    if (index >= 0) {
                        mAdapter.notifyItemChanged(index);

                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDocument != null) {
            mDocument.save(Document.saveMaskStyle);
        }

        super.onBackPressed();

        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.onBackPressed();
        } else if (v == mEditBtn) {
            this.setEditMode(true);
        } else if (v == mDoneBtn) {
            this.setEditMode(false);
        }
    }

    void setEditMode(boolean value) {
        if (!(mEditMode ^ value)) {
            return;
        }

        this.mEditMode = value;

        if (mEditMode) {
            mTopNormalBar.setVisibility(View.INVISIBLE);
            mTopEditBar.setVisibility(View.VISIBLE);
        } else {
            mTopNormalBar.setVisibility(View.VISIBLE);
            mTopEditBar.setVisibility(View.INVISIBLE);
        }

        boolean notify = true;
        if (notify) {
            mAdapter.notifyDataSetChanged();
        } else {
            int count = mRecyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                int pos = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(i));
                mAdapter.notifyItemChanged(pos);
            }
        }
    }

    void requestRename(final StyleEntity entry) {
        Context context = this;
        String title = "给样式重命名";
        String msg = "请为此样式输入新名称。";
        String text = entry.getName();
        String hint = "名称";

        InputDialog dialog = InputDialog.create(context, title, msg, text, hint, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    InputDialog inputDialog = (InputDialog)dialog;
                    String text = inputDialog.getText();

                    doRename(entry, text);
                }
            }
        });
        dialog.show();
    }

    void doRename(final StyleEntity entry, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        String name = text.trim();
        if (TextUtils.isEmpty(name)) {
            return;
        }

        if (entry.getName().compareTo(name) == 0) {
            return;
        }

        // 判断文件是否存在
        Style style = mDocument.getStyle();
        boolean exist = (style.find(name) != null);
        if (exist) {
            Context context = this;
            CharSequence title = "名称已存在";
            CharSequence msg = "请选取其他名称。";
            CharSequence btn = getString(R.string.btn_good);

            MessageDialog.show(context, title, msg, btn);
            return;
        }

        // 修改名称
        entry.setName(name);

        // 更新界面
        int pos = mAdapter.indexOf(entry.getId());
        if (pos >= 0) {
            mAdapter.notifyItemChanged(pos);
        }
    }

    /**
     *
     */
    private class StyleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!mEditMode) {
                return 0;
            }

            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return super.getSwipeThreshold(viewHolder);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();

            mAdapter.move(from, to);

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
        }
    }

    /**
     *
     */
    private class StyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Style mStyle;
        LayoutInflater mInflater;

        StyleAdapter(Activity context, Style style) {
            this.mInflater = context.getLayoutInflater();

            this.mStyle = style;
        }

        int indexOf(String id) {
            List<StyleEntity> list = mStyle.getList();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                StyleEntity e = list.get(i);
                if (e.getId().equalsIgnoreCase(id)) {
                    return i;
                }
            }

            return -1;
        }

        public void move(int from, int to) {

            List<StyleEntity> list = mStyle.getList();

            StyleEntity prev = list.remove(from);
            list.add(to, prev);

            this.notifyItemMoved(from, to);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemHolder holder = ItemHolder.create(NoteStyleActivity.this, mInflater, parent);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            List<StyleEntity> list = mStyle.getList();

            StyleEntity e = list.get(position);

            ItemHolder itemHolder = (ItemHolder)holder;
            itemHolder.bind(position, e);
        }

        @Override
        public int getItemCount() {
            return mStyle.getList().size();
        }
    }

    /**
     *
     */
    private static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameView;
        View mDeleteIcon;
        View mDragIcon;
        View mArrowView;

        StyleEntity mEntity;

        NoteStyleActivity mContext;

        static ItemHolder create(NoteStyleActivity context, LayoutInflater inflater, ViewGroup parent) {
            int resource = R.layout.layout_app_style_item;
            View view = inflater.inflate(resource, parent, false);

            ItemHolder holder = new ItemHolder(context, view);
            return holder;
        }

        public ItemHolder(NoteStyleActivity context, View itemView) {
            super(itemView);

            this.mContext = context;

            this.mNameView = (TextView)itemView.findViewById(R.id.tv_name);
            mNameView.setOnClickListener(this);

            this.mDeleteIcon = itemView.findViewById(R.id.iv_delete);
            this.mDragIcon = itemView.findViewById(R.id.iv_drag);
            mDragIcon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        mContext.mItemTouchHelper.startDrag(ItemHolder.this);
                        mContext.mRecyclerView.postInvalidateOnAnimation();
                    }

                    return false;
                }
            });

            this.mArrowView = itemView.findViewById(R.id.iv_arrow);


            itemView.setOnClickListener(this);
        }

        void bind(int position, StyleEntity s) {
            this.mEntity = s;

            mNameView.setText(s.getName());
            ParagraphStyleUtils.apply(mNameView, s);
            mNameView.setTextAlignment(StyleEntity.ALIGN_START);

            if (mContext.mEditMode) {
                mNameView.setClickable(true);
                itemView.setClickable(false);

                mDeleteIcon.setVisibility(View.VISIBLE);
                mDragIcon.setVisibility(View.VISIBLE);
                mArrowView.setVisibility(View.GONE);
            } else {
                mNameView.setClickable(false);
                itemView.setClickable(true);

                mDeleteIcon.setVisibility(View.GONE);
                mDragIcon.setVisibility(View.GONE);
                mArrowView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                StyleComposeActivity.startForResult(mContext, REQUEST_COMPOSE, mContext.mDocument.getId(), mEntity.getId());
            } else if (v == mNameView) {
                mContext.requestRename(mEntity);
            }
        }
    }

}
