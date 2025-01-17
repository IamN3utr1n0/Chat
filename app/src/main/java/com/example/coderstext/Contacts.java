package com.example.coderstext;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contacts extends Fragment {
private View ContactView;
private RecyclerView myContactslist;
private DatabaseReference ContactsRef,usersref;
private FirebaseAuth mauth;
private String curentuserid;

    public Contacts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactView = inflater.inflate(R.layout.fragment_contacts,container,false);
       myContactslist = (RecyclerView)ContactView.findViewById(R.id.contacts_list);
       myContactslist.setLayoutManager(new LinearLayoutManager(getContext()));
       mauth = FirebaseAuth.getInstance();
       curentuserid = mauth.getCurrentUser().getUid();
       ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(curentuserid);
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");
       return ContactView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contactsfriends>()
                .setQuery(ContactsRef,Contactsfriends.class)
                .build();


        FirebaseRecyclerAdapter<Contactsfriends,ContactsViewhOlder>adapter =
                new FirebaseRecyclerAdapter<Contactsfriends, ContactsViewhOlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewhOlder holder, int position, @NonNull final Contactsfriends model) {

                String userIDs = getRef(position).getKey();
                usersref.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("image"))
                        {
                            String userimage =dataSnapshot.child("image").getValue().toString();
                            String profilename =dataSnapshot.child("name").getValue().toString();
                            String profilestatus =dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);
                            Picasso.get().load(userimage).placeholder(R.drawable.userfreinds).into(holder.profileimage);

                        }
                        else {
                            String profilename =dataSnapshot.child("name").getValue().toString();
                            String profilestatus =dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewhOlder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                ContactsViewhOlder viewHolder = new ContactsViewhOlder(view);
                return viewHolder;
            }
        };
        myContactslist.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ContactsViewhOlder extends RecyclerView.ViewHolder
    {
TextView username,userstatus;
CircleImageView profileimage;
        public ContactsViewhOlder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.userstatusx);
            profileimage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
