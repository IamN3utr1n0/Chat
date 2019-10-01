package com.example.coderstext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import es.dmoral.toasty.Toasty;

public class
Register extends AppCompatActivity {

    private Button RegisterButton;
    private EditText UserEmail,UserPassword,usern,renterpass;
    private TextView LoginText;
    private FirebaseAuth mAuth;
    private ProgressDialog LOADING;
    private DatabaseReference RootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializefieldsnow();
     mAuth = FirebaseAuth.getInstance();
     RootRef = FirebaseDatabase.getInstance().getReference();

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreatenewAccount();

            }
        });
        LoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginnow = new Intent(Register.this,Login.class);
                startActivity(loginnow );
                finish();
            }
        });

    }


    private void CreatenewAccount(){
        final String email = UserEmail.getText().toString();
        final String password = UserPassword.getText().toString();
        final String reepassword = renterpass.getText().toString();
        final String name = usern.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toasty.error(Register.this,"Please enter Email",Toasty.LENGTH_SHORT,true).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toasty.error(Register.this,"Please enter Password",Toasty.LENGTH_SHORT,true).show();
        }
        else if (TextUtils.isEmpty(reepassword))
        {
            Toasty.error(Register.this,"Please re-enter Password",Toasty.LENGTH_SHORT,true).show();
        }
        else if (TextUtils.isEmpty(name))
        {
            Toasty.error(Register.this,"Please enter your Name",Toasty.LENGTH_SHORT,true).show();
        }
     else if (password.length() < 6)
     {
            UserPassword.setError("Weak Password");
            UserPassword.setText("");
            renterpass.setText("");

     }
        else if (!reepassword.equals(password))
        {
            Toasty.error(Register.this,"Password doesn't match",Toasty.LENGTH_SHORT,true).show();
            UserPassword.setText("");
            renterpass.setText("");
        }
        else
        {LOADING.setTitle("Creating your account");
            LOADING.setMessage("Please wait,while we create your account...");
            LOADING.setCanceledOnTouchOutside(false);
            LOADING.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String devicetoken = FirebaseInstanceId.getInstance().getToken();
                        String cureentUser = mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(cureentUser).setValue("Name :- "+name+" Email :- "+email);
                        RootRef.child("Users").child(cureentUser).child("device_token")
                                .setValue(devicetoken);

                  Intent mainactivity = new Intent(Register.this,MainActivity.class);
                  mainactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(mainactivity);
                        Toasty.success(Register.this,"Account created successfully",Toasty.LENGTH_SHORT,true).show();
                        LOADING.dismiss();
                        finish();
                    }
                        else {
                        String message = task.getException().toString().trim();
                        Toasty.error(Register.this, "" + message, Toasty.LENGTH_SHORT, true).show();
                        LOADING.dismiss();

                    }
                }
            });
        }

    }

    private void initializefieldsnow(){
        RegisterButton = (Button)findViewById(R.id.Register);
        UserEmail = (EditText) findViewById(R.id.registeremail);
        usern = (EditText) findViewById(R.id.nameuser);
        UserPassword = (EditText) findViewById(R.id.registerpassword);
        renterpass = (EditText) findViewById(R.id.repassword);
        LoginText = (TextView)findViewById(R.id.Loginnow);
        LOADING = new ProgressDialog(this);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
