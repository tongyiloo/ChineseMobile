package com.chinesemobile.chinesemobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chinesemobile.chinesemobile.databinding.ActivityVocabEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class VocabEditActivity extends AppCompatActivity {

    //view binding
    private ActivityVocabEditBinding binding;

    //book id get from intent startedd from AdapterVocabAdmin
    private String vocabularyId;

    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    private static final String TAG = "BOOK_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vocabularyId = getIntent().getStringExtra("vocabularyId");

        binding.progressBar.setVisibility(View.GONE);

        loadCategories();
        loadVocabularyInfo();

        //handle click, pick category
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click begin upload
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();

            }
        });
    }

    private void loadVocabularyInfo() {
        Log.d(TAG, "loadVocabularyInfo: Loading book info");

        DatabaseReference refVocab = FirebaseDatabase.getInstance().getReference("Vocabulary");
        refVocab.child(vocabularyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get vocab info
                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String english = ""+snapshot.child("english").getValue();
                        String chinese = ""+snapshot.child("chinese").getValue();
                        String pinyin = ""+snapshot.child("pinyin").getValue();
                        //set to views
                        binding.titleEnglishEt.setText(english);
                        binding.chineseEt.setText(chinese);
                        binding.pinyinEt.setText(pinyin);

                        Log.d(TAG, "onDataChange: Loading Vocabulary Category Info");
                        DatabaseReference refVocabCategory = FirebaseDatabase.getInstance().getReference("Categories");
                        refVocabCategory.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get category
                                        String category = ""+snapshot.child("category").getValue();
                                        //set to category text view
                                        binding.categoryTv.setText(category);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String titleEng="", titleCn="", pinyin="";
    private void validateData(){
        //get data
        titleEng = binding.titleEnglishEt.getText().toString().trim();
        titleCn = binding.chineseEt.getText().toString().trim();
        pinyin = binding.pinyinEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(titleEng)){
            Toast.makeText(this,"Enter the vocabulary in English...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(titleCn)){
            Toast.makeText(this,"Enter the vocabulary in Chinese...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pinyin)){
            Toast.makeText(this,"Enter the pinyin...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedCategoryId)){
            Toast.makeText(this,"Pick the category ...", Toast.LENGTH_SHORT).show();
        }
        else {
            //all data is valid , can upload now
            updateVocabulary();
        }
    }

    private void updateVocabulary() {
        Log.d(TAG, "updateVocabulary: Starting updating vocabulary info to db");

        //show progress
        binding.progressBar.setVisibility(View.VISIBLE);

        //setup data to update to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("english", ""+titleEng);
        hashMap.put("chinese", ""+titleCn);
        hashMap.put("pinyin", ""+pinyin);
        hashMap.put("categoryId", ""+selectedCategoryId);

        //start updating
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.child(vocabularyId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Vocabulary updated...");
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(VocabEditActivity.this,"Vocabulary successfully updated",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onFailure: failed to update due to "+e.getMessage());
                        Toast.makeText(VocabEditActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private String selectedCategoryId="", selectedCategoryTitle="";

    private void categoryDialog(){
        //make string array from arraylist of string
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i<categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategoryId = categoryIdArrayList.get(which);
                        selectedCategoryTitle = categoryTitleArrayList.get(which);

                        //set to textview
                        binding.categoryTv.setText(selectedCategoryTitle);


                    }
                })
                .show();
    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories: Loading categories...");

        categoryIdArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArrayList.clear();
                categoryTitleArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    String category = ""+ds.child("category").getValue();
                    categoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);

                    Log.d(TAG, "onDataChange: ID: "+id);
                    Log.d(TAG, "onDataChange: Category"+category);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}