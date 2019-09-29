package com.haiyunshan.express;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.haiyunshan.express.dataset.DataAccessor;
import com.haiyunshan.express.dataset.catalog.CatalogEntry;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.Identifier;
import com.haiyunshan.express.dataset.note.Note;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.dataset.template.TemplateEntry;
import com.haiyunshan.express.dataset.template.TemplateManager;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.DocumentManager;

public class NoteSettingActivity extends BaseActivity implements View.OnClickListener {

    public static final String actionPageSetting = "action.pageSetting";
    public static final String actionCreate = "action.create";

    View mDoneBtn;

    View mTypefaceItem;
    View mStyleItem;
    View mPageItem;
    View mVoiceItem;

    View mCreateItem;

    Intent mResultIntent;

    Document mDocument;

    public static final void startForResult(Fragment f, int requestCode, String noteId) {
        Intent intent = new Intent(f.getActivity(), NoteSettingActivity.class);
        intent.putExtra("noteId", noteId);

        f.startActivityForResult(intent, requestCode);
        f.getActivity().overridePendingTransition(R.anim.push_in_up, R.anim.standby);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_setting);

        {
            String noteId = getIntent().getStringExtra("noteId");
            this.mDocument = DocumentManager.instance().obtain(noteId);
        }

        this.mDoneBtn = findViewById(R.id.btn_done);
        mDoneBtn.setOnClickListener(this);

        this.mTypefaceItem = findViewById(R.id.item_typeface);
        mTypefaceItem.setOnClickListener(this);

        this.mStyleItem = findViewById(R.id.item_style);
        mStyleItem.setOnClickListener(this);

        this.mPageItem = findViewById(R.id.item_page_setting);
        mPageItem.setOnClickListener(this);

        this.mVoiceItem = findViewById(R.id.item_voice_setting);
        mVoiceItem.setOnClickListener(this);

        this.mCreateItem = findViewById(R.id.item_create);
        mCreateItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == this.mDoneBtn) {
            this.onBackPressed();
        } else if (v == mTypefaceItem) {
            TypefaceActivity.start(this);
        } else if (v == mStyleItem) {
            NoteStyleActivity.start(this, getIntent().getStringExtra("noteId"));
        } else if (v == mPageItem) {
            this.setResultAction(actionPageSetting, null);

            this.onBackPressed();
        } else if (v == mCreateItem) {
            this.requestCreate();

            this.onBackPressed();
        } else if (v == mVoiceItem) {
            String action = "com.android.settings.TTS_SETTINGS";

            try {
                startActivity(new Intent(action));
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mResultIntent == null) {
            this.setResult(RESULT_OK);
        } else {
            this.setResult(RESULT_OK, mResultIntent);
        }

        super.onBackPressed();
        this.overridePendingTransition(R.anim.standby, R.anim.push_out_down);
    }

    void setResultAction(String action, Bundle args) {
        if (this.mResultIntent == null) {
            mResultIntent = new Intent();
        }

        mResultIntent.putExtra("action", action);
        if (args != null) {
            mResultIntent.putExtras(args);
        }
    }

    void requestCreate() {
        String templateId = mDocument.getTemplateId();
        TemplateEntry t = TemplateManager.instance().obtain(templateId);
        t = (t == null)? TemplateManager.instance().getDefault(): t;

        NoteEntry e = NoteManager.instance().obtain(mDocument.getId());
        CatalogEntry catalog = CatalogManager.instance().obtain(e.getCatalog());

        {
            e = NoteManager.instance().create(catalog, t.getName());
            NoteManager.instance().save();
        }

        {
            DataAccessor accessor = DataAccessor.instance();
            Identifier id = new Identifier(e.getId(), t.getId());
            Note note = mDocument.toTemplate();

            accessor.saveNoteId(id);
            accessor.saveNoteStyle(e.getId(), mDocument.getStyle());
            accessor.saveNotePage(e.getId(), mDocument.getPage());
            accessor.saveNoteFile(e.getId(), note);
        }

        Bundle args = new Bundle();
        args.putString("noteId", e.getId());
        this.setResultAction(actionCreate, args);
    }
}
