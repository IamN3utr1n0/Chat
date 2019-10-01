package com.example.coderstext;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import es.dmoral.toasty.Toasty;

public class GroupChat extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton send;
    private EditText msg;
    private ScrollView mScrollview;
    private TextView dispaytext;
    private String groupnamecurent,curentUserid,currentusernam,currentdate,currenttime;
    private FirebaseAuth mAuth;
    private DatabaseReference Usersref,groupnameref,groupmessagekeyref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mToolbar = (Toolbar)findViewById(R.id.groupchatbar);
        setSupportActionBar(mToolbar);
        groupnamecurent = getIntent().getExtras().get("groupname").toString();
        getSupportActionBar().setTitle(groupnamecurent);
        Toasty.success(GroupChat.this,groupnamecurent,Toasty.LENGTH_SHORT,true).show();

        mAuth = FirebaseAuth.getInstance();
       curentUserid = mAuth.getCurrentUser().getUid();
       Usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        groupnameref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupnamecurent);
        send = (ImageButton)findViewById(R.id.send_msg);
        msg = (EditText) findViewById(R.id.input_group_msg);
        dispaytext = (TextView) findViewById(R.id.groupchattextdisplay);
        mScrollview = (ScrollView)findViewById(R.id.myscrollview);

        getuserinfo();
  send.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Savemsginfotodatabase();
          msg.setText("");
          mScrollview.fullScroll(ScrollView.FOCUS_DOWN);
      }
  });


    }

    @Override
    protected void onStart() {
        super.onStart();
        groupnameref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    Displaymessages(dataSnapshot);
                    mScrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Displaymessages(dataSnapshot);
                mScrollview.fullScroll(ScrollView.FOCUS_DOWN);
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





    private void getuserinfo() {
        Usersref.child(curentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   currentusernam = dataSnapshot.child("name").getValue().toString();

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Savemsginfotodatabase() {


        String message = msg.getText().toString();
        String messagekey = groupnameref.push().getKey();
        if (TextUtils.isEmpty(message)){
            Toasty.error(GroupChat.this,"Please write msg first...",Toasty.LENGTH_SHORT,true).show();
        }else {
            Calendar calfordate = Calendar.getInstance();
            SimpleDateFormat currentdtaformat = new SimpleDateFormat("MMM dd,yyyy");
            currentdate = currentdtaformat.format(calfordate.getTime());

            Calendar calfortime = Calendar.getInstance();
            SimpleDateFormat currenttimeformat = new SimpleDateFormat("hh:mm a");
            currenttime = currenttimeformat.format(calfortime.getTime());

            HashMap<String,Object> groupmesgkey = new HashMap<>();
            groupnameref.updateChildren(groupmesgkey);
            groupmessagekeyref = groupnameref.child(messagekey);
            HashMap<String,Object>messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentusernam);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentdate);
            messageInfoMap.put("time",currenttime);
            groupmessagekeyref.updateChildren(messageInfoMap);


        }
    }
    private void Displaymessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatdate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatmsg = (String)((DataSnapshot)iterator.next()).getValue();
            String chatname = (String)((DataSnapshot)iterator.next()).getValue();
            String chattime = (String)((DataSnapshot)iterator.next()).getValue();
            dispaytext.append(chatname+":\n"+chatmsg+"\n"+chattime+"    "+chatdate+"\n\n\n");

            mScrollview.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }

}
