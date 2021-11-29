package com.chinesemobile.chinesemobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinesemobile.chinesemobile.MyApplication;
import com.chinesemobile.chinesemobile.activities.VocabularyDetailsActivity;
import com.chinesemobile.chinesemobile.databinding.RowVocabSavedBinding;
import com.chinesemobile.chinesemobile.databinding.RowVocabUserBinding;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterSavedVocab extends RecyclerView.Adapter<AdapterSavedVocab.HolderSavedVocab> {

    private Context context;

    private ArrayList<ModelVocabulary> vocabularyArrayList1;
    //view binding for row_vocab_user
    private RowVocabSavedBinding binding;

    //constructor
    public AdapterSavedVocab(Context context, ArrayList<ModelVocabulary> vocabularyArrayList) {
        this.context = context;
        this.vocabularyArrayList1 = vocabularyArrayList;
    }

    @NonNull
    @Override
    public HolderSavedVocab onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind/inflate row_vocab
        binding = RowVocabSavedBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderSavedVocab(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSavedVocab holder, int position) {
        //get data
        ModelVocabulary model = vocabularyArrayList1.get(position);

        loadVocabDetails(model, holder);

        //handle click, open vocabulary details page, pass vocabulary id to get the details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VocabularyDetailsActivity.class);
                intent.putExtra("vocabularyId", model.getId()); // pass vocab id
                context.startActivity(intent);
            }
        });

        holder.removeSavedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.removeFromBookmark(context, model.getId()); // pass vocab id
            }
        });

    }

    private void loadVocabDetails(ModelVocabulary model, HolderSavedVocab holder) {
        String vocabularyId = model.getId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.child(vocabularyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String EnTitle = ""+snapshot.child("english").getValue();
                        String CnTitle = ""+snapshot.child("chinese").getValue();
                        String pinyinTitle = ""+snapshot.child("pinyin").getValue();
                        String vocabUrl = ""+snapshot.child("url").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();

                        model.setSave(true);
                        model.setEnglish(EnTitle);
                        model.setChinese(CnTitle);
                        model.setPinyin(pinyinTitle);
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(vocabUrl);


                        //set data to views
                        holder.cnTitleTv.setText(CnTitle);
                        holder.enTitleTv.setText(EnTitle);
                        holder.pinyinTv.setText(pinyinTitle);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return vocabularyArrayList1.size();
    }

    class HolderSavedVocab extends RecyclerView.ViewHolder{

        // UI views of row_vocab
        TextView cnTitleTv, enTitleTv, pinyinTv;
        ImageButton removeSavedBtn;

        public HolderSavedVocab(@NonNull View itemView) {
            super(itemView);
            //init ui view
            cnTitleTv = binding.cnTitleTv;
            enTitleTv = binding.enTitleTv;
            pinyinTv = binding.pinyinTv;
            removeSavedBtn = binding.removeSavedBtn;
        }
    }
}
