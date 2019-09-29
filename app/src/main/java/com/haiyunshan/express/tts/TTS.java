package com.haiyunshan.express.tts;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.haiyunshan.express.App;
import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.test.TestTTSActivity;

import java.util.Locale;

/**
 * Created by sanshibro on 2018/3/19.
 */

public class TTS implements TextToSpeech.OnInitListener {

    static final String TAG = "TTS";

    TextToSpeech mSpeech;
    Integer mStatus;

    private static TTS sInstance;

    public static TTS instance() {
        if (sInstance == null) {
            sInstance = new TTS();
        }

        return sInstance;
    }

    private TTS() {

    }

    public Integer getStatus() {
        return this.mStatus;
    }

    public TextToSpeech getEngine() {
        return mSpeech;
    }

    public void prepare() {
        if (mSpeech != null) {
            this.shutdown();
        }

        Context context = App.instance();
        this.mSpeech = new TextToSpeech(context, this);
    }

    public String speak(final CharSequence text,
                        final String utteranceId) {
        if (mSpeech == null || mStatus != TextToSpeech.SUCCESS) {
            return null;
        }

        LogUtils.w(TAG, "start speak");
        int result = mSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
        if (result != TextToSpeech.SUCCESS) {
            return null;
        }

        LogUtils.w(TAG, "start speak result = " + result);
        return utteranceId;
    }

    public void shutdown() {
        if (mSpeech != null) {
            mSpeech.shutdown();
            mSpeech = null;

            this.mStatus = null;
        }
    }

    @Override
    public void onInit(int status) {
        this.mStatus = status;

        if (status == TextToSpeech.SUCCESS) {
            int result = mSpeech.setLanguage(Locale.CHINA);
            if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                    && result != TextToSpeech.LANG_AVAILABLE){
                this.mStatus = TextToSpeech.ERROR;
            }
        }

        LogUtils.w(TAG, "init = " + mStatus);
    }
}
