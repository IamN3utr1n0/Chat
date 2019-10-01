package com.example.coderstext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class ResetPassword extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset;
    private  TextView btnBack;
    private FirebaseAuth auth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = (EditText) findViewById(R.id.Emailreset);
        btnReset = (Button) findViewById(R.id.resetbutton);
        btnBack = (TextView) findViewById(R.id.backreset);
        mDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toasty.error(ResetPassword.this,"Please enter your Email",Toasty.LENGTH_SHORT,true).show();
                    return;
                }

                mDialog.setTitle("PLease wait");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setMessage("We are sending you the password reset link");
                mDialog.show();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mDialog.dismiss();
                                    Toasty.success(ResetPassword.this,"We have sent you instructions to reset your password!",Toasty.LENGTH_SHORT,true).show();
                                } else {
                                    Toasty.error(ResetPassword.this,"Failed to send reset email!",Toasty.LENGTH_SHORT,true).show();

                                }

                                mDialog.dismiss();
                            }
                        });
            }
        });
    }

}