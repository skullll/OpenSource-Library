package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText userName, userProfName, userAboutyou, userCountry, userPhone, userGender, userEduc, userDOB, userEmail;
    private Button updateSettingButton;
    private CircleImageView userProfImg;
    private ProgressDialog loadingBar;

    private DatabaseReference SettingsUserRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;

    private String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfName = (EditText) findViewById(R.id.settings_profile_fullname);
        userAboutyou = (EditText) findViewById(R.id.settings_about_you);
        userCountry = (EditText) findViewById(R.id.settings_profile_country);
        userPhone = (EditText) findViewById(R.id.settings_userphone);
        userEmail = (EditText) findViewById(R.id.settings_userEmail);
        userGender = (EditText) findViewById(R.id.settings_gender);
        userEduc = (EditText) findViewById(R.id.settings_education_status);
        userDOB = (EditText) findViewById(R.id.settings_profile_DOB);

        userProfImg = (CircleImageView) findViewById(R.id.settings_profile_image);

        updateSettingButton = (Button) findViewById(R.id.setting_update_button);

        loadingBar = new ProgressDialog(this);

        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(datasnapshot.exists())
                {
                    String myuserName = datasnapshot.child("username").getValue().toString();
                    String myfullname = datasnapshot.child("fullname").getValue().toString();
                    String mycountry = datasnapshot.child("country").getValue().toString();
                    String myphoneNo = datasnapshot.child("phoneno").getValue().toString();
                    String myaboutyou = datasnapshot.child("aboutyou").getValue().toString();
                    String myEmailid = datasnapshot.child("emailid").getValue().toString();
                    String mygender = datasnapshot.child("gender").getValue().toString();
                    String mydob = datasnapshot.child("dob").getValue().toString();
                    String myEducstatus = datasnapshot.child("education").getValue().toString();

                    //Picasso.get().load(myP)


                    userName.setText(myuserName);
                    userProfName.setText(myfullname);
                    userCountry.setText(mycountry);
                    userAboutyou.setText(myaboutyou);
                    userPhone.setText(myphoneNo);
                    userEmail.setText(myEmailid);
                    userGender.setText(mygender);
                    userDOB.setText(mydob);
                    userEduc.setText(myEducstatus);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });

        userProfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                            SettingsUserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SettingsActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }


    private void ValidateAccountInfo() {

        String username = userName.getText().toString();
        String fullname = userProfName.getText().toString();
        String aboutyou = userAboutyou.getText().toString();
        String country = userCountry.getText().toString();
        String phoneno = userPhone.getText().toString();
        String emailid = userEmail.getText().toString();
        String gender = userGender.getText().toString();
        String dob = userDOB.getText().toString();
        String eduction = userEduc.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please, enter your Username...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this,"Please, enter your fullname...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(aboutyou))
        {
            Toast.makeText(this,"Please, enter your About you...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(country))
        {
            Toast.makeText(this,"Please, enter your Country...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneno))
        {
            Toast.makeText(this,"Please, enter your Phone number...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(emailid))
        {
            Toast.makeText(this,"Please, enter your Email ID...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(gender))
        {
            Toast.makeText(this,"Please, enter your Gender...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(dob))
        {
            Toast.makeText(this,"Please, enter your Date of Birth...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(eduction))
        {
            Toast.makeText(this,"Please, enter your Education Qualification...",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait, while we updating your profile image...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            UpdateAccountInfo(username,fullname,aboutyou,phoneno,emailid,gender,dob,eduction,country);
        }

    }

    private void UpdateAccountInfo(String username, String fullname, String aboutyou, String phoneno, String emailid, String gender, String dob, String education, String country) {

        HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("aboutyou",aboutyou);
            userMap.put("phoneno",phoneno);
            userMap.put("emailid",emailid);
            userMap.put("gender",gender);
            userMap.put("country",country);
            userMap.put("dob",dob);
            userMap.put("education",education);

        SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this,"Account settings updated successfully...",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this,"Error occured,while updating account settings...",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
        });

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}
