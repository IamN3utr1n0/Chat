package com.example.coderstext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private TextView Reset,signupherenow;
    private Button  btnLogin,mobile;
    private ProgressDialog mDialog;
    private DatabaseReference useref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Firebase auth instance




useref = FirebaseDatabase.getInstance().getReference().child("Users");
        mDialog = new ProgressDialog(this);
        inputEmail = (EditText) findViewById(R.id.Email);
        inputPassword = (EditText) findViewById(R.id.Password);
        btnLogin = (Button) findViewById(R.id.loginbutton);
        mobile = (Button) findViewById(R.id.loginphone);
        Reset = (TextView) findViewById(R.id.forgot);
        signupherenow = (TextView) findViewById(R.id.signup);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        signupherenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

       Reset.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(Login.this, ResetPassword.class));
               finish();
           }
       });
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, PhoneLogin.class));
                finish();
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toasty.error(Login.this,"Please enter Email",Toasty.LENGTH_SHORT,true).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                   Toasty.error(Login.this,"Please enter Password",Toasty.LENGTH_SHORT,true).show();
                    return;
                }

               mDialog.setTitle("Please wait");
                mDialog.setCanceledOnTouchOutside(false);
               mDialog.setMessage("Logging in.....");
               mDialog.show();
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mDialog.dismiss();
                                if (task.isSuccessful()) {
                                    // there was an error
                                    String currentuserId = auth.getCurrentUser().getUid();
                                    String devicetoken = FirebaseInstanceId.getInstance().getToken();
                                    useref.child(currentuserId).child("device_token")
                                            .setValue(devicetoken)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful())
                                                 {  mDialog.dismiss();
                                                     Intent intent = new Intent(Login.this,MainActivity.class);
                                                     startActivity(intent);
                                                     finish();
                                                 }
                                                }
                                            });
                                    mDialog.dismiss();
                                    Toasty.success(Login.this,"Login Successful",Toasty.LENGTH_SHORT,true).show();
                                    Intent intent = new Intent(Login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    }
                                else {
                                if (password.length() < 6) {
                                    inputPassword.setError("Weak Password");
                                    inputPassword.setText("");
                                    mDialog.dismiss();
                                } else {
                                    Toasty.error(Login.this,"Login Failed",Toasty.LENGTH_SHORT,true).show();
                                    mDialog.dismiss();

                                }}
                            }
                        });
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}