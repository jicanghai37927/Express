package com.haiyunshan.express;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiyunshan.express.app.ScreenShot;
import com.haiyunshan.express.app.ToastUtils;
import com.haiyunshan.express.app.Utils;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.TitleSegmentHolder;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.DocumentManager;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.share.SinaWeibo;
import com.haiyunshan.express.share.SinaWeiboSender;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoteShareActivity extends BaseActivity implements View.OnClickListener {

    View mBackBtn;
    TextView mSelectBtn;

    View mCaptureBtn;
    View mShareBtn;

    ViewPager mViewPager;
    SharePagerAdapter mAdapter;

    SinaWeiboSender mWeiboSender;

    Document mDocument;
    ArrayList<Section> mList;

    public static final void start(Activity context, String id) {
        Intent intent = new Intent(context, NoteShareActivity.class);
        intent.putExtra("id", id);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_share);

        {
            this.mBackBtn = findViewById(R.id.btn_back);
            mBackBtn.setOnClickListener(this);

            this.mSelectBtn = findViewById(R.id.btn_select);
            mSelectBtn.setOnClickListener(this);
        }


        {
            this.mCaptureBtn = findViewById(R.id.btn_capture);
            mCaptureBtn.setOnClickListener(this);

            this.mShareBtn = findViewById(R.id.btn_share);
            mShareBtn.setOnClickListener(this);
        }

        {
            this.mViewPager = findViewById(R.id.vp_pager);
            mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.inset_2l));
        }

        {
            Intent intent = getIntent();
            String id = intent.getStringExtra("id");
            id = (TextUtils.isEmpty(id))? "39df7dd0fed54933844d9d1ae2886a4a": id;
            if (!TextUtils.isEmpty(id)) {
                this.mDocument = DocumentManager.instance().obtain(id);
            }

            if (mDocument != null) {
                this.mList = this.getList(mDocument);
            }
        }

        if (!mList.isEmpty()) {
            this.mAdapter = new SharePagerAdapter(this, mDocument, mList);
            mViewPager.setAdapter(mAdapter);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == this.mBackBtn) {
            this.onBackPressed();
        } else if (v == this.mSelectBtn) {
            boolean value = mAdapter.isAllChecked();
            value = !value;
            mAdapter.setSelected(value);

            CharSequence text = value? "全不选": "全选";
            mSelectBtn.setText(text);

        } else if (v == this.mCaptureBtn) {
            this.capture();
        } else if (v == this.mShareBtn) {
            this.share();
        }
    }

    View createView(Activity context, LinearLayout parent, Segment segment) {
        View view = null;

        int type = segment.getType();
        if (type == Segment.TYPE_PARAGRAPH) {
            ParagraphSegment seg = (ParagraphSegment)segment;
            view = ParagraphSegmentHolder.create(context, parent, seg);
        }

        return view;
    }

    public void share() {
        if (mWeiboSender == null) {
            mWeiboSender = SinaWeibo.instance().getSender(this);
        }


        String title = mDocument.getTitle().getText().toString();
        String msg = "";
//        msg = "清净慧菩萨保佑，分享成功 2 次。";
        ArrayList<Uri> list = new ArrayList<>();

        {
            ArrayList<File> array = new ArrayList<>();

            SectionHolder current = mAdapter.getCurrent();

            int size = mAdapter.getCount();
            for (int i = size - 1; i >= 0; i--) {
                SectionHolder holder = mAdapter.mList.get(i);

                if (!holder.isChecked() && (holder != current)) {
                    continue;
                }

                File file = saveTemp(holder);
                if (file != null) {
                    array.add(0, file);
                }
            }

            for (File file : array) {
                list.add(Uri.fromFile(file));
            }
        }

        if (!list.isEmpty()) {
            mWeiboSender.send(title, msg, list);
        }
    }

    public void capture() {
        ArrayList<File> list = new ArrayList<>();

        SectionHolder current = mAdapter.getCurrent();
        File target = null;

        int size = mAdapter.getCount();
        for (int i = size - 1; i >= 0; i--) {
            SectionHolder holder = mAdapter.mList.get(i);

            if (!holder.isChecked() && (holder != current)) {
                continue;
            }

            File file = capture(holder);
            if (file != null) {
                list.add(0, file);
            }

            if (holder == current) {
                target = file;
            }
        }

        if (list.isEmpty()) {
            String msg = getString(R.string.share_slogan_fail);
            ToastUtils.show(this, msg);
        } else {

            final File temp = (target != null)? target: list.get(0);

            String msg = getString(R.string.share_slogan_msg_fmt, getString(R.string.app_name));
            Snackbar bar = Snackbar.make(mSelectBtn, msg, Snackbar.LENGTH_SHORT);
            bar.setAction(getString(R.string.share_slogan_action_view), new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    requestViewFile(temp);
                }
            });
            bar.show();
        }
    }

    File saveTemp(SectionHolder holder) {

        View view = holder.mView;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {

            view.measure(
                    View.MeasureSpec.makeMeasureSpec(mViewPager.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0,
                    view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        String name = mDocument.getId() + "_" + (holder.mSection.mIndex + 1);
        ScreenShot shot = new ScreenShot(this, view.findViewById(R.id.scroll_content), name);
        shot.capture();
        File file = shot.saveTemp();

        return file;
    }

    File capture(SectionHolder holder) {

        View view = holder.mView;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {

            view.measure(
                    View.MeasureSpec.makeMeasureSpec(mViewPager.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0,
                    view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        String name = mDocument.getId() + "_" + (holder.mSection.mIndex + 1);
        ScreenShot shot = new ScreenShot(this, view.findViewById(R.id.scroll_content), name);
        shot.capture();
        Bitmap bitmap = shot.getBitmap();
        if (bitmap == null) {
            return null;
        }

        name = shot.getName();
        final File file = Utils.savePicture(this, name, bitmap);
        shot.recycle();

        return file;
    }

    void requestViewFile(File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image/*");

        try {
            this.startActivity(intent);
        } catch (Exception e) {

        }
    }

    String getTimeText(Activity context, Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day -= 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);


        String[] array = context.getResources().getStringArray(R.array.day_of_week);
        String text = array[day];

        text = String.format("%1$s %2$02d:%3$02d", text, hour, min);
        return text;
    }

    ArrayList<Section> getList(Document document) {
        ArrayList<Section> list;

        if (document.hasStop()) {
            list = new ArrayList<>();

            int index = 0;
            Section section = new Section(index);

            int pos = 0;
            while (true) {
                if (pos >= document.size()) {
                    break;
                }

                Segment segment = document.getBody().get(pos);
                if (segment.getType() == Segment.TYPE_STOP) {
                    if (!section.mList.isEmpty()) {
                        list.add(section);

                        ++index;
                        section = new Section(index);
                    }
                } else {
                    section.mList.add(segment);
                }

                ++pos;
            }

            if (!section.mList.isEmpty()) {
                list.add(section);
            }
        } else {
            list = new ArrayList<>();
            list.add(new Section(document));
        }

        return list;
    }

    SectionHolder createHolder(Section section, Calendar calendar) {
        SectionHolder holder = new SectionHolder(this, section, calendar);
        return holder;
    }

    void onCheckedChanged(SectionHolder holder, boolean isChecked) {
        boolean value = mAdapter.isAllChecked();

        CharSequence text = value? "全不选": "全选";
        mSelectBtn.setText(text);

    }

    private static class SharePagerAdapter extends PagerAdapter {

        SectionHolder mCurrent;
        ArrayList<SectionHolder> mList;

        SharePagerAdapter(NoteShareActivity context, Document document, List<Section> list) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            int size = list.size();

            this.mList = new ArrayList<>(list.size());

            for (int i = 0; i < size; i++) {
                Section section = list.get(i);
                mList.add(context.createHolder(section, calendar));
            }
        }

        boolean isAllChecked() {
            for (SectionHolder holder : mList) {
                if (!holder.isChecked()) {
                    return false;
                }
            }

            return true;
        }

        void setSelected(boolean value) {
            for (SectionHolder holder : mList) {
                holder.mCheckBox.setOnCheckedChangeListener(null);

                holder.mCheckBox.setChecked(value);

                holder.mCheckBox.setOnCheckedChangeListener(holder);
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = mList.get(position).mView;
            container.addView(v);
            return mList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = ((SectionHolder)object).mView;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((SectionHolder)object).mView;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            this.mCurrent = (SectionHolder) object;
        }

        public SectionHolder getCurrent() {
            return mCurrent;
        }
    }

    private static class SectionHolder implements CompoundButton.OnCheckedChangeListener {

        NoteShareActivity mContext;
        Section mSection;

        View mView;
        CheckBox mCheckBox;

        public SectionHolder(NoteShareActivity context, Section section, Calendar calendar) {
            this.mContext = context;
            this.mSection = section;

            this.mView = this.createView(context.mDocument, section, section.mIndex, context.mList.size(), calendar);
            this.mCheckBox = mView.findViewById(R.id.cb_check);
            mCheckBox.setChecked(true);

            mCheckBox.setOnCheckedChangeListener(this);
        }

        public boolean isChecked() {
            return mCheckBox.isChecked();
        }

        private View createView(Document document, Section section, int position, int size, Calendar calendar) {


            View view = mContext.getLayoutInflater().inflate(R.layout.layout_share, mContext.mViewPager, false);


            {
                TextView monthView = view.findViewById(R.id.tv_month);
                TextView dayView = view.findViewById(R.id.tv_day);
                TextView yearView = view.findViewById(R.id.tv_year);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                month += 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                monthView.setText(String.valueOf(month));
                dayView.setText(String.valueOf(day));
                yearView.setText(String.valueOf(year));
            }

            {

                TextView extraView = (view.findViewById(R.id.tv_extra));

                CharSequence text = null;
                if (size > 1) {
                    text = String.format("第 %1$d 节，共 %2$d 节", (position + 1), size);
                }

                extraView.setText(text);
            }

            {
                TextView titleView = (view.findViewById(R.id.tv_title));

                CharSequence time = mContext.getTimeText(mContext, calendar);
                titleView.setText(time);
            }

            {
                TextView subtitleView = (view.findViewById(R.id.tv_subtitle));
                CharSequence text;

                int total = document.getCount();

                if (size == 1) {
                    text = String.format("全文 %1$d 个字", total);
                } else {
                    int count = section.getCount();
                    if (count <= 0) {
                        if (total <= 0) {
                            text = null;
                        } else {
                            text = String.format("全文 %1$d 个字", total);
                        }
                    } else {
                        if (total <= 0) {
                            text = String.format("本节 %1$d 个字", count);
                        } else {
                            text = String.format("本节 %1$d 个字，全文 %2$d 个字", count, total);
                        }
                    }
                }


                subtitleView.setText(text);

                boolean isEmpty = TextUtils.isEmpty(subtitleView.getText());
                subtitleView.setVisibility(isEmpty? View.GONE: View.VISIBLE);
            }

            {
                View v = view.findViewById(R.id.scroll_content);
                int height = WindowUtils.getRealHeight(mContext);
                v.setMinimumHeight(height);
            }

            if (document != null) {
                LinearLayout mContentLayout = (view.findViewById(R.id.share_layout));

                {
                    ParagraphSegment segment = document.getTitle();
                    View v = TitleSegmentHolder.create(mContext, mContentLayout, segment);
                    if (v != null) {
                        mContentLayout.addView(v);
                    }
                }

                {
                    ParagraphSegment segment = document.getSubtitle();
                    View v = TitleSegmentHolder.create(mContext, mContentLayout, segment);
                    if (v != null) {
                        mContentLayout.addView(v);
                    }
                }

                {
                    List<Segment> list = section.mList;
                    for (Segment segment : list) {
                        View v = mContext.createView(mContext, mContentLayout, segment);
                        if (v == null) {
                            continue;
                        }

                        mContentLayout.addView(v);
                    }
                }

            }

            return view;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mContext.onCheckedChanged(this, isChecked);
        }
    }

    private static class Section {

        int mIndex;
        ArrayList<Segment> mList;

        public Section(Document document) {
            this.mIndex = 0;
            this.mList = new ArrayList<>(document.getBody());
        }

        public Section(int index) {
            this.mIndex = index;
            this.mList = new ArrayList<>();
        }

        int getCount() {

            int count = 0;
            List<Segment> list = this.mList;
            for (Segment segment : list) {
                if (segment.getType() != Segment.TYPE_PARAGRAPH) {
                    continue;
                }

                ParagraphSegment seg = (ParagraphSegment)(segment);
                CharSequence cs = seg.getText();
                if (TextUtils.isEmpty(cs)) {
                    continue;
                }

                int length = cs.length();
                for (int i = 0; i < length; i++) {
                    char c = cs.charAt(i);

                    if (Utils.isChinese(c)) {
                        ++count;
                    }
                }
            }

            return count;
        }
    }
}
