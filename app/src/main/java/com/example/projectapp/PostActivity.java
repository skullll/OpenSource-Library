package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Toolbar mToolbar ;
    private ProgressDialog loadingBar;

    private ImageButton SelectPostImage;
    private Button UploadPostButton;
    private EditText PostTitle, PostDescription;

    private static  final int Gallery_Pick=1;
    private Uri ImageUri;
    private  String Description, Title;
    private StorageReference PostsImagesRefrence;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downlodUrl, current_user_id;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance("https://project-app-33ac1-default-rtdb.firebaseio.com/").getReference().child("Posts");

        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UploadPostButton = (Button) findViewById(R.id.upload_post_button);
        PostDescription = (EditText) findViewById(R.id.post_descriptiton);
        PostTitle = (EditText) findViewById(R.id.post_title);
        loadingBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        UploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });


    }

    private void ValidatePostInfo() {
         Description = PostDescription.getText().toString();
         Title = PostTitle.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this,"please select post image...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"please write about your post description...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Title))
        {
            Toast.makeText(this,"please write about your post title...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Add new post");
            loadingBar.setMessage("Please wait, while we uploading new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToStorage();
        }
    }

    private void StoringImageToStorage() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsImagesRefrence.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {
                    downlodUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this,"Post uploaded succesfully...",Toast.LENGTH_SHORT).show();

                    SavingPostInfoToDatabase();
                }
                else
                {
                    String Message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this,"Err occured:" + Message,Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void SavingPostInfoToDatabase() {

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if(datasnapshot.exists())
                {
                    String userFullname = datasnapshot.child("fullname").getValue().toString();
                   // String userProfileImage = datasnapshot.child("profileImage").getValue().toString(); //this line code is not working proprly i'll do it later

                    HashMap postMap = new HashMap();
                        postMap.put("uid",current_user_id);
                        postMap.put("date",saveCurrentDate);
                        postMap.put("time",saveCurrentTime);
                        postMap.put("title",Title);
                        postMap.put("description",Description);
                        postMap.put("postimage",downlodUrl);
                        // postMap.put("profileimage",userProfileImage);
                        postMap.put("fullname",userFullname);
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postMap)
                                .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if(task.isSuccessful())
                                    {
                                        SendUserToMainActivity();
                                        Toast.makeText(PostActivity.this,"new post updated successfully...",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(PostActivity.this,"Error ocurred while updating...",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode== RESULT_OK && data!= null)
        {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(PostActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
}
