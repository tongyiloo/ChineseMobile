package com.chinesemobile.chinesemobile.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chinesemobile.chinesemobile.databinding.ActivityVocabAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class VocabAddActivity extends AppCompatActivity {

    //setup view binding
    private ActivityVocabAddBinding binding;

    private FirebaseAuth firebaseAuth;

    //arraylist to hold vocab categories
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    //uri of the picked img
    private Uri imgUri = null;


    private static final int IMG_PICK_CODE = 1000; // 20:26

    //TAG for debugging
    private static final String TAG = "ADD_IMG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.GONE);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadVocabCategories();

        //handle click , go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click, attach image
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imgPickIntent();
                mGetContent.launch("image/*");
            }
        });

        //handle click, pick categories
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPick();
            }
        });

        //handle click, upload img
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                validateData();

            }
        });
    }

    private String titleEng="", titleCn="", pinyin="";

    private void validateData() {
        //Step 1: validate data
        Log.d(TAG,"validateData: validating data...");

        //get data
        titleEng = binding.titleEnglishEt.getText().toString().trim();
        titleCn = binding.chineseEt.getText().toString().trim();
        pinyin = binding.pinyinEt.getText().toString().trim();
        //category = binding.categoryTv.getText().toString().trim();

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
        else if (TextUtils.isEmpty(selectedCategoryTitle)){
            Toast.makeText(this,"Pick the category ...", Toast.LENGTH_SHORT).show();
        }
        else if (imgUri==null){
            Toast.makeText(this,"Pick the image ...", Toast.LENGTH_SHORT).show();
        }
        else {
            //all data is valid , can upload now
            uploadVocabToStorage();
        }
    }

    private void uploadVocabToStorage() {
        //Step 2: Upload Vocab to firebase storage
        binding.progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG,"uploadVocabToStorage: uploading to storage...");

        Toast.makeText(this,"Uploading vocabulary details...", Toast.LENGTH_SHORT).show();

        //timestamp
        long timestamp = System.currentTimeMillis();

        //path in firebase storage
        String filePathAndName = "Vocabulary/" + timestamp;
        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSuccess: vocab uploaded to Storage...");
                        Log.d(TAG,"onSuccess: getting url");

                        //get pdf url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedVocabUrl = ""+uriTask.getResult();

                        //upload to firebase db
                        uploadVocabInfoToDB(uploadedVocabUrl, timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: vocab upload failed due to"+e.getMessage());
                        Toast.makeText(VocabAddActivity.this, "Vocab upload failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadVocabInfoToDB(String uploadedVocabUrl, long timestamp) {
        //Step 3: Upload vocab info to firebase db
        Log.d(TAG,"uploadVocabToStorage: uploading vocab info into firebase db...");

        Toast.makeText(VocabAddActivity.this, "Uploading vocabulary info...", Toast.LENGTH_SHORT).show();

        String uid = firebaseAuth.getUid();

        //setup data to upload, also add view count, download count while adding vocab
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("english", ""+titleEng);
        hashMap.put("chinese", ""+titleCn);
        hashMap.put("pinyin", ""+pinyin);
        hashMap.put("categoryId", selectedCategoryId);
        hashMap.put("url", ""+uploadedVocabUrl);
        hashMap.put("timestamp", timestamp);
        hashMap.put("viewCounts", 0); //08 2:19

        //db reference: DB > Vocabulary (line 146) 43:29 Books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "OnSuccess: successfully uploaded");
                        Toast.makeText(VocabAddActivity.this,"Successfully uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onFailure: Failed to upload to db due to "+e.getMessage());
                        Toast.makeText(VocabAddActivity.this, "Failed to upload to db due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadVocabCategories() {
        Log.d(TAG, "loadVocabCategories: Loading img categories");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        //db reference to load categories.. db > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();//clear before adding data
                for (DataSnapshot ds: snapshot.getChildren()){

                    //get id and title of category
                    String categoryId = ""+ds.child("id").getValue();
                    String categoryTitle = ""+ds.child("category").getValue();

                    //add to respective arraylists
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //selected category id and category id
    private String selectedCategoryId, selectedCategoryTitle;

    private void categoryPick() {
        //first we need to get categories from firebase
        Log.d(TAG, "categoryPickDialog: showing category pick dialog");

        //get array of categories from arraylist
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item click
                        //get clicked item from list
                        selectedCategoryTitle = categoryTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);

                        //set to category text view
                        binding.categoryTv.setText(selectedCategoryTitle);

                        Log.d(TAG, "onClick: Selected Categories: "+selectedCategoryId+" "+selectedCategoryTitle);
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == IMG_PICK_CODE){
                Log.d(TAG,"onActivityResult: Image Picked");

                imgUri = data.getData();

                Log.d(TAG, "onActivityResult: URI "+imgUri);
            }
        }else {
            Log.d(TAG, "onActivityResult: cancelled picking img");
            Toast.makeText(this, "cancelled picking img", Toast.LENGTH_SHORT).show();
        }
    }

    //start an activity for result and get the result from the activity select image, the result will set to imageUri
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            result -> {
                //this result is the result of uri
                if (result!=null){
                    //imgview.setImageURI(result);
                    //result will be set in uri
                    imgUri = result;
                }
            });

}