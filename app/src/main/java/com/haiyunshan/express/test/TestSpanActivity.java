package com.haiyunshan.express.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.TabStopSpan;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.compose.widget.ParagraphView;
import com.haiyunshan.express.style.highlight.HighlightSpan;

public class TestSpanActivity extends AppCompatActivity {

    static final String TAG = "TestSpanActivity";

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_span);

        initParagraphView();
//        initEditText();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        LogUtils.w(TAG, "onCreateContextMenu");
    }

    void initParagraphView() {
        findViewById(R.id.sv_edit_text).setVisibility(View.GONE);
        findViewById(R.id.sv_pv_paragraph).setVisibility(View.VISIBLE);

        final ParagraphView mParagraphView = findViewById(R.id.pv_paragraph);

        CharSequence s = this.createText();
        mParagraphView.setText(s);

        mParagraphView.getView().setCustomSelectionActionModeCallback(this.mSelectionAction);

        this.mTextView = mParagraphView.getView();
        mTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);
        mTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                HighlightSpan[] array = s.getSpans(0, s.length(), HighlightSpan.class);
//                for (HighlightSpan span: array) {
//                    int start = s.getSpanStart(span);
//                    int end = s.getSpanEnd(span);
//
//                    LogUtils.w("TestSpanActivity", start + ", " + end);
//                }

                boolean value = mParagraphView.textCanBeSelected();
                LogUtils.w(TAG, "textCanBeSelected = " + value);
            }
        });
    }

    void initEditText() {
        findViewById(R.id.sv_edit_text).setVisibility(View.VISIBLE);
        findViewById(R.id.sv_pv_paragraph).setVisibility(View.GONE);

        EditText mEditText = findViewById(R.id.edit_text);

        CharSequence s = this.createText();
        mEditText.setText(new SpannableStringBuilder(s));
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                HighlightSpan[] array = s.getSpans(0, s.length(), HighlightSpan.class);
//                for (HighlightSpan span: array) {
//                    int start = s.getSpanStart(span);
//                    int end = s.getSpanEnd(span);
//
//                    LogUtils.w("TestSpanActivity", start + ", " + end);
//                }

            }
        });

        this.mTextView = mEditText;
    }

    CharSequence createText() {
        String text = "掌权的第一年，他总在四下无人时，宣念着变革的决心，看着昔日同道，以嘲讽语气，恭喜着高位上的自己，他在永昼的慈光之塔，为自己点起一盏小烛；" +
                "\n\n\t第二年，耳边常回响着不谅解的声音，一道道回过身去的背影，他们说“错看了，无法认清你了，原来你是这种人。”这是必然的过程，为什么还是会对这过程耿耿于怀，权利熏心吗？他不由自问；" +
                "\n\n第三年，在惊涛骇浪中。他如愿掌了舵，但掌舵的手，却从此有了一股涤洗不去的腥味，他时常为这股血腥，而浅眠、而惊醒。这一年，一切如愿，嗅觉却出了问题。" +
                "\n\n从此，他只反复的记着这三年，眼里、心里、却再也看不清，永昼中点起小烛的意义。" +
                "\n\n此后，岁月不堪记，无衣师尹不堪提……";

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        HighlightSpan drawingSpan = new HighlightSpan(getDrawable(R.drawable.bdreader_note_bg_brown_1));

        int start = 21;
        int end = 79;

//        ssb.setSpan(drawingSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ssb.setSpan(new TabStopSpan.Standard(240), 0, ssb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        return ssb;
    }


    void highlight() {

        Spannable text = ((Spannable)(mTextView.getText()));

        int start = mTextView.getSelectionStart();
        int end = mTextView.getSelectionEnd();

        int min = Math.max(0, Math.min(start, end));
        int max = Math.max(0, Math.max(start, end));

        if (min != max) {
            HighlightSpan span = new HighlightSpan(getDrawable(R.drawable.bdreader_note_bg_brown_1));
            text.setSpan(span, min, max, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }


        LogUtils.w(TAG, "highlight = " + min + ", " + max);
    }

    ActionMode.Callback mSelectionAction = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.clear();

            menu.add(Menu.NONE, 1001, 0, "高亮");
            menu.add(Menu.NONE, android.R.id.copy, 0, android.R.string.copy);
            menu.add(Menu.NONE, android.R.id.paste, 0, android.R.string.paste);
            menu.add(Menu.NONE, android.R.id.selectAll, 0, android.R.string.selectAll);
            menu.add(Menu.NONE, android.R.id.cut, 0, android.R.string.cut);

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == android.R.id.selectAll
                    || itemId == android.R.id.copy
                    || itemId == android.R.id.paste
                    || itemId == android.R.id.cut) {
                return false;
            }

            if (itemId == 1001) {
                highlight();

            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

}
