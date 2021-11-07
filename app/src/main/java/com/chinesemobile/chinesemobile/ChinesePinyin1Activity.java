package com.chinesemobile.chinesemobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import com.chinesemobile.chinesemobile.activities.ChineseIntroductionActivity;
import com.chinesemobile.chinesemobile.databinding.ActivityChineseCharacterBinding;
import com.chinesemobile.chinesemobile.databinding.ActivityChinesePinyin1Binding;

import java.util.Locale;

public class ChinesePinyin1Activity extends AppCompatActivity {

    private ActivityChinesePinyin1Binding binding;

    TextToSpeech txtToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChinesePinyin1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    txtToSpeech.setLanguage(Locale.CHINESE);
                }
            }
        });

        binding.pinyin01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chinese = "妈";
                txtToSpeech.speak(chinese, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        binding.pinyin02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chinese = "麻";
                txtToSpeech.speak(chinese, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        binding.pinyin03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chinese = "马";
                txtToSpeech.speak(chinese, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        binding.pinyin04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chinese = "骂";
                txtToSpeech.speak(chinese, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        binding.toChCharTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChinesePinyin1Activity.this, HomeUserActivity.class));
            }
        });


    }

    @Override
    protected void onPause(){
        if (txtToSpeech!=null){
            txtToSpeech.stop();
            txtToSpeech.shutdown();
        }
        super.onPause();
    }
}