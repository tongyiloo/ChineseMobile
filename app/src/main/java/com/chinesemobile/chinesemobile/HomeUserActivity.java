package com.chinesemobile.chinesemobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeUserActivity extends AppCompatActivity {

    private ActivityHomeUserBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();
        checkUser();

        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeUserActivity.this);
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //begin delete
                                firebaseAuth.signOut();
                                checkUser();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
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

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = ""+snapshot.child("name").getValue();
                        binding.nameTv.setText(name);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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