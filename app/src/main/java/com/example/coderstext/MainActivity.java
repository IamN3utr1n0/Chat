package com.example.coderstext;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
private ViewPager myviewpager;
private TabLayout mytablayout;
private TabsAccessorAdapter mytabsaccessoraadapter;
private FirebaseAuth mAUTH;
private DatabaseReference Rootrefrence;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
mAUTH = FirebaseAuth.getInstance();
Rootrefrence = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Coders Text");

        myviewpager =(ViewPager)findViewById(R.id.main_tabs_pager);
        mytabsaccessoraadapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(mytabsaccessoraadapter);
        mytablayout =(TabLayout)findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewpager);




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
      getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
      if (item.getItemId()==R.id.logout_user){
          mAUTH.signOut();
          Toasty.success(MainActivity.this,"Logged out Successful",Toasty.LENGTH_SHORT,true).show();
          Intent LoginIntent = new Intent(MainActivity.this,Login.class);
          startActivity(LoginIntent);
          finish();

      }
        if (item.getItemId()==R.id.sett){
            Intent settingintent = new Intent(MainActivity.this,Settings.class);
            startActivity(settingintent);
        }
        if (item.getItemId()==R.id.find_fs){

                Intent friendsintent = new Intent(MainActivity.this,FindFriendsActivity.class);
            startActivity(friendsintent);
        }
        if (item.getItemId()==R.id.group){
            Requestnewgroup();
        }
    return true;
    }

    private void Requestnewgroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        builder.setIcon(R.drawable.spl);

        final EditText groupnamefield = new EditText(MainActivity.this);
        groupnamefield.setHint("e.g Coders Hub");
        builder.setView(groupnamefield);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupnamefield.getText().toString();
                if (TextUtils.isEmpty(groupName))
                {
                    Toasty.error(MainActivity.this,"Please Enter Group name",Toasty.LENGTH_SHORT,true).show();
                }

                else{
                    CreateNewGroup(groupName);
                }

            }
        });



        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.cancel();

            }
        });
       builder.show();
    }

    private void CreateNewGroup(String groupName) {
        Rootrefrence.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toasty.success(MainActivity.this,"Group created successful",Toasty.LENGTH_SHORT,true).show();
                        }
                    }
                });
    }


}
