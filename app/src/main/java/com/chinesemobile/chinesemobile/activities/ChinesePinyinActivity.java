package com.chinesemobile.chinesemobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import com.chinesemobile.chinesemobile.ChineseCharacterActivity;
import com.chinesemobile.chinesemobile.databinding.ActivityChineseCharacterBinding;
import com.chinesemobile.chinesemobile.databinding.ActivityChinesePinyinBinding;

import java.util.Locale;

public class ChinesePinyinActivity extends AppCompatActivity {

    private ActivityChinesePinyinBinding binding;

    TextToSpeech txtToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChinesePinyinBinding.inflate(getLayoutInflater());
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