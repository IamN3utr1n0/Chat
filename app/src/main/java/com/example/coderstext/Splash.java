package com.example.coderstext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Splash extends AppCompatActivity {
    private FirebaseAuth mAUTHx;
    private DatabaseReference Rootrefrence;

        @SuppressLint("InlinedApi")
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash);
            mAUTHx = FirebaseAuth.getInstance();
            Rootrefrence = FirebaseDatabase.getInstance().getReference();
           final ImageView logo=(ImageView)findViewById(R.id.imageView);
            TextView textView = (TextView)findViewById(R.id.textView2);
         final    RelativeLayout anim = (RelativeLayout)findViewById(R.id.bgx);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAUTHx.getCurrentUser() == null) {
                        Intent intent = new Intent(Splash.this, Login.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(Splash.this, logo, getString(R.string.act))
                                    .  makeSceneTransitionAnimation(Splash.this, anim, getString(R.string.act));
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                        else {
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        VerifyUserExixtence();

                    }
                                 }
            },2000);

            Animation myanim = AnimationUtils.loadAnimation(this,R.anim.splash);
            logo.startAnimation(myanim);
            Animation myanimtext = AnimationUtils.loadAnimation(this,R.anim.blink);
            textView.startAnimation(myanimtext);

        }

    private void VerifyUserExixtence() {
        String curentuserid = mAUTHx.getCurrentUser().getUid();
        Rootrefrence.child("Users").child(curentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists()))
                {
                    Intent main = new Intent(Splash.this,MainActivity.class);
                    startActivity(main);
                    finish();
                }
                else
                {
                    Intent LoginIntent = new Intent(Splash.this,Settings.class);
                    startActivity(LoginIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
    }
}
