package com.haiyunshan.express.compose.utils;

import android.app.Activity;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.tts.TTS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanshibro on 2018/3/19.
 */

public class DocumentVoice {

    TTS mTTS;

    ParagraphSegment mCurrent;

    ArrayList<ParagraphSegment> mList;

    Activity mContext;
    NoteComposeFragment mParent;

    public DocumentVoice(NoteComposeFragment parent) {
        this.mContext = parent.getActivity();
        this.mParent = parent;

        this.mTTS = TTS.instance();
        mTTS.prepare();
        mTTS.getEngine().setOnUtteranceProgressListener(mListener);

        mList = this.init();
    }

    public ParagraphSegment start() {

        if (mList.isEmpty()) {
            return null;
        }

        ParagraphSegment seg = mList.get(0);
        if (seg == null) {
            return seg;
        }

        String id = mTTS.speak(seg.getText(), seg.getId());
        if (id == null) {

        }

        this.mCurrent = seg;
        return seg;
    }

    public void shutdown() {
        mTTS.shutdown();
    }

    ParagraphSegment next() {
        int index = mList.indexOf(mCurrent);
        if (index < 0 || index + 1 == mList.size()) {
            return null;
        }

        ++index;
        ParagraphSegment seg = mList.get(index);
        String id = mTTS.speak(seg.getText(), seg.getId());
        if (id == null) {

        }

        this.mCurrent = seg;
        return seg;
    }

    ArrayList<ParagraphSegment> init() {
        ArrayList<ParagraphSegment> outList = new ArrayList<>();

        ParagraphSegment seg = mParent.getDocument().getTitle();
        if (!seg.isEmpty()) {
            outList.add(seg);
        }

        seg = mParent.getDocument().getSubtitle();
        if (!seg.isEmpty()) {
            outList.add(seg);
        }

        List<Segment> list = mParent.getDocument().getBody();
        for (Segment s : list) {
            if (s instanceof ParagraphSegment) {
                seg = (ParagraphSegment)s;
                if (!seg.isEmpty()) {
                    outList.add(seg);
                }
            }
        }

        return outList;
    }

    UtteranceProgressListener mListener = new UtteranceProgressListener() {

        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            ParagraphSegment seg = next();
            if (seg == null) {

            }
        }

        @Override
        public void onError(String utteranceId) {

        }
    };
}
