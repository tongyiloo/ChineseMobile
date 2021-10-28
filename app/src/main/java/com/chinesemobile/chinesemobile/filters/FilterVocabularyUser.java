package com.chinesemobile.chinesemobile.filters;

import android.widget.Filter;

import com.chinesemobile.chinesemobile.adapters.AdapterVocabularyUser;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;

import java.util.ArrayList;

public class FilterVocabularyUser extends Filter {


    //arraylist in which we want to search
    ArrayList<ModelVocabulary> filterList;
    //adapter in which filter need to be implemented
    AdapterVocabularyUser adapterVocabularyUser;

    //constructor


    public FilterVocabularyUser(ArrayList<ModelVocabulary> filterList, AdapterVocabularyUser adapterVocabularyUser) {
        this.filterList = filterList;
        this.adapterVocabularyUser = adapterVocabularyUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null and empty
        if (constraint!= null || constraint.length() > 0){
            //not null nor empty
            //change to uppercase or lower case sensitive
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelVocabulary> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                //validate
                if (filterList.get(i).getEnglish().toUpperCase().contains(constraint)){
                    //search matches, add to list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            //empty or null, make original list/result
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter changes
        adapterVocabularyUser.vocabularyArrayList = (ArrayList<ModelVocabulary>)results.values;

        //notify changes
        adapterVocabularyUser.notifyDataSetChanged();

    }
}
