package com.chinesemobile.chinesemobile.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinesemobile.chinesemobile.MyApplication;
import com.chinesemobile.chinesemobile.activities.VocabEditActivity;
import com.chinesemobile.chinesemobile.VocabularyDetailActivity;
import com.chinesemobile.chinesemobile.databinding.RowVocabAdminBinding;
import com.chinesemobile.chinesemobile.filters.FilterVocabularyAdmin;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;

import java.util.ArrayList;

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
        String vocabularyId = model.getId();
        String categoryId = model.getCategoryId();
        String enTitle = model.getEnglish();
        String cnTitle = model.getChinese();
        String pinyin = model.getPinyin();
        String vocabularyUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        //need to convert timestamp to dd/mm/yyyy format
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        //setData
        holder.enTitleTv.setText(enTitle);
        holder.cnTitleTv.setText(cnTitle);
        holder.pinyinTv.setText(pinyin);

        //load further details like category, img from url

        //MyApplication.loadCategories 08. 13:32
//        MyApplication.loadCategory(
//                ""+categoryId,
//                holder.categoryTv);

        //loadImgFromUrl(model, holder); //MyApplication.loadImgFromUrl 08. 12:05
        //loadImgSize(model, holder); MyApplication.loadPdfSize()08/ *:07

        //handle click, show dialog with options 1) Edit, 2) Delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionDialog(model, holder);
            }
        });

        //handle click, open vocabulary details page, pass vocabulary id to get the details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VocabularyDetailActivity.class);
                intent.putExtra("vocabularyId", vocabularyId);
                context.startActivity(intent);
            }
        });

    }


    private void moreOptionDialog(ModelVocabulary model, HolderVocabAdmin holder) {

        String vocabularyId = model.getId();
        String vocabularyUrl = model.getUrl(); //7. 22:22
        String enTitle = model.getEnglish();

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
                            MyApplication.deleteVocabulary(
                                    context,
                                    ""+vocabularyId,
                                    ""+vocabularyUrl,
                                    ""+enTitle
                            );
                            //deleteVocabulary(model, holder);
                        }
                    }
                })
                .show();

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

//    private void loadImgFromUrl(ModelVocabulary model, HolderVocabAdmin holder) {
//        // using url we can get file and its metadata from firebase storage
//        String imgUrl = model.getUrl();
//        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
//        ref.getBytes(MAX_BYTES_IMG)
//                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Log.d(TAG,"onSuccess: "+model.getEnglish()+ " successfully got the file");
//                        //set to img view 46:34
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG,"onFailure: failed getting img from url due to " +e.getMessage());
//                    }
//                });
//    }
//
//    private void loadCategory(ModelVocabulary model, HolderVocabAdmin holder) {
//        //get category using categoryId
//
//        String categoryId = model.getCategoryId();
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
//        ref.child(categoryId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        //get category
//                        String category = ""+snapshot.child("category").getValue();
//                        //set category text view
//                        //49:43
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//    }

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
