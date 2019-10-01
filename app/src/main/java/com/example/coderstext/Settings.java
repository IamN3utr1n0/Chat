package com.example.coderstext;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.flags.IFlagProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.editdrawabletext.DrawablePosition;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class Settings extends AppCompatActivity {
private Button UpdateAccountSettings;
private EditText user_name,user_status;
private CircleImageView user_image;
private ImageView ureserima;
private String cureentid;
private ProgressDialog loadi;
private Toolbar mtoolbarx;

    private FirebaseAuth mAuthD;
    private DatabaseReference RootRef;
    private Button editnme,editststus;
    private static final int gallerypic = 0;
    private StorageReference userprofileimagesref;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

mAuthD = FirebaseAuth.getInstance();
cureentid = mAuthD.getCurrentUser().getUid();
RootRef = FirebaseDatabase.getInstance().getReference();
userprofileimagesref = FirebaseStorage.getInstance().getReference().child("Profile Images");


        mtoolbarx = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(mtoolbarx);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
        UpdateAccountSettings = (Button)findViewById(R.id.Update);
        user_name = (EditText)findViewById(R.id.setusername);
        user_status = (EditText)findViewById(R.id.set_status);
        user_image = (CircleImageView)findViewById(R.id.userimage);
        loadi = new ProgressDialog(this);


        editnme = (Button)findViewById(R.id.editname);
        editststus = (Button)findViewById(R.id.editstatus);
        user_name.setEnabled(false);
        user_status.setEnabled(false);
        UpdateAccountSettings.setEnabled(false);

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,gallerypic);
            }
        });

        editnme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_name.setEnabled(true);
                UpdateAccountSettings.setEnabled(true);
                user_name.setFocusableInTouchMode(true);
                user_name.requestFocus();
                user_name.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                user_name.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

            }
        });

        editststus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_status.setEnabled(true);
                user_status.setFocusableInTouchMode(true);
                user_status.requestFocus();
                user_status.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                user_status.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

                UpdateAccountSettings.setEnabled(true);

            }
        });

UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        UpdateDetails();
        user_name.setEnabled(false);
        user_status.setEnabled(false);

    }
});

RetrieveUserInfo();

    }
    private void UpdateDetails(){
        String username = user_name.getText().toString();
        String status = user_status.getText().toString();
        if (TextUtils.isEmpty(username))
        {
            Toasty.error(Settings.this,"Please enter name",Toasty.LENGTH_SHORT,true).show();
        }
        if (TextUtils.isEmpty(status))
        {
            Toasty.error(Settings.this,"Please write status",Toasty.LENGTH_SHORT,true).show();
        }
        else
        {
            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("status",status);
            profileMap.put("name",username);

            RootRef.child("Users").child(cureentid).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toasty.success(Settings.this,"Profile Updated successfully",Toasty.LENGTH_SHORT,true).show();
                        Intent mainactivity = new Intent(Settings.this,MainActivity.class);
                        mainactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainactivity);
                    }
                    else {

                        String message = task.getException().toString().trim();
                        Toasty.error(Settings.this, "" + message, Toasty.LENGTH_SHORT, true).show();
                    }
                }
            });



        }
    }
    private void RetrieveUserInfo(){
        RootRef.child("Users").child(cureentid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
              {

                  String  retreiveusername = dataSnapshot.child("name").getValue().toString();
                  String  retreivestatus = dataSnapshot.child("status").getValue().toString();
                  String  retrieveimage = dataSnapshot.child("image").getValue().toString();
                  Picasso.get().load(retrieveimage).into(user_image);


                  user_name.setText(retreiveusername);
                  user_status.setText(retreivestatus);

              }
              else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                  String  retreiveusername = dataSnapshot.child("name").getValue().toString();
                  String  retreivestatus = dataSnapshot.child("status").getValue().toString();
                  user_name.setText(retreiveusername);
                  user_status.setText(retreivestatus);

              }
              else {
                  Toasty.error(Settings.this,"Please Update your Status",Toasty.LENGTH_SHORT,true).show();
              }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==gallerypic && resultCode==RESULT_OK && data!=null){
            Uri  Imageuri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode==RESULT_OK)
            {   final StorageReference  fielPath = userprofileimagesref.child(cureentid+".jpg");
                      final  Uri resulturi = result.getUri();

                fielPath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            fielPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloadUri = uri;
                                    String download_url = uri.toString();
                                    RootRef.child("Users").child(cureentid).child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override

                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {
                                                loadi.dismiss();
                                                Toasty.success(Settings.this, "Profile Image Updated" , Toasty.LENGTH_SHORT, true).show();
                                          Picasso.get().load(downloadUri).into(user_image);

                                            }else {
                                                Toast.makeText(Settings.this, "Error happened during the upload process", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }); }
                            });

                        }else{
                            Toast.makeText(Settings.this, "Error happened during the upload process", Toast.LENGTH_LONG ).show();
                        }
                    }
                }) .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //displaying the upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        loadi.setTitle("Please wait");
                        loadi.setCanceledOnTouchOutside(false);
                        loadi.setMessage("Uploaded " + ((int) progress) + " %...");
                        loadi.show();
                    }
                });


            }
        }
    }
}
