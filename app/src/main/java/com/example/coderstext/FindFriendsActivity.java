package com.example.coderstext;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mtolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference useref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        useref = FirebaseDatabase.getInstance().getReference().child("Users");
        FindFriendsRecyclerList = (RecyclerView)findViewById(R.id.finf_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        mtolbar = (Toolbar)findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mtolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contactsfriends> options = new FirebaseRecyclerOptions.Builder<Contactsfriends>()
                .setQuery(useref ,Contactsfriends.class)
                .build();
        FirebaseRecyclerAdapter<Contactsfriends,FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contactsfriends, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contactsfriends model) {
                holder.username.setText(model.getName());
                holder.userstatsu.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.userfreinds).into(holder.profileimage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id = getRef(position).getKey();
                        Intent profileIntent = new Intent(FindFriendsActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
          View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
          FindFriendViewHolder viewHolder = new
                  FindFriendViewHolder(view);
          return viewHolder;
            }
        };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
TextView username,userstatsu;
CircleImageView profileimage;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userstatsu = itemView.findViewById(R.id.userstatusx);
            profileimage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
