package com.haiyunshan.express.compose.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.haiyunshan.express.R;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.compose.state.FormatState;
import com.haiyunshan.express.fragment.BaseFragment;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;

/**
 *
 */
public class TextFormatFragment extends BaseFragment {

    FrameLayout mContainer;

    FormatState mParent;
    ParagraphSegment mSegment;

    public TextFormatFragment() {

    }

    public void setArguments(FormatState parent, ParagraphSegment segment) {
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

        this.showParagraph();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            return false;
        }

        fm.popBackStack();
        return true;
    }

    public FormatState getParent() {
        return this.mParent;
    }

    public Segment getSegment() {
        return this.mSegment;
    }

    public void reset() {
        FragmentManager fm = this.getChildFragmentManager();
        while (fm.getBackStackEntryCount() != 0) {
            fm.popBackStackImmediate(); // 必须使用Immediate版本，否则会进入死循环
        }

    }

    void showParagraph() {
        ParagraphFragment f = new ParagraphFragment();
        f.setArguments(this, mSegment);

        {
            FragmentManager fm = this.getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.add(mContainer.getId(), f, "paragraph");
            t.commit();
        }
    }

    void showStyle() {
        StyleFragment f = new StyleFragment();
        f.setArguments(this, mSegment);

        {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();

            t.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            t.replace(mContainer.getId(), f, "style");

            t.addToBackStack("style");

            t.commit();
        }

    }

    void showTypeface() {
        TypefaceFragment f = new TypefaceFragment();
        f.setArguments(this, mSegment);

        {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction t = fm.beginTransaction();

            t.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            t.replace(mContainer.getId(), f, "typeface");

            t.addToBackStack("typeface");

            t.commit();
        }
    }

    void back() {
        FragmentManager fm = getChildFragmentManager();
        fm.popBackStack();
    }

    void close() {
        NoteComposeFragment f = mParent.getFragment();
        f.getDocument().save(Document.saveMaskStyle);

        mParent.pop();

    }

}
