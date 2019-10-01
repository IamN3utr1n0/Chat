package com.example.coderstext;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {
private String receiverUserId,Current_State,sender_user_id;
private CircleImageView userProfileimage;
private TextView userprofilename,getUserprofilestatus;
private Button Sendmessagereqbutton,Declinemessagereqbutton;
private DatabaseReference UserRef,ChatRequestRef,ContactRef,notificationref;
private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        notificationref = FirebaseDatabase.getInstance().getReference().child("Notifications");
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
mAuth = FirebaseAuth.getInstance();
        userProfileimage = (CircleImageView)findViewById(R.id.visit_profile_image);
        userprofilename = (TextView) findViewById(R.id.visit_user_name);
        getUserprofilestatus = (TextView) findViewById(R.id.visit_user_status);
        Sendmessagereqbutton = (Button) findViewById(R.id.send_msg_req_button);
        Declinemessagereqbutton = (Button) findViewById(R.id.decline_msg_req_button);
        Current_State = "new";
        sender_user_id = mAuth.getCurrentUser().getUid();
        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
       UserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists() && (dataSnapshot.hasChild("image"))){
                   String userimage = dataSnapshot.child("image").getValue().toString();
                   String username = dataSnapshot.child("name").getValue().toString();
                   String userstatus = dataSnapshot.child("status").getValue().toString();

                   Picasso.get().load(userimage).placeholder(R.drawable.userfreinds).into(userProfileimage);
                   userprofilename.setText(username);
                   getUserprofilestatus.setText(userstatus);
                   managechatrequest();


               }
               else
               {
                   String username = dataSnapshot.child("name").getValue().toString();
                   String userstatus = dataSnapshot.child("status").getValue().toString();
                   userprofilename.setText(username);
                   getUserprofilestatus.setText(userstatus);
                   managechatrequest();

               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       }) ;
    }

    private void managechatrequest()
    {
        ChatRequestRef.child(sender_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.hasChild(receiverUserId))
             {
              String request_type = dataSnapshot.child(receiverUserId).child("request type").getValue().toString();
              if (request_type.equals("sent")){
                  Current_State = "request sent";
                  Sendmessagereqbutton.setText("Cancel Request");

              }
              else if (request_type.equals("received"))
              {
                  Current_State = "request received";
                  Sendmessagereqbutton.setText("Accept Request");
                  Declinemessagereqbutton.setVisibility(View.VISIBLE);
                  Declinemessagereqbutton.setEnabled(true);
                  Declinemessagereqbutton.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          CnacelChatRequest();

                      }
                  });
              }
             }
             else
             {
                 ContactRef.child(sender_user_id)
                         .addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 if (dataSnapshot.hasChild(receiverUserId))
                                 {
                                     Current_State = "friends";
                                     Sendmessagereqbutton.setText("Unfriend");
                                 }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                         });
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!sender_user_id.equals(receiverUserId))
        {
            Sendmessagereqbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Sendmessagereqbutton.setEnabled(false);
                    if (  Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if (Current_State.equals("request sent"))
                    {
                        CnacelChatRequest();
                    }
                    if (Current_State.equals("request received"))
                    {
                       AcceptChatRequest();
                    }
                    if (Current_State.equals("friends"))
                    {
                       Unfriend();
                    }
                }
            });
        }
        else
        {
            Sendmessagereqbutton.setVisibility(View.INVISIBLE);
        }
    }

    private void Unfriend() {

        ContactRef.child(sender_user_id).child(receiverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    ContactRef.child(receiverUserId).child(sender_user_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Sendmessagereqbutton.setEnabled(true);
                                Current_State = "new";
                                Sendmessagereqbutton.setText("Send Mesaage");
                                Declinemessagereqbutton.setVisibility(View.INVISIBLE);
                                Declinemessagereqbutton.setEnabled(false);
                            }
                        }
                    });

                }
            }
        });

    }

    private void AcceptChatRequest() {
        ContactRef.child(sender_user_id).child(receiverUserId)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           ContactRef.child(receiverUserId).child(sender_user_id)
                                   .child("Contacts").setValue("saved")
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful())
                                           {
                                           ChatRequestRef.child(sender_user_id).child(receiverUserId)
                                                   .removeValue()
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                         if (task.isSuccessful())
                                                         {
                                                             ChatRequestRef.child(receiverUserId).child(sender_user_id)
                                                                     .removeValue()
                                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Void> task) {
                                                                         Sendmessagereqbutton.setEnabled(true);
                                                                         Current_State = "friends";
                                                                         Sendmessagereqbutton.setText("Unfriend");
                                                                         Declinemessagereqbutton.setVisibility(View.INVISIBLE);
                                                                         Declinemessagereqbutton.setEnabled(false);
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

    private void CnacelChatRequest() {
        ChatRequestRef.child(sender_user_id).child(receiverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    ChatRequestRef.child(receiverUserId).child(sender_user_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Sendmessagereqbutton.setEnabled(true);
                                Current_State = "new";
                                Sendmessagereqbutton.setText("Send Mesaage");
                                Declinemessagereqbutton.setVisibility(View.INVISIBLE);
                                Declinemessagereqbutton.setEnabled(false);
                            }
                        }
                    });

                }
            }
        });
    }

    private void SendChatRequest() {
        ChatRequestRef.child(sender_user_id).child(receiverUserId)
                .child("request type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                        ChatRequestRef.child(receiverUserId).child(sender_user_id)
                                .child("request type").setValue("received")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            HashMap<String,String>chatNotificationMap = new HashMap<>();
                                            chatNotificationMap.put("from",sender_user_id);
                                            chatNotificationMap.put("type","request");
                                            notificationref.child(receiverUserId).push()
                                                    .setValue(chatNotificationMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                Sendmessagereqbutton.setEnabled(true);
                                                                Current_State = "request sent";
                                                                Sendmessagereqbutton.setText("Cancel Request");


                                                            }
                                                        }
                                                    });


                                        }
                                    }
                                });
                    }}
                });
    }
}
