package com.haiyunshan.express;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.note.DocumentManager;

public class NoteComposeActivity extends BaseActivity {

    FrameLayout mContainer;

    public static final void startForCompose(Fragment fragment, int requestCode, String noteId) {
        Activity context = fragment.getActivity();

        Intent intent = new Intent(context, NoteComposeActivity.class);
        if (!TextUtils.isEmpty(noteId)) {
            intent.putExtra(NoteComposeFragment.argNoteId, noteId);
        }

        fragment.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Log.w("Compose", noteId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_compose);

        this.mContainer = (FrameLayout)(findViewById(R.id.root_container));

        this.showNote(getIntent().getStringExtra(NoteComposeFragment.argNoteId));
    }

    @Override
    public void onBackPressed() {
        Fragment f = this.getSupportFragmentManager().findFragmentByTag("note_compose");
        if (f == null) {
            super.onBackPressed();
            return;
        }

        NoteComposeFragment fragment = (NoteComposeFragment)f;
        if (fragment.onBackPressed()) {
            return;
        }

        {
            DocumentManager.instance().clearCache();
            NoteManager.instance().save();
        }

        Intent intent = fragment.getResult();
        this.setResult(RESULT_OK, intent);

        super.onBackPressed();

        Activity context = this;
        context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void showNote(String noteId) {
        Bundle args = new Bundle();
        args.putString(NoteComposeFragment.argNoteId, noteId);

        FragmentManager fm = this.getSupportFragmentManager();

        NoteComposeFragment f = new NoteComposeFragment();
        f.setArguments(this, args);

        FragmentTransaction t = fm.beginTransaction();
        t.replace(mContainer.getId(), f, "note_compose");
        t.commit();
    }
}
