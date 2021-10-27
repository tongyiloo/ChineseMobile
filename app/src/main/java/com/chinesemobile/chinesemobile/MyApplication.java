package com.chinesemobile.chinesemobile;

import android.app.Application;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chinesemobile.chinesemobile.adapters.AdapterVocabAdmin;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;

import static com.chinesemobile.chinesemobile.Constants.MAX_BYTES_IMG;

//application class runs before launcher activity
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //created a static method to convert timestamp to proper date format
    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        //format timestamp to dd/mm/yyy
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }

    public static void deleteVocabulary(Context context, String vocabularyId, String vocabularyUrl, String enTitle) {
        String TAG = "DELETE_BOK_TAG";
//        String vocabularyId = model.getId();
//        String vocabularyUrl = model.getUrl();
//        String vocabularyEnTitle = model.getEnglish();

        Log.d(TAG, "deleteVocabulary: Deleting...");

        Log.d(TAG, "deleteVocabulary: Deleting from storage...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(vocabularyUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from storage...");
                        Log.d(TAG, "onSuccess: Now deleting info from db...");

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vocabulary");
                        reference.child(vocabularyId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Deleted from db...");
                                        Toast.makeText(context, "Vocabulary deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failed to delete from db due to "+e.getMessage());
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to delete from storage due to "+e.getMessage());
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //loadPdfSize (08. 6:52)

    //loadImgFromUrl (08. 10:40) 08. 9:27
//    public static void loadImgFromUrl(String imgUrl, String enTitle, ImageView imageView){
//        String TAG = "IMG_LOAD";
//
//        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
//
//
//        ref.getBytes(MAX_BYTES_IMG)
//                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        imageView.fromBytes(bytes);
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: failed getting img from url due to "+e.getMessage());
//                    }
//                });
//    }

    //loadCategory(08. 12:50)
    public static void loadCategory(String categoryId, TextView categoryTv){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = ""+snapshot.child("category").getValue();

                        //set category text view
                        categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
