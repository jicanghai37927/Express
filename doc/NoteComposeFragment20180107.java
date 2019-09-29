package com.haiyunshan.express.fragment.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiyunshan.express.NoteComposeActivity;
import com.haiyunshan.express.NoteSettingActivity;
import com.haiyunshan.express.R;
import com.haiyunshan.express.app.SoftKeyboardUtils;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.fragment.BaseFragment;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.DocumentManager;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.decoration.EntityDividerDecoration;
import com.haiyunshan.express.note.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.note.holder.SegmentHolder;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.widget.MyRecyclerView;

import java.util.ArrayList;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE;

public class NoteComposeFragment20180107 extends BaseFragment implements View.OnClickListener {

    private static final int DELAY_UNLOCK_CONTENT_HEIGHT = 200;

    private static final int requestMore = 1003;

    public static final int MODE_VIEW   = 101; //
    public static final int MODE_FORMAT = 102; //
    public static final int MODE_EDIT   = 103; //
    public static final int MODE_PAGE   = 104; // 页面设置

    public static final String argNoteId = "noteId";

    View mTopBar;
    View mBottomBar;
    View mBottomNormalBar;
    View mBottomEditBar;

    TextView mTitleView;
    View mBackBtn;
    View mDoneBtn;
    View mFormatBtn;
    View mCreateBtn;
    View mMoreBtn;

    View mEditBtn;

    View mTabBtn;

    FrameLayout mEntityContainer;
    MyRecyclerView mRecyclerView;
    SegmentAdapter mAdapter;

    EntityDividerDecoration mDividerDecor;

    FrameLayout mFormatContainer;

    int mMode = MODE_VIEW;

    Handler mHandler;

    Document mDocument;
    NoteComposeActivity mParent;

    public NoteComposeFragment20180107() {
        this.mHandler = new Handler();
    }

    public void setArguments(NoteComposeActivity parent, Bundle args) {
        this.mParent = parent;
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_compose20180107, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mTopBar = view.findViewById(R.id.top_bar);

            this.mTitleView = (TextView)(view.findViewById(R.id.tv_title));

            this.mBackBtn = view.findViewById(R.id.btn_back);
            mBackBtn.setOnClickListener(this);

            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);

            this.mFormatBtn = view.findViewById(R.id.btn_format);
            mFormatBtn.setOnClickListener(this);

            this.mCreateBtn = view.findViewById(R.id.btn_create);
            mCreateBtn.setOnClickListener(this);

            this.mMoreBtn = view.findViewById(R.id.btn_more);
            mMoreBtn.setOnClickListener(this);

            this.mBottomBar = view.findViewById(R.id.bottom_bar);

            this.mBottomNormalBar = view.findViewById(R.id.bottom_normal_bar);
            this.mBottomEditBar = view.findViewById(R.id.bottom_edit_bar);

            this.mEditBtn = view.findViewById(R.id.btn_edit);
            mEditBtn.setOnClickListener(this);

            this.mTabBtn = view.findViewById(R.id.btn_tab);
            mTabBtn.setOnClickListener(this);
        }

        {
            this.mEntityContainer = (FrameLayout)(view.findViewById(R.id.entity_container));

            this.mRecyclerView = (MyRecyclerView) (view.findViewById(R.id.rv_list));
            LinearLayoutManager layout = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layout);
        }

        {
            this.mFormatContainer = (FrameLayout)(view.findViewById(R.id.format_container));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            String noteId = getArguments().getString(argNoteId, "demo");

            NoteEntry entry = NoteManager.instance().obtain(noteId);
            if (entry == null) {
                entry = NoteManager.instance().create(noteId, CatalogManager.instance().getDefault(), "DEMO");
            }

            this.mDocument = DocumentManager.instance().obtain(noteId);
            if (TextUtils.isEmpty(mDocument.getTitle().getText())) {
                mDocument.getTitle().setText(entry.getName());
            }
        }

        {
            this.mAdapter = new SegmentAdapter(this, mDocument);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnScrollListener(new MyScrollListener());
        }

        {
            mTitleView.setText(mDocument.getTitle().getText());
        }

        {
            EntityDividerDecoration decor = new EntityDividerDecoration(this.getActivity());
            float margin = mDocument.getPage().getPaddingLeft();
            margin = TypedValue.applyDimension(COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());
            decor.setMargin((int)margin, 0);
            mRecyclerView.addItemDecoration(decor);

            this.mDividerDecor = decor;
        }

        if (mDocument.isEmpty()) {
            this.setMode(MODE_EDIT, false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    View view = getView().findViewById(R.id.root_container).findFocus();
                    SoftKeyboardUtils.show(getActivity(), view);
                }
            }, 200);
        } else {

            this.setMode(MODE_VIEW, false);
            mDividerDecor.setEnable(false);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestMore) {
            if (resultCode == Activity.RESULT_OK) {

                mAdapter.notifyDataSetChanged();

                if (data != null) {
                    String action = data.getStringExtra("action");
                    if (action.equalsIgnoreCase(NoteSettingActivity.actionPageSetting)) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean result = showPageSetting();
                                if (result) {
                                    setNaviVisible(false);
                                    setActionBarVisible(false, true);
                                }
                            }
                        });
                    } else if (action.equalsIgnoreCase(NoteSettingActivity.actionDuplicate)) {
                        String noteId = data.getStringExtra("noteId");
                        mParent.showNote(noteId);
                    }
                }
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            this.applyChanged();
            mDocument.save();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDocument != null) {
            this.applyChanged();
            mDocument.save();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFormatContainer.getVisibility() == View.VISIBLE) {
            setNaviVisible(false);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mFormatContainer.getVisibility() == View.VISIBLE) {
            TextFormatFragment f = (TextFormatFragment)(getChildFragmentManager().findFragmentByTag("text_format"));
            if (f != null) {
                boolean r = f.onBackPressed();
                if (r) {
                    return true;
                }

                this.hideTextFormat();
                return true;
            }
        }

        if (mDocument != null) {
            this.applyChanged();
            mDocument.save();
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.getActivity().onBackPressed();
        } else if (v == mDoneBtn) {

            this.setMode(MODE_VIEW, true);
            this.applyChanged();
            mDocument.save();

        } else if (v == mMoreBtn) {
            NoteSettingActivity.startForResult(this, requestMore, mDocument.getId());
        } else if (v == mFormatBtn) {
            boolean result = this.showTextFormat();
            if (result) {
                this.setNaviVisible(false);
                this.setActionBarVisible(false, true);
            }
        } else if (v == mCreateBtn) {
            this.requestSplitParagraph();
        } else if (v == mEditBtn) {
            this.setMode(MODE_EDIT, true);
        } else if (v == mTabBtn) {
            this.requestTab();
        }
    }

    public Intent getResult() {
        Intent intent = new Intent();
        intent.putExtra(argNoteId, mDocument.getId());
        return intent;
    }

    public void setMode(int mode, boolean anim) {
        int oldMode = this.mMode;

        if (this.mMode == mode) {
            return;
        }

        this.mMode = mode;

        // 分隔线
        {
            boolean dividerVisible = (mode == MODE_EDIT);
            mDividerDecor.setEnable(dividerVisible);
        }

        // 返回、完成
        {
            mBackBtn.setVisibility((mode == MODE_EDIT)? View.GONE: View.VISIBLE);
            mDoneBtn.setVisibility((mode == MODE_EDIT)? View.VISIBLE: View.GONE);

            if (mode == MODE_EDIT) {
                mBottomNormalBar.setVisibility(View.INVISIBLE);
                mBottomEditBar.setVisibility(View.VISIBLE);

                if (mBottomBar.getVisibility() != View.VISIBLE) {
                    mBottomBar.setVisibility(View.VISIBLE);

                    if (anim) {
                        mBottomBar.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), R.anim.push_in_up));
                    }
                }

            } else if (mode == MODE_FORMAT || mode == MODE_PAGE) {
                if (mBottomBar.getVisibility() == View.VISIBLE) {
                    if (anim) {
                        mBottomBar.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), R.anim.push_out_down));
                    }

                    mBottomBar.setVisibility(View.INVISIBLE);
                }
            } else {
                mBottomNormalBar.setVisibility(View.VISIBLE);
                mBottomEditBar.setVisibility(View.INVISIBLE);

                if (mBottomBar.getVisibility() != View.VISIBLE) {
                    mBottomBar.setVisibility(View.VISIBLE);

                    if (anim) {
                        mBottomBar.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), R.anim.push_in_up));
                    }
                }

            }
        }

        // 软键盘
        {
            if (mode != MODE_EDIT) {
                SoftKeyboardUtils.hide(this.getActivity(),  mRecyclerView.getFocusedChild());
            }
        }


        //
        mAdapter.notifyDataSetChanged();

        if (mode == MODE_EDIT) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Activity context = getActivity();

                    if (!SoftKeyboardUtils.isVisible(context)) {
                        RecyclerView.ViewHolder h = mRecyclerView.getFirstViewHolder();
                        if (h != null) {
                            SegmentHolder holder = (SegmentHolder)h;
                            holder.requestFocus();
                            SoftKeyboardUtils.show(getActivity(), getActivity().getCurrentFocus());
                        }
                    }

                }
            }, 100);
        }
    }

    public int getMode() {
        return this.mMode;
    }

    public Document getDocument() {
        return this.mDocument;
    }

    public void remove(String id) {
        mAdapter.remove(id, true);

        mDocument.save();
    }

    public View getEntityContainer() {
        return this.mEntityContainer;
    }

    public View getFormatContainer() {
        return this.mFormatContainer;
    }

    void applyChanged() {
        {
            String name = mDocument.getTitle().getText().toString();
            String subtitle = mDocument.getSubtitle().getText().toString();

            NoteEntry entry = NoteManager.instance().obtain(mDocument.getId());
            entry.setName(name);
            entry.setDesc(subtitle);
        }

        {
            int count = mRecyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = mRecyclerView.getChildAt(i);
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                if (holder != null && holder instanceof SegmentHolder) {
                    ((SegmentHolder) holder).applyChanged();
                }
            }
        }
    }

    public void setFormatEntity(Segment entity) {
        if (entity == null) {
            return;
        }

        if (mFormatContainer == null || mFormatContainer.getVisibility() != View.VISIBLE) {
            return;
        }

        String id = (String)(mFormatContainer.getTag(R.id.key_id));
        if (TextUtils.isEmpty(id) || !entity.getId().equalsIgnoreCase(id)) {
            mFormatContainer.setTag(R.id.key_id, entity.getId());

            {
                FragmentManager fm = this.getChildFragmentManager();

                TextFormatFragment f = new TextFormatFragment();
                f.setArguments(this, (ParagraphSegment)entity);

                FragmentTransaction t = fm.beginTransaction();
                t.replace(mFormatContainer.getId(), f, "text_format");
                t.commit();
            }
        }

        boolean value = (Boolean)mFormatContainer.getTag(R.id.key_collapse);
        if (value) {
            mFormatContainer.setTag(R.id.key_collapse, false);
            mFormatContainer.getLayoutParams().height = ParagraphSegmentHolder.getSoftKeyboardHeight(this.getActivity());
            mFormatContainer.requestLayout();
        }

    }

    public Segment getFormatEntity() {
        if (mFormatContainer == null || mFormatContainer.getVisibility() != View.VISIBLE) {
            return null;
        }

        Object tag = mFormatContainer.getTag(R.id.key_id);
        if (tag == null) {
            return null;
        }

        String id = (String)tag;
        Segment en = mDocument.obtain(id);

        return en;
    }

    public boolean isCollapse() {
        if (mFormatContainer == null && mFormatContainer.getVisibility() != View.VISIBLE) {
            return true;
        }

        boolean value = (Boolean)mFormatContainer.getTag(R.id.key_collapse);
        return value;
    }

    public void collapseTextFormat() {
        if (mFormatContainer == null && mFormatContainer.getVisibility() != View.VISIBLE) {
            return;
        }

        boolean value = (Boolean)mFormatContainer.getTag(R.id.key_collapse);
        if (value) {
            return;
        }

        FragmentManager fm = this.getChildFragmentManager();
        TextFormatFragment f = (TextFormatFragment)(fm.findFragmentByTag("text_format"));
        if (f != null) {
            f.collapse();
        }

        mFormatContainer.setTag(R.id.key_collapse, true);
        mFormatContainer.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.action_bar_height);
        mFormatContainer.requestLayout();
    }

    public void hidePageSetting() {

        int mode = (Integer)mFormatContainer.getTag(R.id.key_mode);

        this.setMode(mode, false);

        if (mode == MODE_EDIT) {

            View child = mRecyclerView.getFocusedChild();
            if (child != null && child instanceof EditText) {
                this.lockContentHeight();

                // 键盘显示后，恢复原来设置
                mHandler.removeCallbacks(mUnlockContentHeight);
                mHandler.postDelayed(mUnlockContentHeight, DELAY_UNLOCK_CONTENT_HEIGHT);

                SoftKeyboardUtils.show(getActivity(), child);
            }
        }

        {
            this.setNaviVisible(true);
            this.setActionBarVisible(true, true);
        }

        {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.remove(fm.findFragmentByTag("page_setting"));
            t.commit();

            this.mFormatContainer.setVisibility(View.GONE);
        }

    }

    boolean showPageSetting() {

        if (mDocument == null) {
            return false;
        }

        {
            FragmentManager fm = this.getChildFragmentManager();

            PageFragment f = new PageFragment();
            f.setArguments(this);

            FragmentTransaction t = fm.beginTransaction();
            t.replace(mFormatContainer.getId(), f, "page_setting");
            t.commit();

            mFormatContainer.getLayoutParams().height = ParagraphSegmentHolder.getSoftKeyboardHeight(this.getActivity());

            mFormatContainer.setTag(R.id.key_mode, this.mMode);

            this.mFormatContainer.setVisibility(View.VISIBLE);

        }

        boolean isKeyboardVisible = SoftKeyboardUtils.isVisible(getActivity());

        // 显示面板
        if (isKeyboardVisible) {

            // 锁定高度
            lockContentHeight();

            // 显示面板
            setPanel(true);

            // 隐藏输入法
            SoftKeyboardUtils.hide(this.getActivity(), mFormatContainer);

            // 解除锁定
            mHandler.removeCallbacks(mUnlockContentHeight);
            mHandler.postDelayed(mUnlockContentHeight, DELAY_UNLOCK_CONTENT_HEIGHT);

        } else {
            this.setPanel(true);
        }

        this.setMode(MODE_PAGE, false);

        return true;
    }

    public void notifyPageChanged() {
        mAdapter.notifyDataSetChanged();
    }

    boolean showTextFormat() {

        if (mDocument == null) {
            return false;
        }

        View child = mRecyclerView.getFocusedChild();
        if (child == null) {
            child = mRecyclerView.getChildAt(0);
        }
        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(child);
        if (holder == null) {
            return false;
        }

        SegmentHolder itemHolder = (SegmentHolder)holder;
        Segment entity = itemHolder.getEntity();

        {
            FragmentManager fm = this.getChildFragmentManager();

            TextFormatFragment f = new TextFormatFragment();
            f.setArguments(this, (ParagraphSegment)entity);

            FragmentTransaction t = fm.beginTransaction();
            t.replace(mFormatContainer.getId(), f, "text_format");
            t.commit();

            mFormatContainer.getLayoutParams().height = ParagraphSegmentHolder.getSoftKeyboardHeight(this.getActivity());

            mFormatContainer.setTag(R.id.key_id, entity.getId());
            mFormatContainer.setTag(R.id.key_collapse, false);
            mFormatContainer.setTag(R.id.key_mode, this.mMode);

            this.mFormatContainer.setVisibility(View.VISIBLE);

        }

        boolean isKeyboardVisible = SoftKeyboardUtils.isVisible(getActivity());

        // 显示面板
        if (isKeyboardVisible) {

            // 锁定高度
            lockContentHeight();

            // 显示面板
            setPanel(true);

            // 隐藏输入法
            SoftKeyboardUtils.hide(getActivity(), mFormatContainer);

            // 解除锁定
            mHandler.removeCallbacks(mUnlockContentHeight);
            mHandler.postDelayed(mUnlockContentHeight, DELAY_UNLOCK_CONTENT_HEIGHT);

        } else {
            this.setPanel(true);
        }

        this.setMode(MODE_FORMAT, false);

        return true;
    }

    void setPanel(boolean visible) {

        View view = this.getFormatContainer();

        boolean old = (view.getVisibility() == View.VISIBLE);
        if (!(old ^ visible)) {
            return;
        }

        if (visible) {
            int height = ParagraphSegmentHolder.getSoftKeyboardHeight(getActivity());
            view.getLayoutParams().height = height;
            view.setVisibility(View.VISIBLE);

        } else {
            int height = 0;
            view.getLayoutParams().height = height;
            view.setVisibility(View.GONE);
        }
    }

    void lockContentHeight() {
        View view = this.getEntityContainer();

        int height = view.getHeight();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
        params.height = height;
        params.weight = 0;
    }

    public void hidePanel() {
        this.hideTextFormat();
    }

    public void titleTextFormat() {
        if (isCollapse()) {
            RecyclerView.ViewHolder h = mRecyclerView.getFirstViewHolder();
            if (h != null) {
                SegmentHolder holder = (SegmentHolder) h;
                holder.beginFormat();

                this.setFormatEntity(holder.getEntity());
            }
        }
    }

    public void hideTextFormat() {

        int mode = (Integer)mFormatContainer.getTag(R.id.key_mode);

        this.setMode(mode, false);

        if (mode == MODE_EDIT) {

            View child = mRecyclerView.getFocusedChild();
            if (child != null && child instanceof EditText) {
                this.lockContentHeight();

                // 键盘显示后，恢复原来设置
                mHandler.removeCallbacks(mUnlockContentHeight);
                mHandler.postDelayed(mUnlockContentHeight, DELAY_UNLOCK_CONTENT_HEIGHT);

                SoftKeyboardUtils.show(getActivity(), child);
            }
        }

        {
            this.setNaviVisible(true);
            this.setActionBarVisible(true, true);
        }

        {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.remove(fm.findFragmentByTag("text_format"));
            t.commit();

            this.mFormatContainer.setVisibility(View.GONE);
        }

    }

    void setNaviVisible(boolean visible) {
        View view = this.getActivity().getWindow().getDecorView();

        int newVis = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (!visible) {
            newVis |= SYSTEM_UI_FLAG_LOW_PROFILE | SYSTEM_UI_FLAG_FULLSCREEN;
        }

        view.setSystemUiVisibility(newVis);
    }

    void setActionBarVisible(boolean visible, boolean anim) {
        if (visible) {
            if (mTopBar.getVisibility() != View.VISIBLE) {
                mTopBar.setVisibility(View.VISIBLE);

                if (anim) {
                    mTopBar.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), R.anim.push_in_down));
                }
            }
        } else {
            if (mTopBar.getVisibility() != View.INVISIBLE) {
                if (anim) {
                    mTopBar.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), R.anim.push_out_up));
                }

                mTopBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    void requestTab() {
        if (!SoftKeyboardUtils.isVisible(this.getActivity())) {
            return;
        }

        View view = this.getActivity().getCurrentFocus();
        if (view == null) {
            return;
        }

        if (!(view instanceof EditText)) {
            return;
        }

        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(view);
        if (!(holder instanceof ParagraphSegmentHolder)) {
            return;
        }

        ParagraphSegmentHolder itemHolder = (ParagraphSegmentHolder)holder;
        itemHolder.requestTab();
    }

    void requestSplitParagraph() {
        View view = this.getActivity().getCurrentFocus();
        if (view == null) {
            return;
        }

        if (!(view instanceof EditText)) {
            return;
        }

        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(view);
        if (!(holder instanceof ParagraphSegmentHolder)) {
            return;
        }

        ParagraphSegmentHolder itemHolder = (ParagraphSegmentHolder)holder;
        ParagraphSegment en = itemHolder.getEntity();
        if (mDocument.indexOf(en) >= 0) {
            EditText edit = (EditText)view;

            String text = edit.getText().toString();
            int start = edit.getSelectionStart();
            int end = edit.getSelectionEnd();

            if (text.length() != 0) {
                if (start == end) {
                    int pos = start;

                    String t1;
                    String t2;

                    t1 = text.substring(0, pos);
                    t2 = text.substring(pos);

                    this.splitParagraph(itemHolder, t1, t2);

                } else {
                    String t1 = text.substring(0, start);
                    String t2 = text.substring(start, end);
                    String t3 = text.substring(end);

                    this.splitParagraph(itemHolder, t1, t2, t3);
                }
            }

        } else {

        }
    }

    SegmentHolder findEntityHolder(String id) {
        Segment en = mDocument.obtain(id);
        if (en == null) {
            return null;
        }

        return findEntityHolder(en);
    }

    SegmentHolder findEntityHolder(Segment en) {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mRecyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
            if (holder != null && holder instanceof SegmentHolder) {
                SegmentHolder itemHolder = (SegmentHolder)holder;
                if (itemHolder.getEntity() == en) {
                    return itemHolder;
                }
            }
        }

        return null;
    }

    public void mergeParagraph(ParagraphSegment from, ParagraphSegment to) {
        int index = to.getText().length();

        SegmentHolder toHolder = findEntityHolder(to);

        CharSequence t = from.getText();
        if (!TextUtils.isEmpty(t)) {
            CharSequence text = to.getText();
            text = new SpannableStringBuilder(text).append(t);

            to.setText(text);

            if (toHolder != null) {
                ((ParagraphSegmentHolder)toHolder).setText(text);
            }
        }

        mDocument.remove(from);

        mAdapter.remove(from.getId(), true);

        if (toHolder == null) {
            mAdapter.notifyItemChanged(mAdapter.indexOf(to.getId()));
        } else {
            toHolder.requestFocus();
            ((ParagraphSegmentHolder)toHolder).setSelection(index);

            if (mDocument.indexOf(to) + 1 == mDocument.size()) {
                ((ParagraphSegmentHolder) toHolder).setMinLines(ParagraphSegmentHolder.getMinLines(this.getActivity(), mDocument));
                ((ParagraphSegmentHolder) toHolder).setHint("正文");
            }
        }

        mDocument.save();
    }

    void splitParagraph(ParagraphSegmentHolder holder, CharSequence t1, CharSequence t2) {

        // 当前数据
        ParagraphSegment en = holder.getEntity();
        en.setText(t1);
        holder.setText(t1);
        holder.setSelection(t1.length());
        holder.setMinLines(1);

        // 新的数据
        ParagraphSegment entity = new ParagraphSegment(this.mDocument);
        entity.setText(t2);
        entity.setStyle(new StyleEntity(en.getStyle()));

        // 更新列表
        {
            int index = mAdapter.indexOf(en.getId());
            index = mAdapter.add(entity, index + 1);
//            mAdapter.notifyItemInserted(index);
            mAdapter.notifyDataSetChanged();
        }

        // 更新数据
        {
            int index = mDocument.indexOf(en);
            mDocument.add(entity, index + 1);

            mDocument.save();
        }

        final String id = entity.getId();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                int index = mAdapter.indexOf(id);
//                mRecyclerView.scrollToPosition(index);

                SegmentHolder itemHolder = findEntityHolder(id);
                if (itemHolder != null) {
                    itemHolder.requestFocus();
                }
            }
        });

    }

    void splitParagraph(ParagraphSegmentHolder holder, CharSequence t1, CharSequence t2, CharSequence t3) {

        // 当前数据
        ParagraphSegment en = holder.getEntity();
        en.setText(t1);
        holder.setText(t1);
        holder.requestFocus();

        // 新的数据
        ParagraphSegment entity = new ParagraphSegment(mDocument);
        entity.setText(t2);
        entity.setStyle(new StyleEntity(en.getStyle()));

        ParagraphSegment e3 = new ParagraphSegment(mDocument);
        e3.setText(t3);
        e3.setStyle(new StyleEntity(en.getStyle()));

        // 更新列表
        {
            int index = mAdapter.indexOf(en.getId());
            index = mAdapter.add(entity, index + 1);
            mAdapter.add(e3, index + 1);

            mAdapter.notifyItemRangeInserted(index, 2);
        }

        // 更新数据
        {
            int index = mDocument.indexOf(en);
            mDocument.add(entity, index + 1);
            mDocument.add(e3, index + 2);

            mDocument.save();
        }
    }

    public ParagraphSegmentHolder findParagraphHolder(ParagraphSegment segment) {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mRecyclerView.getChildAt(i);
            SegmentHolder holder = (SegmentHolder)mRecyclerView.getChildViewHolder(child);
            if (holder.getEntity() == segment) {
                return (ParagraphSegmentHolder)holder;
            }
        }

        return null;
    }

    public void applyStyle() {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mRecyclerView.getChildAt(i);
            SegmentHolder holder = (SegmentHolder)mRecyclerView.getChildViewHolder(child);
            holder.applyStyle();

        }
    }

    /**
     *
     */
    private class SegmentAdapter extends RecyclerView.Adapter<SegmentHolder> {

        ArrayList<Segment> mList;

        Document mDocument;
        NoteComposeFragment20180107 mContext;

        SegmentAdapter(NoteComposeFragment20180107 context, Document note) {
            this.mDocument = note;
            this.mContext = context;

            this.mList = new ArrayList<>();
            mList.add(note.getTitle());
            mList.add(note.getSubtitle());
            mList.addAll(note.getBody());
        }

        int add(Segment entity, int index) {
            mList.add(index, entity);
            return mList.indexOf(entity);
        }

        int remove(String id, boolean notify) {
            int index = indexOf(id);
            if (index >= 0) {
                mList.remove(index);
            }

            if (notify) {
                mAdapter.notifyItemRemoved(index);
            }

            return index;
        }

        int indexOf(String id) {
            int size = mList.size();

            for (int i = 0; i < size; i++) {
                Segment s = mList.get(i);
                if (s.getId().equalsIgnoreCase(id)) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public int getItemViewType(int position) {
            Segment s = mList.get(position);
            int type = SegmentHolder.getType(this.mDocument, s);

            return type;
        }

        @Override
        public SegmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SegmentHolder holder = SegmentHolder.create(mContext, parent, viewType);

            return holder;
        }

        @Override
        public void onBindViewHolder(SegmentHolder holder, int position) {
            Segment s = mList.get(position);

            holder.bind(position, s);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public void onViewDetachedFromWindow(SegmentHolder holder) {
            super.onViewDetachedFromWindow(holder);

            holder.onViewDetachedFromWindow();
        }

        @Override
        public void onViewAttachedToWindow(SegmentHolder holder) {
            super.onViewAttachedToWindow(holder);
        }
    }

    void unlockContentHeight() {
        View view = this.getEntityContainer();

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = 1;

        // 判断软键盘是否显示，同步一次软键盘高度
        if (SoftKeyboardUtils.isVisible(this.getActivity())) {
            int value = ParagraphSegmentHolder.getSoftKeyboardHeight();
            int height = ParagraphSegmentHolder.getSoftKeyboardHeight(this.getActivity());
            if (height > 0 && height != value) {
                view.requestLayout();
            }
        }
    }

    Runnable mUnlockContentHeight = new Runnable() {

        @Override
        public void run() {
            unlockContentHeight();
        }
    };

    private class MyScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mMode == MODE_EDIT) {
                    if (mRecyclerView.getFocusedChild() == null
                            && !SoftKeyboardUtils.isVisible(getActivity())) {

//                        int count = mRecyclerView.getChildCount();
//                        for (int i = 0; i < count; i++) {
//                            View child = mRecyclerView.getChildAt(i);
//                            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(child);
//                            if (holder != null && holder instanceof ParagraphSegmentHolder) {
//
//                                ((ParagraphSegmentHolder)holder).requestFocus();
//                                break;
//                            }
//                        }
                    }
                }
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

            } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }
}
