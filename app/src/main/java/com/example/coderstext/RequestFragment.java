package com.example.coderstext;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
private View RequestFragmentView;
private RecyclerView myRequestlist;
private DatabaseReference Charrequestref,useref,ContactsRef;
private FirebaseAuth mauth;
private String currentuserid;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
mauth = FirebaseAuth.getInstance();
currentuserid = mauth.getCurrentUser().getUid();
      Charrequestref = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        useref = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        RequestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
        myRequestlist = (RecyclerView)RequestFragmentView.findViewById(R.id.Chat_Request_list);
        myRequestlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestFragmentView;

    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerOptions<Contactsfriends> options =
                new FirebaseRecyclerOptions.Builder<Contactsfriends>()
                .setQuery(Charrequestref.child(currentuserid),Contactsfriends.class)
                .build();
        FirebaseRecyclerAdapter<Contactsfriends,RequestViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contactsfriends, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contactsfriends model) {

                       holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();
                        DatabaseReference getTypeRef = getRef(position).child("request type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists())
                                {
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("received"))
                                    {
                                        useref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.hasChild("image")){

                                            final  String requestprofileimage =dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(requestprofileimage).placeholder(R.drawable.userfreinds).into(holder.profileimage);
                                            }

                                                final  String requestusername =dataSnapshot.child("name").getValue().toString();
                                                final  String requestuserstatus=dataSnapshot.child("status").getValue().toString();

                                                holder.username.setText(requestusername);
                                                holder.userstatus.setText("Wants to connect to you");

                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    CharSequence optionsx[] = new CharSequence[]
                                                            {
                                                                    "Accept","Cancel"
                                                            };
                                                    AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
                                                    builder.setTitle(requestusername+"  Chat Request");
                                                    builder.setItems(optionsx, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0)
                                                        {
                                                            ContactsRef.child(currentuserid).child(list_user_id).child("Contact")
                                                                    .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        ContactsRef.child(list_user_id).child(currentuserid).child("Contact")
                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                Charrequestref.child(currentuserid).child(list_user_id)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if (task.isSuccessful())
                                                                                                {
                                                                                                    Charrequestref.child(currentuserid).child(list_user_id)
                                                                                                            .removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                                {
                                                                                                                    if (task.isSuccessful())
                                                                                                                    {
                                                                                                                        Charrequestref.child(list_user_id).child(currentuserid)
                                                                                                                                .removeValue()
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                                                    {
                                                                                                                                        if (task.isSuccessful())
                                                                                                                                        {
                                                                                                                                            Toasty.success(getContext(),"Contact saved",Toasty.LENGTH_SHORT,true).show();                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });
                                                                                                                    }
                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            }
                                                                                        });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                            if (i == 1)
                                                            {
                                                                Charrequestref.child(currentuserid).child(list_user_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    Charrequestref.child(list_user_id).child(currentuserid)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                {
                                                                                                    if (task.isSuccessful())
                                                                                                    {
                                                                                                        Toasty.success(getContext(),"Request Deleted",Toasty.LENGTH_SHORT,true).show();                                                                                                                                        }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        RequestViewHolder holder = new RequestViewHolder(view);
                        return holder;
                    }
                };
        myRequestlist.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView username,userstatus;
        CircleImageView profileimage;
        Button AcceptButton,CancelButton;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.userstatusx);
            profileimage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}
