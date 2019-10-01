package com.example.coderstext;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class Groups extends Fragment {
private View GroupFragmentview;
private ListView listview;
private ArrayAdapter<String>arrayAdapter;
private ArrayList<String> list_of_groups = new ArrayList<>();
private DatabaseReference Groupref;

    public Groups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GroupFragmentview = inflater.inflate(R.layout.fragment_groups, container, false);
        Groupref = FirebaseDatabase.getInstance().getReference().child("Groups");
        Initialize();
        RetrieveDisplay();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String currentgroupname = adapterView.getItemAtPosition(position).toString();
                Intent groupchatintent = new Intent(getContext(),GroupChat.class);
                groupchatintent.putExtra("groupname",currentgroupname);
                startActivity(groupchatintent);
            }
        });


        return  GroupFragmentview;
    }

    private void RetrieveDisplay() {
        Groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                   set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Initialize() {
        listview = (ListView) GroupFragmentview.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        listview.setAdapter(arrayAdapter);
    }

}
