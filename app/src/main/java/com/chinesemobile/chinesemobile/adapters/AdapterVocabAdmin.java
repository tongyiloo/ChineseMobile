package com.chinesemobile.chinesemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinesemobile.chinesemobile.MyApplication;
import com.chinesemobile.chinesemobile.databinding.RowVocabAdminBinding;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;

import java.util.ArrayList;

public class AdapterVocabAdmin extends RecyclerView.Adapter<AdapterVocabAdmin.HolderVocabAdmin> {

    //context
    private Context context;
    //arraylist to hold list of data of type ModelVocabulary
    private ArrayList<ModelVocabulary> vocabularyArrayList;
    //view binding row_vocab_amin.xml
    private RowVocabAdminBinding binding;

    //constructor
    public AdapterVocabAdmin(Context context, ArrayList<ModelVocabulary> vocabularyArrayList) {
        this.context = context;
        this.vocabularyArrayList = vocabularyArrayList;
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
        loadImgSize(model, holder);

    }

    private void loadImgSize(ModelVocabulary model, HolderVocabAdmin holder) {
        
    }

    private void loadImgFromUrl(ModelVocabulary model, HolderVocabAdmin holder) {
    }

    private void loadCategory(ModelVocabulary model, HolderVocabAdmin holder) {

    }

    @Override
    public int getItemCount() {
        return vocabularyArrayList.size();// return number of record  | lsit size
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
