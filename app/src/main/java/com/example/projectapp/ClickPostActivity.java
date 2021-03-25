package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostTitle, PostDescription;
    private String PostKey;
    private DatabaseReference ClickPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        PostImage = (ImageView) findViewById(R.id.click_post_image);
        PostTitle = (TextView) findViewById(R.id.click_post_title);
        PostDescription = (TextView) findViewById(R.id.click_post_descp);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String title = datasnapshot.child("title").getValue().toString();
                String description = datasnapshot.child("description").getValue().toString();
                String image = datasnapshot.child("postimage").getValue().toString();

                PostTitle.setText(title);
                PostDescription.setText(description);
                Picasso.with(ClickPostActivity.this).load(image).into(PostImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
