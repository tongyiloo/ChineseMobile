package com.chinesemobile.chinesemobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinesemobile.chinesemobile.MyApplication;
import com.chinesemobile.chinesemobile.VocabularyDetailActivity;
import com.chinesemobile.chinesemobile.databinding.RowVocabUserBinding;
import com.chinesemobile.chinesemobile.filters.FilterVocabularyAdmin;
import com.chinesemobile.chinesemobile.filters.FilterVocabularyUser;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;

import java.util.ArrayList;

public class AdapterVocabularyUser extends RecyclerView.Adapter<AdapterVocabularyUser.HolderVocabularyUser> implements Filterable {

    private Context context;
    public ArrayList<ModelVocabulary> vocabularyArrayList, filterList;
    private FilterVocabularyUser filter;

    private RowVocabUserBinding binding;

    private static final String TAG = "ADAPTER_VOCAB_USER_TAG";

    public AdapterVocabularyUser(Context context, ArrayList<ModelVocabulary> vocabularyArrayList) {
        this.context = context;
        this.vocabularyArrayList = vocabularyArrayList;
        this.filterList = vocabularyArrayList;
    }


    @Override
    public HolderVocabularyUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind the view
        binding = RowVocabUserBinding.inflate(LayoutInflater.from(context), parent,false);

        return new HolderVocabularyUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterVocabularyUser.HolderVocabularyUser holder, int position) {
        //Get data, set datta, handle click

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

    @Override
    public int getItemCount() {
        return vocabularyArrayList.size(); // return list size
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterVocabularyUser(filterList, this);
        }
        return filter;

    }

    class HolderVocabularyUser extends RecyclerView.ViewHolder {

        // UI views of row_vocab_admin.xml
        TextView cnTitleTv, enTitleTv, pinyinTv;

        public HolderVocabularyUser(@NonNull View itemView) {
            super(itemView);

            //init ui view
            cnTitleTv = binding.cnTitleTv;
            enTitleTv = binding.enTitleTv;
            pinyinTv = binding.pinyinTv;
        }
    }
}
