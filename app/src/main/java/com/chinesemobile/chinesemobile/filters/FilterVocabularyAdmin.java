package com.chinesemobile.chinesemobile.filters;

import android.widget.Filter;

import com.chinesemobile.chinesemobile.adapters.AdapterCategory;
import com.chinesemobile.chinesemobile.adapters.AdapterVocabAdmin;
import com.chinesemobile.chinesemobile.models.ModelCategory;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;

import java.util.ArrayList;

public class FilterVocabularyAdmin extends Filter {

    //arraylist in which we want to search
    ArrayList<ModelVocabulary> filterList;
    //adapter in which filter need to be implemented
    AdapterVocabAdmin adapterVocabAdmin;

    //constructor
    public FilterVocabularyAdmin(ArrayList<ModelVocabulary> filterList, AdapterVocabAdmin adapterVocabAdmin) {
        this.filterList = filterList;
        this.adapterVocabAdmin = adapterVocabAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null and empty
        if (constraint!= null && constraint.length() > 0){
            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelVocabulary> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                //validddate
                if (filterList.get(i).getEnglish().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter changes
        adapterVocabAdmin.vocabularyArrayList = (ArrayList<ModelVocabulary>)results.values;

        //notify changes
        adapterVocabAdmin.notifyDataSetChanged();
    }
}
