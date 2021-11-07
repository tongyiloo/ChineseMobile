package com.chinesemobile.chinesemobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinesemobile.chinesemobile.activities.ChinesePinyinActivity;
import com.chinesemobile.chinesemobile.databinding.ActivityChineseCharacterBinding;

public class ChineseCharacterActivity extends AppCompatActivity {

    private ActivityChineseCharacterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChineseCharacterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.toChinesePinyinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChineseCharacterActivity.this, ChinesePinyinActivity.class));
            }
        });


    }
}