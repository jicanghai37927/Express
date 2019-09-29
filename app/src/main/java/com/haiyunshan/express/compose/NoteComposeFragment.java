package com.haiyunshan.express.compose;


import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.haiyunshan.express.App;
import com.haiyunshan.express.NoteComposeActivity;
import com.haiyunshan.express.NoteSettingActivity;
import com.haiyunshan.express.NoteShareActivity;
import com.haiyunshan.express.R;
import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.app.SoftKeyboardUtils;
import com.haiyunshan.express.app.ToastUtils;
import com.haiyunshan.express.app.Utils;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.decoration.EntityDividerDecoration;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.compose.state.ComposeState;
import com.haiyunshan.express.compose.state.FormatState;
import com.haiyunshan.express.compose.state.InsertState;
import com.haiyunshan.express.compose.state.PageState;
import com.haiyunshan.express.compose.state.ReadState;
import com.haiyunshan.express.compose.utils.DocumentShot;
import com.haiyunshan.express.compose.utils.DocumentVoice;
import com.haiyunshan.express.compose.widget.DocumentView;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.fragment.BaseFragment;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.DocumentManager;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.note.segment.PictureSegment;
import com.haiyunshan.express.note.segment.StopSegment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteComposeFragment extends BaseFragment implements View.OnClickListener {

    static final String TAG = "NoteComposeFragment";

    private static final int DELAY_UNLOCK_HEIGHT = 200;

    private static final int REQUEST_MORE = 1003;
    private static final int REQUEST_PHOTO = 1004;

    public static final String argNoteId = "noteId";

    public DocumentView mRecyclerView;
    public DocumentLayoutManager mLayoutManager;
    public DocumentAdapter mAdapter;
    public EntityDividerDecoration mDividerDecor;

    public FrameLayout mFragmentLayout;
    BaseFragment mFragment;

    public View mTopBar;
    public View mBottomBar;

    public View mBackBtn;
    public View mDoneBtn;

    public View mPhotoBtn;
    public View mFormatBtn;
    public View mCreateBtn;
    public View mComposeBtn;
    public View mMoreBtn;

    public View mCaptureView;
    public TextView mVoiceView;

    public Handler mHandler;

    ArrayList<DocumentState> mStates;

    boolean mCollapsed;

    Document mDocument;
    Segment mFocus;

    DocumentVoice mVoice;

    NoteComposeActivity mParent;

    String action;

    public NoteComposeFragment() {
        this.mHandler = new Handler();

        this.mFragment = null;

        this.mCollapsed = false;
    }

    public void setArguments(NoteComposeActivity parent, Bundle args) {
        this.mParent = parent;

        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.mRecyclerView = (DocumentView)(view.findViewById(R.id.rv_list));
            mRecyclerView.setPreserveFocusAfterLayout(false);

            this.mLayoutManager = new DocumentLayoutManager(getActivity(), this);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        {
            this.mFragmentLayout = (FrameLayout)(view.findViewById(R.id.fragment_layout));
        }

        {
            this.mTopBar = view.findViewById(R.id.top_bar);

            boolean fitSystemWindow = false;
            if (fitSystemWindow) {
                int top = WindowUtils.getStatusBarHeight(getActivity());
                top = (top <= 0) ? getActivity().getResources().getDimensionPixelSize(R.dimen.status_bar_height) : top;
                int bottom = mTopBar.getPaddingBottom();
                int left = mTopBar.getPaddingLeft();
                int right = mTopBar.getPaddingRight();
                mTopBar.setPadding(left, top, right, bottom);
            }

            this.mBottomBar = view.findViewById(R.id.bottom_bar);
        }

        {

            this.mBackBtn = view.findViewById(R.id.btn_back);
            mBackBtn.setOnClickListener(this);

            this.mDoneBtn = view.findViewById(R.id.btn_done);
            mDoneBtn.setOnClickListener(this);

            this.mFormatBtn = view.findViewById(R.id.btn_format);
            mFormatBtn.setOnClickListener(this);

            this.mPhotoBtn = view.findViewById(R.id.btn_pic);
            mPhotoBtn.setOnClickListener(this);

            this.mCreateBtn = view.findViewById(R.id.btn_create);
            mCreateBtn.setOnClickListener(this);

            this.mComposeBtn = view.findViewById(R.id.btn_compose);
            mComposeBtn.setOnClickListener(this);

            this.mMoreBtn = view.findViewById(R.id.btn_more);
            mMoreBtn.setOnClickListener(this);

        }

        {
            this.mCaptureView = view.findViewById(R.id.btn_capture);
            mCaptureView.setOnClickListener(this);

            this.mVoiceView = view.findViewById(R.id.btn_voice);
            mVoiceView.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            String id = "3b77ff3e9b4a47eb9f0ebfa84a783da1";
            String noteId = getArguments().getString(argNoteId, id);

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
            this.mStates = new ArrayList<>();
            mStates.add(new ReadState(this));

            if (mDocument.isEmpty()) {
                mStates.add(new ComposeState(this)); 
            }
        }

        {
            this.mAdapter = new DocumentAdapter(this, mDocument);
            mRecyclerView.setAdapter(mAdapter);

            {
                EntityDividerDecoration decor = new EntityDividerDecoration(this.getActivity());
                float margin = mDocument.getPage().getPaddingLeft();
                margin = WindowUtils.dp2px(margin);
                margin = 0;
                decor.setMargin((int)margin, 0);
                mRecyclerView.addItemDecoration(decor);

                this.mDividerDecor = decor;
            }

            mRecyclerView.setNestedScrollingParent(mNestedScrollingParent);
        }

        if (!mStates.isEmpty()) {
            DocumentState state = mStates.get(mStates.size() - 1);
            state.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        // 保存
        this.saveNote();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mVoice != null) {
            mVoice.shutdown();
            mVoice = null;

            mVoiceView.setText("朗读");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((mFragment != null) && (mFragmentLayout.getVisibility() != View.VISIBLE)) {

            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.remove(mFragment);
            t.commit();

            mFragment = null;

        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // 回到阅读状态
        int count = App.instance().getActivityCount();
        LogUtils.w(TAG, "activity count = " + count);

        if (count == 0) {
            this.resetState();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MORE) {
            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    String action = data.getStringExtra("action");
                    if (action.equalsIgnoreCase(NoteSettingActivity.actionPageSetting)) {
                        PageState state = new PageState(this);
//                        this.pushState(state);
                        this.replaceState(state);
                    } else if (action.equalsIgnoreCase(NoteSettingActivity.actionCreate)) {
                        String noteId = data.getStringExtra("noteId");
                        mParent.showNote(noteId);
                    }
                }
            }
        } else if (requestCode == REQUEST_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {


                if(action.equals("android.intent.action.MULTIPLE_PICK")){
                    final Bundle extras = data.getExtras();
                    int count = extras.getInt("selectedCount");
                    ArrayList<String> items = extras.getStringArrayList("selectedItems");

                    LogUtils.w(TAG, count);
                    LogUtils.w(TAG, items);
                }else {
                    if (data != null && data.getData() != null) {

                        Uri uri = data.getData();
                        String uriString = uri.toString();
                        String path;

                        if(uriString.contains("content")){
                            path = getRealPathFromURI(data.getData());
                        }else {
                            path = uriString.replace("file://", "");
                        }


                        path = (path == null)? uriString: path;
                        LogUtils.w(TAG, path);

                        this.insertPhoto(path);

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                ArrayList<Uri> uris = new ArrayList<>();
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    uris.add(uri);
                                }


                                // Do someting
                            }
                        }
                    }
                }



            }
        }

    }

    public String getRealPathFromURI(Uri contentURI) {
        String path = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getActivity().getContentResolver().query(contentURI, projection, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = cursor.getString(idx);

            cursor.close();
        }

        return path;
    }

    public boolean onBackPressed() {
        if (mFragment != null) {
            boolean result = mFragment.onBackPressed();
            if (result) {
                return result;
            }
        }

        if (mFragmentLayout.getVisibility() == View.VISIBLE) {
            this.popState();
            return true;
        }

        if (mStates.size() > 1) {
            this.popState();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            this.getActivity().onBackPressed();
        } else if (v == mDoneBtn) {
            this.resetState();
        } else if (v == mFormatBtn) {
            FormatState state = new FormatState(this);
//            this.pushState(state);
            this.replaceState(state);
        } else if (v == mComposeBtn) {
            ComposeState state = new ComposeState(this);
//            this.pushState(state);
            this.replaceState(state);
        } else if (v == mCreateBtn) {
            InsertState state = new InsertState(this);
//            this.pushState(state);
            this.replaceState(state);
        } else if (v == mMoreBtn) {
            NoteSettingActivity.startForResult(this, REQUEST_MORE, mDocument.getId());
        } else if (v == mCaptureView) {
//            this.capture();
            NoteShareActivity.start(getActivity(), mDocument.getId());
        } else if (v == mVoiceView) {
            if (mVoice != null) {
                mVoice.shutdown();
                mVoice = null;

                mVoiceView.setText("朗读");
            } else {
                mVoice = new DocumentVoice(this);

                mVoiceView.setText("停止");

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ParagraphSegment segment = mVoice.start();

                        if (segment == null) {
                            mVoice.shutdown();
                            mVoice = null;

                            mVoiceView.setText("朗读");
                        }
                    }
                }, 100);

            }
        } else if (v == mPhotoBtn) {
            this.startGallery2();
        }
    }

    public void startGallery() {
        // 调用系统的相冊
        Intent intent = new Intent(Intent.ACTION_PICK, null);
//        intent = new Intent(Intent.ACTION_GET_CONTENT, null);

        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        // 调用剪切功能
        this.startActivityForResult(intent, REQUEST_PHOTO);
    }

    void startGallery2() {

        // Undocumented way to get multiple photo selections from Android Gallery ( on Samsung )
        Intent intent = new Intent("android.intent.action.MULTIPLE_PICK");//("Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        // Check to see if it can be handled...
        PackageManager manager = getActivity().getApplicationContext().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            // Ok, "android.intent.action.MULTIPLE_PICK" can be handled

            action = "android.intent.action.MULTIPLE_PICK";
        } else {

            action = Intent.ACTION_PICK;
         /* This is the documented way you are to get multiple images from a gallery BUT IT DOES NOT WORK with Android Gallery! (at least on Samsung )
           But the Android Email client WORKS! What the f'k!
               */
            intent.setAction(action);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Note: only supported after Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT, harmless if used below 19, but no mutliple selection supported
        }


        startActivityForResult(intent, REQUEST_PHOTO);
    }

    public void insertPhoto(String source) {
        Segment focus = this.getFocus();
        if (focus == null) {
            return;
        }

        SegmentHolder h = mRecyclerView.obtainHolder(focus);
        if (h == null || !(h instanceof ParagraphSegmentHolder)) {
            return;
        }

        ParagraphSegmentHolder holder = (ParagraphSegmentHolder)h;
        int start = holder.getSelectionStart();
        int end = holder.getSelectionEnd();
        if (start < 0 || end < 0 || (start != end)) {
            return;
        }

        ParagraphSegment en = holder.getSegment();
        CharSequence text = holder.mParagraphView.getText();

        int pos = start;
        if (pos == 0) { // 在开始插入
            this.doSplit(holder);
            this.doInsertPhoto(en, 1, source);
        } else if ((pos + 1) == (text.length())) { // 在末尾插入
            this.doSplit(holder);
            this.doInsertPhoto(en, 1, source);
        } else { // 在中间插入
            this.doSplit(holder);
            this.doInsertPhoto(en, 1, source);
        }

    }

    void doInsertPhoto(ParagraphSegment segment, int position, String source) {

        PictureSegment seg = null;

        // 更新数据
        {
            int pos = mDocument.indexOf(segment);
            if (pos >= 0) {
                seg = new PictureSegment(mDocument, source);
                if (position < 0) {
                    mDocument.add(seg, pos);
                } else {
                    mDocument.add(seg, pos + 1);
                }

                mDocument.save();
            }
        }

        // 更新界面
        if (seg != null) {
            int index = mAdapter.indexOf(segment.getId());
            if (index >= 0) {
                if (position < 0) {
                    mAdapter.add(seg, index);
                } else {
                    mAdapter.add(seg, index + 1);
                }
            }

            mAdapter.notifyItemRangeInserted(index, 1);
        }
    }

    void doSplit(ParagraphSegmentHolder target) {

        ParagraphSegmentHolder itemHolder = target;
        ParagraphSegment en = itemHolder.getSegment();
        if (mDocument.indexOf(en) >= 0) {
            ParagraphView edit = target.mParagraphView;

            Editable text = edit.getText();
            int start = edit.getSelectionStart();
            int end = edit.getSelectionEnd();

            if (text.length() != 0) {
                if (start == end) {
                    int pos = start;

                    CharSequence t1;
                    CharSequence t2;

                    t1 = text.subSequence(0, pos);
                    t2 = text.subSequence(pos, text.length());

                    this.splitParagraph(itemHolder, t1, t2);

                } else {
                    CharSequence t1 = text.subSequence(0, start);
                    CharSequence t2 = text.subSequence(start, end);
                    CharSequence t3 = text.subSequence(end, text.length());

                    this.splitParagraph(itemHolder, t1, t2, t3);
                }
            }

        } else {

        }
    }
    void splitParagraph(ParagraphSegmentHolder holder, CharSequence t1, CharSequence t2) {

        DocumentAdapter mAdapter = this.mAdapter;

        // 当前数据
        ParagraphSegment en = holder.getSegment();
        en.setText(t1);
        holder.mParagraphView.setText(t1);
        holder.mParagraphView.setSelection(t1.length());
        holder.mParagraphView.setMinLines(1);

        // 新的数据
        final ParagraphSegment entity = new ParagraphSegment(this.mDocument);
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

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                SegmentHolder itemHolder = mRecyclerView.obtainHolder(entity);
                if (itemHolder != null) {
                    itemHolder.requestFocus();
                }
            }
        });

    }
    void splitParagraph(ParagraphSegmentHolder holder, CharSequence t1, CharSequence t2, CharSequence t3) {

        DocumentAdapter mAdapter = this.mAdapter;

        // 当前数据
        ParagraphSegment en = holder.getSegment();
        en.setText(t1);
        holder.mParagraphView.setText(t1);
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

    public void capture() {
        DocumentShot shot = new DocumentShot(this);
        shot.capture();
        Bitmap bitmap = shot.getBitmap();
        if (bitmap == null) {
            return;
        }

        String name = shot.getName();
        final File file = Utils.savePicture(getActivity(), name, bitmap);
        shot.recycle();

        if (file == null) {
            String msg = getString(R.string.share_slogan_fail);
            ToastUtils.show(getActivity(), msg);
        } else {
            String msg = getString(R.string.share_slogan_msg_fmt, getString(R.string.app_name));
            Snackbar bar = Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT);
            bar.setAction(getString(R.string.share_slogan_action_view), new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    requestViewFile(file);
                }
            });
            bar.show();
        }
    }

    void requestViewFile(File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image/*");

        try {
            getActivity().startActivity(intent);
        } catch (Exception e) {

        }
    }

    public Document getDocument() {
        return this.mDocument;
    }

    public Segment getFocus() {
        return this.mFocus;
    }

    public void setFocus(Segment focus) {
        this.mFocus = focus;
    }

    public void replaceState(DocumentState state) {
        if (mStates.size() == 1) {
            this.pushState(state);
        } else {
            mStates.remove(mStates.size() - 1).end();
            mStates.add(state);
            state.start();
        }
    }

    public void pushState(DocumentState state) {
        if (!mStates.isEmpty()) {
            this.peekState().end();
        }

        {
            mStates.add(state);
            state.start();
        }
    }

    public void popState() {
        int size = mStates.size();
        if (size <= 1) {
            return;
        }

        mStates.remove(size - 1).end();
        mStates.get(mStates.size() - 1).start();
    }

    public void resetState() {
        if (mStates.size() == 1) {
            return;
        }

        // 通知最近的一个End
        mStates.remove(mStates.size() - 1).end();

        while (mStates.size() > 1) {
            mStates.remove(mStates.size() - 1);
        }

        mStates.get(0).start();
    }

    public DocumentState peekState() {
        if (mStates.isEmpty()) {
            return null;
        }
        
        int pos = mStates.size() - 1;
        return mStates.get(pos);
    }

    public Intent getResult() {
        Intent intent = new Intent();
        intent.putExtra(argNoteId, mDocument.getId());
        return intent;
    }

    public ParagraphSegment findFocus() {
        ParagraphSegment target = null;

        // 判断当前Focus是否可用
        if ((mFocus != null) && (mFocus instanceof ParagraphSegment)) {
            if (mRecyclerView.obtainHolder(mFocus) != null) {
                target = (ParagraphSegment)mFocus;
            }
        }

        if (target != null) {
            return target;
        }

        // 寻找合适的目标
        ParagraphSegmentHolder holder = mRecyclerView.peekTarget(ParagraphSegmentHolder.class);
        if (holder != null) {
            target = holder.getSegment();
        }

        return target;
    }

    public ParagraphSegmentHolder findParagraphHolder(ParagraphSegment segment) {
        SegmentHolder holder = mRecyclerView.obtainHolder(segment);
        return (ParagraphSegmentHolder)holder;
    }

    public void mergeParagraph(ParagraphSegment from, ParagraphSegment to) {
        int index = to.getText().length();

        SegmentHolder toHolder = mRecyclerView.obtainHolder(to);

        CharSequence t = from.getText();
        if (!TextUtils.isEmpty(t)) {
            Spanned text = to.getText();
            text = new SpannableStringBuilder(text).append(t);

            to.setText(text);
        }

        mDocument.remove(from);
        mAdapter.remove(from.getId(), true);

        if (toHolder != null) {
            ParagraphView mParagraphView = ((ParagraphSegmentHolder)toHolder).mParagraphView;
            mParagraphView.setText(to.getText());
            mParagraphView.setSelection(index);

            toHolder.requestFocus();

        } else {

            to.setSelection(index, index);
            mAdapter.notifyItemChanged(mAdapter.indexOf(to.getId()));
        }

        mDocument.save();
    }

    public void hideFragment() {
        boolean isStateSaved = this.isStateSaved();
        if (!isStateSaved) {
            if (mFragment != null) {
                FragmentManager fm = this.getChildFragmentManager();
                FragmentTransaction t = fm.beginTransaction();
                t.remove(mFragment);
                t.commit();

                mFragment = null;
            }
        }

        this.setExtra(false);

        this.setTopBar(true);

    }

    public boolean isCollapsed() {
        return mCollapsed;
    }

    public void collapse() {
        if (mCollapsed) {
            return;
        }

        FrameLayout mFragmentLayout = this.mFragmentLayout;
        mFragmentLayout.getLayoutParams().height = getActivity().getResources().getDimensionPixelSize(R.dimen.action_bar_height);
        mFragmentLayout.requestLayout();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mRecyclerView.getLayoutParams();
        params.bottomMargin = mFragmentLayout.getLayoutParams().height;
        mRecyclerView.requestLayout();

        this.mCollapsed = true;
    }

    public void expand() {
        if (!mCollapsed) {
            return;
        }

        FrameLayout mFragmentLayout = this.mFragmentLayout;
        mFragmentLayout.getLayoutParams().height = this.getExtraHeight();
        mFragmentLayout.requestLayout();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mRecyclerView.getLayoutParams();
        params.bottomMargin = mFragmentLayout.getLayoutParams().height;
        mRecyclerView.requestLayout();

        this.mCollapsed = false;
    }

    public final void saveNote() {

        {
            String name = mDocument.getTitle().getText().toString();
            String desc = mDocument.getSubtitle().getText().toString();

            NoteEntry entry = NoteManager.instance().obtain(mDocument.getId());
            if (entry != null) {
                entry.setName(name);
                entry.setDesc(desc);
            }
        }

        {
            RecyclerView mRecyclerView = this.mRecyclerView;
            int count = mRecyclerView.getChildCount();

            for (int i = 0; i < count; i++) {
                View view = mRecyclerView.getChildAt(i);
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                if (holder == null) {
                    continue;
                }

                if (holder instanceof ParagraphSegmentHolder) {
                    ParagraphSegmentHolder target = (ParagraphSegmentHolder)holder;
                    target.getSegment().setText(target.mParagraphView.getText());
                }
            }
        }

        {
            mDocument.save();
        }
    }


    public void showKeyboard(Segment focus) {
        if (SoftKeyboardUtils.isVisible(getActivity())) {
            return;
        }

        boolean extra = mFragmentLayout.getVisibility() == View.VISIBLE;

        if (extra) {
            this.lockHeight();
        }

        {
            SegmentHolder holder = mRecyclerView.obtainHolder(focus);
            if (holder != null && holder instanceof ParagraphSegmentHolder) {

                ParagraphSegmentHolder ph = (ParagraphSegmentHolder) holder;
                View itemView = ph.itemView;
                ParagraphView view = ph.mParagraphView;
                int y1 = view.getY(ph.getSegment().getSelectionStart());
                int y2 = view.getY(ph.getSegment().getSelectionEnd());

                if (y2 <= 0 || y1 >= this.mRecyclerView.getHeight()) {
                    int pos = 0;

                    int top = itemView.getTop();
                    if (top < 0) {
                        pos = view.getEnd(-top);
                    } else {
                        pos = view.getEnd(0);
                    }

                    view.setSelection(pos);
                }

                holder.requestFocus();
                SoftKeyboardUtils.show(getActivity());
            }
        }

        if (extra) {
            this.hideFragment();
            this.unlockHeight();
        }
    }

    public void showFragment(String tag, BaseFragment fragment) {

        boolean delay = false;
        if (delay) {
            this.showFragmentDelay(tag, fragment);
            return;
        }

        this.mFragment = fragment;

        //
        {
            FragmentManager fm = this.getChildFragmentManager();

            FragmentTransaction t = fm.beginTransaction();
            t.replace(mFragmentLayout.getId(), fragment, tag);
            t.commit();
        }

        // 显示面板
        boolean isKeyboardVisible = SoftKeyboardUtils.isVisible(getActivity());
        if (isKeyboardVisible) {

            // 锁定高度
            this.lockHeight();

            // 显示面板
            this.setExtra(true);

            // 隐藏输入法
            SoftKeyboardUtils.hide(getActivity());

            // 解除锁定
            mHandler.removeCallbacks(mUnlockHeightRunnable);
            mHandler.postDelayed(mUnlockHeightRunnable, DELAY_UNLOCK_HEIGHT);

        } else {
            this.setExtra(true);
        }

        if (mCollapsed) {
            this.expand();
        }

        // 隐藏顶部条
        this.setTopBar(false);

        // 重新布局
        if (!isKeyboardVisible) {
            getView().requestLayout();
        }
    }

    void showFragmentDelay(String tag, BaseFragment fragment) {

        this.mFragment = fragment;

        //
        {
            FragmentManager fm = this.getChildFragmentManager();

            FragmentTransaction t = fm.beginTransaction();
            t.replace(mFragmentLayout.getId(), fragment, tag);
            t.commit();
        }

        // 显示面板
        boolean isKeyboardVisible = SoftKeyboardUtils.isVisible(getActivity());
        if (isKeyboardVisible) {
            SoftKeyboardUtils.hide(getActivity());
        }

        if (isKeyboardVisible) {
            mHandler.removeCallbacks(mShowFragmentRunnable);
            mHandler.postDelayed(mShowFragmentRunnable, DELAY_UNLOCK_HEIGHT);
        } else {
            this.setExtra(true);
            this.setTopBar(false);
        }
    }

    void setTopBar(boolean visible) {

        View view = this.mTopBar;

        boolean v = view.getVisibility() == View.VISIBLE;
        if (!(v ^ visible)) {
            return;
        }

        if (visible) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }

        } else {
            if (view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.GONE);
            }
        }
    }

    public void setBottomBar(boolean visible) {

        View view = this.mBottomBar;

        boolean v = view.getVisibility() == View.VISIBLE;
        if (!(v ^ visible)) {
            return;
        }

        if (visible) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }

        } else {
            if (view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.GONE);
            }
        }
    }

    void setExtra(boolean visible) {

        View view = this.mFragmentLayout;

        boolean old = (view.getVisibility() == View.VISIBLE);
        if (!(old ^ visible)) {
            return;
        }

        if (visible) {
            int height = this.getExtraHeight();
            view.getLayoutParams().height = height;
            view.setVisibility(View.VISIBLE);

        } else {
            int height = 0;
            view.getLayoutParams().height = height;
            view.setVisibility(View.GONE);
        }

        //
        {
            int height = 0;
            if (view.getVisibility() == View.VISIBLE) {
                height = view.getLayoutParams().height;
            }

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            params.bottomMargin = height;
        }
    }

    public void lockHeight() {
        View view = this.getView();

        int height = view.getHeight();
        boolean isKeyboardVisible = SoftKeyboardUtils.isVisible(getActivity());
        if (isKeyboardVisible) {
            height += SoftKeyboardUtils.getHeight(getActivity());
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;

    }

    public void postUnlockHeight() {
        mHandler.removeCallbacks(mUnlockHeightRunnable);
        mHandler.postDelayed(mUnlockHeightRunnable, DELAY_UNLOCK_HEIGHT);
    }

    void unlockHeight() {
        View view = this.getView();

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        view.requestLayout();
    }

    public int getExtraHeight() {
        int height = getResources().getDimensionPixelSize(R.dimen.extra_height);

        {
            int keyboardHeight = SoftKeyboardUtils.getHeight(getActivity());
            int max = (int) (1.2f * height);
            int min = (int) (0.8f * height);
            if (keyboardHeight >= min && keyboardHeight <= max) {
                height = keyboardHeight;
            }
        }

        return height;
    }

    public final ParagraphSegmentHolder find(View view) {
        if (view == null) {
            return null;
        }

        if (mRecyclerView.getChildCount() == 0) {
            return null;
        }

        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mRecyclerView.getChildAt(i);
            SegmentHolder holder = mRecyclerView.getChildViewHolder(child);
            if (!holder.hasChild(view)) {
                continue;
            }

            if (holder instanceof ParagraphSegmentHolder) {
                ParagraphSegmentHolder h = (ParagraphSegmentHolder) holder;
                return h;
            }

            break;
        }

        return null;
    }


    public final void requestDelete(ParagraphSegmentHolder target) {

        ParagraphSegment seg = target.getSegment();
        ParagraphView mParagraphView = target.mParagraphView;

        // 判断是否是第一个段落
        int index = mDocument.indexOf(seg);
        if (index <= 0) {
            if (mParagraphView.length() != 0
                    && mParagraphView.getText().charAt(0) =='\n') {
                mParagraphView.delete(0, 1);
            }

        } else {

            // 判断是否是段落
            Segment en = mDocument.get(index - 1);
            if (en.getType() == Segment.TYPE_PARAGRAPH) {
                this.mergeParagraph(seg, (ParagraphSegment) en);
            } else {
                if (mParagraphView.length() != 0
                        && mParagraphView.getText().charAt(0) =='\n') {
                    mParagraphView.delete(0, 1);
                }
            }
        }

    }

    void showFragment() {
        this.setExtra(true);
        this.setTopBar(false);
    }

    Runnable mUnlockHeightRunnable = new Runnable() {

        @Override
        public void run() {
            unlockHeight();
        }

    };

    Runnable mShowFragmentRunnable = new Runnable() {

        @Override
        public void run() {
            showFragment();
        }

    };

    NestedScrollingParent mNestedScrollingParent = new NestedScrollingParent() {

        @Override
        public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
            DocumentState state = peekState();
            if (state != null) {
                return state.onStartNestedScroll(child, target, nestedScrollAxes);
            }

            return false;
        }

        @Override
        public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
            DocumentState state = peekState();
            if (state != null) {
                state.onNestedScrollAccepted(child, target, nestedScrollAxes);
            }
        }

        @Override
        public void onStopNestedScroll(View target) {
            DocumentState state = peekState();
            if (state != null) {
                state.onStopNestedScroll(target);
            }
        }

        @Override
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            DocumentState state = peekState();
            if (state != null) {
                state.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            }
        }

        @Override
        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
            DocumentState state = peekState();
            if (state != null) {
                state.onNestedPreScroll(target, dx, dy, consumed);
            }
        }

        @Override
        public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
            DocumentState state = peekState();
            if (state != null) {
                return state.onNestedFling(target, velocityX, velocityY, consumed);
            }

            return false;
        }

        @Override
        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            DocumentState state = peekState();
            if (state != null) {
                return state.onNestedPreFling(target, velocityX, velocityY);
            }

            return false;
        }

        @Override
        public int getNestedScrollAxes() {
            DocumentState state = peekState();
            if (state != null) {

            }

            return ViewCompat.SCROLL_AXIS_VERTICAL;
        }
    };
}
