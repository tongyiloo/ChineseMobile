package com.chinesemobile.chinesemobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.chinesemobile.chinesemobile.adapters.AdapterVocabAdmin;
import com.chinesemobile.chinesemobile.databinding.ActivityVocabListAdminBinding;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VocabListAdminActivity extends AppCompatActivity {

    private ActivityVocabListAdminBinding binding;

    //arraylist to hold list of data of type modelVocabulary
    private ArrayList<ModelVocabulary> vocabularyArrayList;
    //adapter
    private AdapterVocabAdmin adapterVocabAdmin;

    private String categoryId, categoryTitle;

    private static final String TAG = "VOCAB_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        //set pdf category
        binding.subTitleTv.setText(categoryTitle);

        loadVocabList();
        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //search as and when user type each letter
                try {
                    adapterVocabAdmin.getFilter().filter(s);

                }
                catch (Exception e){
                    Log.d(TAG,"onTextChanged: "+e.getMessage());

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadVocabList() {
        //init list before adding data
        vocabularyArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        vocabularyArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelVocabulary model = ds.getValue(ModelVocabulary.class);
                            //add to list
                            vocabularyArrayList.add(model);

                            Log.d(TAG,"onDataChange: "+model.getId()+" "+model.getEnglish());
                        }

                        adapterVocabAdmin = new AdapterVocabAdmin(VocabListAdminActivity.this, vocabularyArrayList);
                        binding.VocabRv.setAdapter(adapterVocabAdmin);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}