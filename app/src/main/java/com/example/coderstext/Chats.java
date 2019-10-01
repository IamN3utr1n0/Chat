package com.example.coderstext;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class Chats extends Fragment {
private View PrivateChatsView;
private RecyclerView chatslist;
private DatabaseReference chatsref,userref;
private FirebaseAuth mauth;
private String curentuserid;

    public Chats() {


        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);
        mauth = FirebaseAuth.getInstance();
        curentuserid = mauth.getCurrentUser().getUid();

        chatsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(curentuserid);
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        chatslist = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        chatslist.setLayoutManager(new LinearLayoutManager(getContext()));
        return PrivateChatsView;

    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contactsfriends> options = new
                FirebaseRecyclerOptions.Builder<Contactsfriends>()
                .setQuery(chatsref,Contactsfriends.class)
                .build();


        FirebaseRecyclerAdapter<Contactsfriends,ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contactsfriends, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position, @NonNull Contactsfriends model) {
                        final String userIDs = getRef(position).getKey();
                        final String[] retimage = {"default_image"};

                        userref.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild("image"))
                                {
                                 retimage[0] =dataSnapshot.child("image").getValue().toString();
                                    Picasso.get().load(retimage[0]).placeholder(R.drawable.userfreinds).into(holder.profileimage);

                                }

                                    final String profilenamex =dataSnapshot.child("name").getValue().toString();
                                    String profilestatusxx =dataSnapshot.child("status").getValue().toString();

                                    holder.username.setText(profilenamex);
                                    holder.userstatus.setText("Last seen:"+"\n"+"Date"+" Time");
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent profileIntent = new Intent(getContext(),ChatActivity.class);
                                        profileIntent.putExtra("visit_user_id",userIDs);
                                        profileIntent.putExtra("visit_user_name",profilenamex);
                                        profileIntent.putExtra("visit_image", retimage[0]);

                                        startActivity(profileIntent);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });







                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                     View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                     return new ChatsViewHolder(view);
                    }
                };
        chatslist.setAdapter(adapter);
        adapter.startListening();

    }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileimage;
        TextView username, userstatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            profileimage = itemView.findViewById(R.id.users_profile_image);
            userstatus = itemView.findViewById(R.id.userstatusx);
            username = itemView.findViewById(R.id.user_profile_name);


        }
    }


}
