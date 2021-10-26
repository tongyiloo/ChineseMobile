package com.chinesemobile.chinesemobile.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinesemobile.chinesemobile.MyApplication;
import com.chinesemobile.chinesemobile.VocabEditActivity;
import com.chinesemobile.chinesemobile.databinding.RowVocabAdminBinding;
import com.chinesemobile.chinesemobile.filters.FilterVocabularyAdmin;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.chinesemobile.chinesemobile.Constants.MAX_BYTES_IMG;

public class AdapterVocabAdmin extends RecyclerView.Adapter<AdapterVocabAdmin.HolderVocabAdmin> implements Filterable {

    //context
    private Context context;
    //arraylist to hold list of data of type ModelVocabulary
    public ArrayList<ModelVocabulary> vocabularyArrayList, filterList;
    //view binding row_vocab_amin.xml
    private RowVocabAdminBinding binding;

    private FilterVocabularyAdmin filter;

    private static final String TAG = "VOCAB_ADAPTER_TAG"; //6. 44:55

    //progress dialog 7. 4:13


    //constructor
    public AdapterVocabAdmin(Context context, ArrayList<ModelVocabulary> vocabularyArrayList) {
        this.context = context;
        this.vocabularyArrayList = vocabularyArrayList;
        this.filterList = vocabularyArrayList;
    }

    @NonNull
    @Override
    public HolderVocabAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind layout using view binding
        binding = RowVocabAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderVocabAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterVocabAdmin.HolderVocabAdmin holder, int position) {
        //get data, set data, handle clicks

        //get data
        ModelVocabulary model = vocabularyArrayList.get(position);
        String enTitle = model.getEnglish();
        String cnTitle = model.getChinese();
        String pinyin = model.getPinyin();
        long timestamp = model.getTimestamp();

        //need to convert timestamp to dd/mm/yyyy format
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        //setData
        holder.enTitleTv.setText(enTitle);
        holder.cnTitleTv.setText(cnTitle);
        holder.pinyinTv.setText(pinyin);

        //load further details like category, img from url
        loadCategory(model, holder);
        loadImgFromUrl(model, holder);
        //loadImgSize(model, holder);

        //handle click, show dialog with options 1) Edit, 2) Delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionDialog(model, holder);
            }
        });

    }

    private void moreOptionDialog(ModelVocabulary model, HolderVocabAdmin holder) {

        String vocabularyId = model.getId();
        String vocabularyUrl = model.getUrl(); //7. 22:22

        //options to show in dialog
        String[] options = {"Edit", "Delete"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog option click
                        if (which==0){
                            //Edit Clicked, open new activity to edit book info
                            Intent intent = new Intent(context, VocabEditActivity.class);
                            intent.putExtra("vocabularyId", vocabularyId);
                            context.startActivity(intent);

                        }
                        else if (which==1)
                        {
                            //Delete clicked
                            deleteVocabulary(model, holder);
                        }

                    }
                })
                .show();

    }


    private void deleteVocabulary(ModelVocabulary model, HolderVocabAdmin holder) {
        String vocabularyId = model.getId();
        String vocabularyUrl = model.getUrl();
        String vocabularyEnTitle = model.getEnglish();

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

    //6. 41:24
//    private void loadImgSize(ModelVocabulary model, HolderVocabAdmin holder) {
//        // using url we can get file an dits metdata from firebase storage
//        String imgUrl = model.getUrl();
//
//        StorageReference ref = FirebaseStorage.getInstance().getReference(imgUrl);
//        ref.getMetadata()
//                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//                    @Override
//                    public void onSuccess(StorageMetadata storageMetadata) {
//                        // get size in bytes
//                        double bytes = storageMetadata.getSizeBytes();
//
//                        //convert bytes to KB, MB
//                        double kb = bytes/1024;
//                        double mb = kb/1024;
//
//                        // 45:26
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // failed getting metadata
//                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void loadImgFromUrl(ModelVocabulary model, HolderVocabAdmin holder) {
        // using url we can get file and its metadata from firebase storage
        String imgUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
        ref.getBytes(MAX_BYTES_IMG)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG,"onSuccess: "+model.getEnglish()+ " successfully got the file");
                        //set to img view 46:34


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: failed getting img from url due to " +e.getMessage());
                    }
                });
    }

    private void loadCategory(ModelVocabulary model, HolderVocabAdmin holder) {
        //get category using categoryId

        String categoryId = model.getCategoryId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = ""+snapshot.child("category").getValue();
                        //set category text view
                        //49:43


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return vocabularyArrayList.size();// return number of record  | lsit size
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterVocabularyAdmin(filterList, this);
        }
        return filter;
    }

    //View holder class for row_vocab_admin
    class HolderVocabAdmin extends RecyclerView.ViewHolder{

        // UI views of row_vocab_admin.xml
        TextView cnTitleTv, enTitleTv, pinyinTv;
        ImageButton moreBtn;


        public HolderVocabAdmin(@NonNull View itemView) {
            super(itemView);

            //init ui view
            cnTitleTv = binding.cnTitleTv;
            enTitleTv = binding.enTitleTv;
            pinyinTv = binding.pinyinTv;
            moreBtn = binding.moreBtn;

        }
    }
}
