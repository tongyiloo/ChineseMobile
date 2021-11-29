package com.chinesemobile.chinesemobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinesemobile.chinesemobile.MyApplication;
import com.chinesemobile.chinesemobile.R;
import com.chinesemobile.chinesemobile.adapters.AdapterSavedVocab;
import com.chinesemobile.chinesemobile.databinding.ActivityVocabularyDetailsBinding;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class VocabularyDetailsActivity extends AppCompatActivity {

    private ActivityVocabularyDetailsBinding binding;

    //vocab if, get from intent
    String vocabularyId;

    boolean isInMyBookmark = false;

    private FirebaseAuth firebaseAuth;


    TextToSpeech txtToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabularyDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent e.g vocabularyId
        Intent intent = getIntent();
        vocabularyId = intent.getStringExtra("vocabularyId");

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            checkIsBookmark();
        }

        loadVocabularyDetails();


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
                if (status != TextToSpeech.ERROR) {
                    txtToSpeech.setLanguage(Locale.CHINESE);
                }
            }
        });

        binding.bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(VocabularyDetailsActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isInMyBookmark){
                        MyApplication.removeFromBookmark(VocabularyDetailsActivity.this, vocabularyId);
                    }
                    else{
                        MyApplication.addToBookmark(VocabularyDetailsActivity.this, vocabularyId);
                    }
                }
            }
        });

    }



    private void loadVocabularyDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.child(vocabularyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String english = "" + snapshot.child("english").getValue();
                        String chinese = "" + snapshot.child("chinese").getValue();
                        String pinyin = "" + snapshot.child("pinyin").getValue();
                        String categoryId = "" + snapshot.child("categoryId").getValue();
                        String url = "" + snapshot.child("url").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();

                        MyApplication.loadCategory(
                                "" + categoryId,
                                binding.categoryTv
                        );

                        Glide.with(VocabularyDetailsActivity.this)
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

    private void checkIsBookmark(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Bookmarks").child(vocabularyId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyBookmark = snapshot.exists();
                        if (isInMyBookmark){
                            //exists in bookmark
                            binding.bookmarkBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_bookmark_orange, 0, 0);
                            binding.bookmarkBtn.setText("Remove");
                        }
                        else {
                            // not exists
                            binding.bookmarkBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_bookmark_border_orange, 0, 0);
                            binding.bookmarkBtn.setText("Save");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onPause() {
        if (txtToSpeech != null) {
            txtToSpeech.stop();
            txtToSpeech.shutdown();
        }
        super.onPause();
    }


}
