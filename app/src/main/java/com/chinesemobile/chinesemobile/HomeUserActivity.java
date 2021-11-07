package com.chinesemobile.chinesemobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.chinesemobile.chinesemobile.activities.ChineseIntroductionActivity;
import com.chinesemobile.chinesemobile.activities.ChinesePinyinActivity;
import com.chinesemobile.chinesemobile.activities.DashboardAdminActivity;
import com.chinesemobile.chinesemobile.activities.LoginActivity;
import com.chinesemobile.chinesemobile.activities.MainActivity;
import com.chinesemobile.chinesemobile.activities.VocabEditActivity;
import com.chinesemobile.chinesemobile.activities.VocabularyListUserActivity;
import com.chinesemobile.chinesemobile.adapters.AdapterVocabAdmin;
import com.chinesemobile.chinesemobile.databinding.ActivityDashboardUserBinding;
import com.chinesemobile.chinesemobile.databinding.ActivityHomeUserBinding;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeUserActivity extends AppCompatActivity {

    private ActivityHomeUserBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.lvl1VocabHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUserActivity.this, VocabularyListUserActivity.class));

            }
        });

        binding.introHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUserActivity.this, ChineseIntroductionActivity.class));

            }
        });

        binding.charHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUserActivity.this, ChineseCharacterActivity.class));

            }
        });

        binding.pinyinHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeUserActivity.this, ChinesePinyin1Activity.class));
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser==null){
            //not logged in , go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else {
            //logged in, get user info
            String email = firebaseUser.getEmail();

            //set in textview of toolbar
            binding.nameTv.setText(email);
        }
    }


}