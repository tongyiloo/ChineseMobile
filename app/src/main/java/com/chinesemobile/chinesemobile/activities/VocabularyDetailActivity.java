package com.chinesemobile.chinesemobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chinesemobile.chinesemobile.MyApplication;
import com.chinesemobile.chinesemobile.R;
import com.chinesemobile.chinesemobile.databinding.ActivityVocabularyDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class VocabularyDetailActivity extends AppCompatActivity {

    //view binding
    private ActivityVocabularyDetailBinding binding;

    //vocab if, get from intent
    String vocabularyId;

    TextToSpeech txtToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabularyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent e.g vocabularyId
        Intent intent = getIntent();
        vocabularyId = intent.getStringExtra("vocabularyId");

        loadVocabularyDetail();

        //handle backBtn
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

    private void loadVocabularyDetail() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.child(vocabularyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String english = ""+snapshot.child("english").getValue();
                        String chinese = ""+snapshot.child("chinese").getValue();
                        String pinyin = ""+snapshot.child("pinyin").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String url = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        MyApplication.loadCategory(
                                ""+categoryId,
                                binding.categoryTv
                        );

                        Glide.with(VocabularyDetailActivity.this)
                                .load(url)
                                .centerCrop()
                                .placeholder(R.drawable.ic_vocabimg_gray)
                                .into(binding.vocabImgView);

                        //set data
                        binding.enTitleTv.setText(english);
                        binding.cnTitleTv.setText(chinese);
                        binding.pinyinTv.setText(pinyin);

                        binding.speakBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                txtToSpeech.speak(chinese, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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