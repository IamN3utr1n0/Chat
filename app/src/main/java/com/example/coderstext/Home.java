package com.example.coderstext;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {


    private Toolbar mToolbar;
    private ViewPager myviewpager;
    private TabLayout mytablayout;
    private TabsAccessorAdapter mytabsaccessoraadapter;

    private FirebaseAuth mAuth;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null)
        {
            Intent LoginIntent = new Intent(Home.this,Login.class);

            LoginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(LoginIntent);
        }




    }





}
