package com.example.coderstext;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mauth;
    private DatabaseReference useref;

    public  MessageAdapter (List<Messages>userMessagesList)
    {
        this.userMessagesList = userMessagesList;

    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView sendermsgtext,recievermsgtext;
        public CircleImageView recieverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendermsgtext = (TextView)itemView.findViewById(R.id.sender_messages_text);
            recievermsgtext = (TextView)itemView.findViewById(R.id.receiver_messages_text);
            recieverProfileImage = (CircleImageView)itemView.findViewById(R.id.msg_profile_image);



        }
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout,viewGroup,false);
        mauth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {

        String messagesenderid = mauth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(i);
        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        useref = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        useref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image"))
                {


                    String recieverimage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(recieverimage).placeholder(R.drawable.userfreinds).into(messageViewHolder.recieverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (fromMessageType.equals("text"))
        {
            messageViewHolder.recievermsgtext.setVisibility(View.INVISIBLE);
            messageViewHolder.recieverProfileImage.setVisibility(View.INVISIBLE);
            messageViewHolder.sendermsgtext.setVisibility(View.INVISIBLE);
            if (fromUserID.equals(messagesenderid))
            {
                messageViewHolder.sendermsgtext.setVisibility(View.VISIBLE);
                messageViewHolder.sendermsgtext.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.sendermsgtext.setText(messages.getMessage());
            }
            else
            {

                messageViewHolder.recieverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.recievermsgtext.setVisibility(View.VISIBLE);
                messageViewHolder.recievermsgtext.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.recievermsgtext.setText(messages.getMessage());
            }
        }

    }

    @Override
    public int getItemCount() {
     return userMessagesList.size();
    }


}
