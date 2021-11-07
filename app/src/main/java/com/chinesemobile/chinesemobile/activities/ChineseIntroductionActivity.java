package com.chinesemobile.chinesemobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinesemobile.chinesemobile.ChineseCharacterActivity;
import com.chinesemobile.chinesemobile.HomeUserActivity;
import com.chinesemobile.chinesemobile.databinding.ActivityChineseCharacterBinding;
import com.chinesemobile.chinesemobile.databinding.ActivityChineseIntroductionBinding;
import com.chinesemobile.chinesemobile.databinding.ActivityDashboardUserBinding;

public class ChineseIntroductionActivity extends AppCompatActivity {

    private ActivityChineseIntroductionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChineseIntroductionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       binding.toChineseCharTv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(ChineseIntroductionActivity.this, ChineseCharacterActivity.class));
           }
       });


    }


}