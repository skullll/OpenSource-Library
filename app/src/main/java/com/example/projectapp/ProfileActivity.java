package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView UserName, userProfName, userAboutyou, userCountry, userPhone, userGender, userEduc, userDOB, userEmail;
    private CircleImageView userProfImg;

    private FirebaseAuth mAuth;
    private DatabaseReference profileUserRef;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        UserName = (TextView) findViewById(R.id.my_profile_username);
        userProfName = (TextView) findViewById(R.id.my_profile_fullname);
        userAboutyou = (TextView) findViewById(R.id.my_profile_aboutyou);
        userCountry = (TextView) findViewById(R.id.my_profile_country);
        userPhone = (TextView) findViewById(R.id.my_profile_phone);
        userEmail = (TextView) findViewById(R.id.my_profile_Email);
        userGender = (TextView) findViewById(R.id.my_profile_gender);
        userEduc = (TextView) findViewById(R.id.my_profile_education);
        userDOB = (TextView) findViewById(R.id.my_profile_dob);

        userProfImg = (CircleImageView) findViewById(R.id.my_Profile_Pic);

        profileUserRef.addValueEventListener(new ValueEventListener() {
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


                    UserName.setText("@" + myuserName);
                    userProfName.setText(myfullname);
                    userCountry.setText("Country:" + mycountry);
                    userAboutyou.setText(myaboutyou);
                    userPhone.setText("Phone: " + myphoneNo);
                    userEmail.setText("Email ID: " + myEmailid);
                    userGender.setText("Gender:" + mygender);
                    userDOB.setText("DOB:" + mydob);
                    userEduc.setText("Education:" + myEducstatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
