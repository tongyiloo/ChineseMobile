package com.chinesemobile.chinesemobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinesemobile.chinesemobile.adapters.AdapterSavedVocab;
import com.chinesemobile.chinesemobile.databinding.ActivitySavedVocabularyBinding;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedVocabularyActivity extends AppCompatActivity {

    private ActivitySavedVocabularyBinding binding;

    private FirebaseAuth firebaseAuth;

    // arraylist to hold the books
    private ArrayList<ModelVocabulary> vocabularyArrayList1;
    //adapter saved in recyclerview
    private AdapterSavedVocab adapterSavedVocab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedVocabularyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        loadSavedVocabulary();

        loadUserInfo();
        
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadSavedVocabulary() {
        //init list
        vocabularyArrayList1 = new ArrayList<>();

        //load saved books from db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Bookmarks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // clear list before starting adding data
                        vocabularyArrayList1.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get vocab id
                            String vocabularyId = ""+ds.child("vocabularyId").getValue();

                            //set id to model
                            ModelVocabulary modelVocabulary = new ModelVocabulary();
                            modelVocabulary.setId(vocabularyId);

                            //add model to list
                            vocabularyArrayList1.add(modelVocabulary);
                        }

                        // set number of saved vocabularies
                        binding.numberBookmarkTv.setText(""+vocabularyArrayList1.size());
                        //setup adapter
                        adapterSavedVocab = new AdapterSavedVocab(SavedVocabularyActivity.this, vocabularyArrayList1);
                        // set adapter to recyclerview
                        binding.VocabRv.setAdapter(adapterSavedVocab);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String uid = "" + snapshot.child("uid").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser==null){
        }
        else {

        }
    }
}