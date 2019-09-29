package com.haiyunshan.express.test;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haiyunshan.express.R;
import com.haiyunshan.express.app.LogUtils;

import java.util.HashMap;
import java.util.Locale;

public class TestTTSActivity extends AppCompatActivity {

    private EditText input;
    private Button speech,record;

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tts);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE){
                        Toast.makeText(TestTTSActivity.this, "TTS暂时不支持这种语音的朗读！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textToSpeech.setPitch(0.2f);

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                LogUtils.w("TTS", "onDone = " + utteranceId);
                textToSpeech.speak(input.getText(), TextToSpeech.QUEUE_ADD, null, "abc");
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        input = (EditText) findViewById(R.id.input_text);
        speech = (Button) findViewById(R.id.speech);
        record = (Button) findViewById(R.id.record);

        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                textToSpeech.speak(input.getText(), TextToSpeech.QUEUE_ADD, null, "abc");
//                textToSpeech.speak(input.getText().toString(), TextToSpeech.QUEUE_ADD, null);
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String action = "com.android.settings.tts.TextToSpeechSettings";
                action = "com.android.settings.TTS_SETTINGS";

                startActivity(new Intent(action));


//                String inputText = input.getText().toString();
//                HashMap<String, String> myHashRender = new HashMap<>();
//                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, inputText);
//
//                textToSpeech.synthesizeToFile(inputText, myHashRender,
//                        "/mnt/sdcard/my_recorder_audios/sound.wav");
//                Toast.makeText(TestTTSActivity.this, "声音记录成功。", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null)
            textToSpeech.shutdown();
        super.onDestroy();
    }
}

