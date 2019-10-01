package com.example.coderstext;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {
    private String msgreceiverID, getMsgreceivername,msgreceiverimage,msgsenderid;
    private Toolbar mtoolbarxc;
    private TextView usernamex,userlastseen;
    private CircleImageView userimagex;
    private FloatingActionButton sendmsg;
    private EditText writemsg;
    private DatabaseReference Rootref;
    private FirebaseAuth mauth;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView usermessageslistt;
    private ScrollView mScrollview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        msgreceiverID = getIntent().getExtras().get("visit_user_id").toString();
        getMsgreceivername = getIntent().getExtras().get("visit_user_name").toString();
        msgreceiverimage = getIntent().getExtras().get("visit_image").toString();
mauth = FirebaseAuth.getInstance();
msgsenderid = mauth.getCurrentUser().getUid();

Rootref = FirebaseDatabase.getInstance().getReference();
        sendmsg = (FloatingActionButton) findViewById(R.id.send_msg_btn) ;
        writemsg = (EditText)findViewById(R.id.input_msg);
        usermessageslistt = (RecyclerView) findViewById(R.id.private_msg_list) ;
        linearLayoutManager = new LinearLayoutManager(this);
        usermessageslistt.setLayoutManager(linearLayoutManager);
        usermessageslistt.setAdapter(messageAdapter);
        intializecontrollers();


        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Sendmsgtouser();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Rootref.child("Messages").child(msgsenderid).child(msgreceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        usermessageslistt.smoothScrollToPosition(usermessageslistt.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();


        Rootref.child("Messages").child(msgsenderid).child(msgreceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void intializecontrollers() {

      mtoolbarxc = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mtoolbarxc);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.chatbar_custom, null);

        TextView name = mCustomView.findViewById(R.id.Custom_profile_name);
        userimagex = mCustomView.findViewById(R.id.profile_image_chat);

        actionBar.setCustomView(mCustomView);

        name.setText(getMsgreceivername);
        Picasso.get().load(msgreceiverimage).placeholder(R.drawable.userfreinds).into(userimagex);
        Toolbar parent =(Toolbar) mCustomView.getParent();
        parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0,0);

        messageAdapter = new MessageAdapter(messagesList);

        usermessageslistt = (RecyclerView)findViewById(R.id.private_msg_list);
        linearLayoutManager=new LinearLayoutManager(this);
        usermessageslistt.setLayoutManager(linearLayoutManager);
        usermessageslistt.setAdapter(messageAdapter);





    }
    private void Sendmsgtouser(){
        String msg =  writemsg.getText().toString();
        if (TextUtils.isEmpty(msg))
        {
            Toasty.error(ChatActivity.this,"Please write message first",Toasty.LENGTH_SHORT,true).show();
        }
        else
            {
                String msessagesenderef = "Messages/"+msgsenderid+"/"+msgreceiverID;
                String msessagereceiverref = "Messages/"+msgreceiverID+"/"+msgsenderid;

                DatabaseReference usermsgkeyref = Rootref.child("Messages").child(msessagesenderef).child(msgreceiverID).push();
                String msgPushId = usermsgkeyref.getKey();

                Map msgtextbody = new HashMap();
                msgtextbody.put("message",msg);
                msgtextbody.put("type","text");
                msgtextbody.put("from",msgsenderid);
                Map msgBpdyDetails = new HashMap();
                msgBpdyDetails.put(msessagesenderef + "/" +msgPushId,msgtextbody);
                msgBpdyDetails.put(msessagereceiverref + "/" +msgPushId,msgtextbody);
                Rootref.updateChildren(msgBpdyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            Toasty.success(ChatActivity.this,"Message sent",Toasty.LENGTH_SHORT,true).show();
                        }
                        else
                        {
                            Toasty.error(ChatActivity.this,"Please check your internet connection",Toasty.LENGTH_SHORT,true).show();
                        }
                        writemsg.setText("");
                    }
                });
            }
    }











}
