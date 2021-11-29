package com.chinesemobile.chinesemobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.chinesemobile.chinesemobile.adapters.AdapterVocabularyUser;
import com.chinesemobile.chinesemobile.databinding.FragmentVocabularyUserBinding;
import com.chinesemobile.chinesemobile.models.ModelVocabulary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VocabularyUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VocabularyUserFragment extends Fragment {

    //passed while creating instance of this fragment (vocabularyListUserActivity)85-90, 107-110
    private String categoryId;
    private String category;
    private String uid;

    private ArrayList<ModelVocabulary> vocabularyArrayList;
    private AdapterVocabularyUser adapterVocabularyUser;

    //view binding
    private FragmentVocabularyUserBinding binding;

    private static final String TAG = "VOCAB_USER_TAG";

    public VocabularyUserFragment() {
        // Required empty public constructor
    }

    public static VocabularyUserFragment newInstance(String categoryId, String category, String uid) {
        VocabularyUserFragment fragment = new VocabularyUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        args.putString("category", category);
        args.putString("uid", uid);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVocabularyUserBinding.inflate(LayoutInflater.from(getContext()), container, false);

        if (category.equals("All")){
            //load all vocabulary
            loadAllVocabulary();
        }
        else {
            //load selected category books
            loadCategorizedVocabulary();
        }

        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called as and when user type any letter
                try {
                    adapterVocabularyUser.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }

    private void loadAllVocabulary() {
        //init list
        vocabularyArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vocabularyArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVocabulary model = ds.getValue(ModelVocabulary.class);
                    //add to list
                    vocabularyArrayList.add(model);
                }
                //setup adapter
                adapterVocabularyUser = new AdapterVocabularyUser(getContext(), vocabularyArrayList);
                //set adapter to recyclerview
                binding.VocabRv.setAdapter(adapterVocabularyUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCategorizedVocabulary() {
        //init list
        vocabularyArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vocabulary");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    vocabularyArrayList.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        //get data
                        ModelVocabulary model = ds.getValue(ModelVocabulary.class);
                        //add to list
                        vocabularyArrayList.add(model);
                    }
                    //setup adapter
                    adapterVocabularyUser = new AdapterVocabularyUser(getContext(), vocabularyArrayList);
                    //set adapter to recyclerview
                    binding.VocabRv.setAdapter(adapterVocabularyUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }


}