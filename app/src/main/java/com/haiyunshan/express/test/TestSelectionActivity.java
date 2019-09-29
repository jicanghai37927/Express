package com.haiyunshan.express.test;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haiyunshan.express.R;
import com.haiyunshan.express.compose.widget.ParagraphView;

public class TestSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selection);

//        testTextView();
//        testEditText();
        testParagraphView();
    }

    void testTextView() {
        TextView view = findViewById(R.id.tv_page);
        view.setVisibility(View.VISIBLE);
        view.setText(getText());


        view.setTextIsSelectable(true);
        view.setCustomSelectionActionModeCallback(mSelectionAction);

    }

    void testEditText() {
        EditText view = findViewById(R.id.et_page);
        view.setVisibility(View.VISIBLE);
        view.setText(getText());

        view.setShowSoftInputOnFocus(false);
        view.setCursorVisible(false);

        view.setCustomSelectionActionModeCallback(mSelectionAction);

    }

    void testParagraphView() {
        ParagraphView view = findViewById(R.id.pv_page);
        view.setVisibility(View.VISIBLE);
        view.setText(getText());

//        view.setShowSoftInputOnFocus(false);
//        view.setCursorVisible(false);
        view.setEditable(false);
//        view.setEditable(true);

        view.getView().setCustomSelectionActionModeCallback(mSelectionAction);

    }

    CharSequence getText() {
        String text = "掌权的第一年，他总在四下无人时，宣念着变革的决心，看着昔日同道，以嘲讽语气，恭喜着高位上的自己，他在永昼的慈光之塔，为自己点起一盏小烛；" +
                "\n\n\t第二年，耳边常回响着不谅解的声音，一道道回过身去的背影，他们说“错看了，无法认清你了，原来你是这种人。”这是必然的过程，为什么还是会对这过程耿耿于怀，权利熏心吗？他不由自问；" +
                "\n\n第三年，在惊涛骇浪中。他如愿掌了舵，但掌舵的手，却从此有了一股涤洗不去的腥味，他时常为这股血腥，而浅眠、而惊醒。这一年，一切如愿，嗅觉却出了问题。" +
                "\n\n从此，他只反复的记着这三年，眼里、心里、却再也看不清，永昼中点起小烛的意义。" +
                "\n\n此后，岁月不堪记，无衣师尹不堪提……";

        return text;
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
            menu.add(Menu.NONE, android.R.id.selectAll, 0, android.R.string.selectAll);

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


            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
}
