package com.haiyunshan.express.fragment.note;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.haiyunshan.express.R;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;

/**
 *
 */
public class TextFormatFragment extends Fragment {

    FrameLayout mContainer;

    NoteComposeFragment mParent;
    ParagraphSegment mSegment;

    public TextFormatFragment() {

    }

    public void setArguments(NoteComposeFragment parent, ParagraphSegment segment) {
        this.mParent = parent;
        this.mSegment = segment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_format, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mContainer = (FrameLayout)(view.findViewById(R.id.fragment_container));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            FragmentManager fm = this.getChildFragmentManager();

            ParagraphFragment f = new ParagraphFragment();
            f.setArguments(this, mSegment);

            FragmentTransaction t = fm.beginTransaction();
            t.add(mContainer.getId(), f, "paragraph");
            t.commit();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public NoteComposeFragment getParent() {
        return this.mParent;
    }

    public boolean onBackPressed() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            return false;
        }

        fm.popBackStack();
        return true;
    }

    public void collapse() {
        FragmentManager fm = this.getChildFragmentManager();
        if (fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        }
    }

    public Segment getEntity() {
        return this.mSegment;
    }

    void showStyle() {
        FragmentManager fm = getChildFragmentManager();
        StyleFragment f = new StyleFragment();
        f.setArguments(this, mSegment);

        FragmentTransaction t = fm.beginTransaction();

        t.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        t.replace(mContainer.getId(), f, "style");

        t.addToBackStack("style");
        t.commit();

    }

    void showTypeface() {
        FragmentManager fm = getChildFragmentManager();
        TypefaceFragment f = new TypefaceFragment();
        f.setArguments(this, mSegment);

        FragmentTransaction t = fm.beginTransaction();

        t.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        t.replace(mContainer.getId(), f, "typeface");

        t.addToBackStack("typeface");
        t.commit();

    }

    public void back() {
        FragmentManager fm = getChildFragmentManager();
        fm.popBackStack();
    }

    public void close() {
        mParent.hideTextFormat();

        mParent.getDocument().save(Document.saveMaskStyle);
    }

    public void title() {
        mParent.titleTextFormat();
    }
}
