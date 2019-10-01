package com.example.coderstext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class PhoneLogin extends AppCompatActivity {

    private EditText mobileno,otp;
    private Button sendmsg,verify;
    private ProgressDialog load;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private RelativeLayout otplay,sendmsg1;
    private ImageView sendsms,verifysendotp;
    private TextView first,second;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        mobileno = (EditText) findViewById(R.id.mobilenofield);
        sendmsg = (Button) findViewById(R.id.sendotpbutton);
        otp = (EditText) findViewById(R.id.otpfield);
        verify = (Button) findViewById(R.id.verifybutton);
        sendsms = (ImageView) findViewById(R.id.sendsmsid);
        verifysendotp = (ImageView) findViewById(R.id.otpimge);
        first = (TextView) findViewById(R.id.one);
        second = (TextView) findViewById(R.id.two);

        second.setVisibility(View.INVISIBLE);
        otp.setVisibility(View.INVISIBLE);
        verify.setVisibility(View.INVISIBLE);
        verifysendotp.setVisibility(View.INVISIBLE);
        load = new ProgressDialog(this);



        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String verificationcode = otp.getText().toString();
                if (TextUtils.isEmpty(verificationcode)){

                    Toasty.error(PhoneLogin.this,"Please enter OTP",Toasty.LENGTH_SHORT,true).show();
                }
                else
                {
                    load.setTitle("Please wait");
                    load.setCanceledOnTouchOutside(false);
                    load.setMessage("Verifying OTP ");
                    load.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });



sendmsg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view)
       {
        String phoneNumber = mobileno.getText().toString();
        if (TextUtils.isEmpty(phoneNumber))
        {
            Toasty.error(PhoneLogin.this,"Please enter mobile no",Toasty.LENGTH_SHORT,true).show();
        }
        else
        {
            load.setTitle("Please wait");
            load.setCanceledOnTouchOutside(false);
            load.setMessage("We are sending OTP to your mobile no");
            load.show();

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    PhoneLogin.this,               // Activity (for callback binding)
                    callbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
        }
    }
});



callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
load.hide();
    signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
        load.dismiss();
        String msg = e.getMessage().toString();
        Toasty.error(PhoneLogin.this,"Something went wrong"+msg,Toasty.LENGTH_SHORT,true).show();


    }
    @Override
    public void onCodeSent(String verificationId,
                           PhoneAuthProvider.ForceResendingToken token) {
        otp.setVisibility(View.VISIBLE);
        verify.setVisibility(View.VISIBLE);
        verifysendotp.setVisibility(View.VISIBLE);
        second.setVisibility(View.VISIBLE);
        first.setVisibility(View.INVISIBLE);
        mVerificationId = verificationId;
        mResendToken = token;
        load.dismiss();
        Toasty.success(PhoneLogin.this,"Code has been sent succesfully",Toasty.LENGTH_SHORT,true).show();



        // ...
    }
};



    }//oncreate here


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            load.dismiss();
                            Toasty.success(PhoneLogin.this,"Congratulations! You are logged in",Toasty.LENGTH_SHORT,true).show();
                            Intent mainintent = new Intent(PhoneLogin.this,MainActivity.class);
                            startActivity(mainintent);
                            finish();
                        }
                        else
                            {
                                load.dismiss();
                                String message = task.getException().toString();
                                Toasty.error(PhoneLogin.this,""+message,Toasty.LENGTH_SHORT,true).show();
                                otp.setVisibility(View.INVISIBLE);
                                verify.setVisibility(View.INVISIBLE);
                                verifysendotp.setVisibility(View.INVISIBLE);
                                second.setVisibility(View.INVISIBLE);

                            }
                    }
                });
    }



}
