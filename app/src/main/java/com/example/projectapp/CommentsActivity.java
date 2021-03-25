package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private  String Post_Key, current_user_id;
    private CommentsAdapter commentsAdapter;

    private FirebaseAuth mAuth;
    private List<Comments> Commentsdata;
    private DatabaseReference UsersRef,PostsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance("https://project-app-33ac1-default-rtdb.firebaseio.com/").getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance("https://project-app-33ac1-default-rtdb.firebaseio.com/").getReference().child("Posts").child(Post_Key).child("Comments");
        PostsRef.keepSynced(true);

        CommentsList = (RecyclerView) findViewById(R.id.comment_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);
        Commentsdata = new ArrayList<>();

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        PostCommentButton = (ImageButton) findViewById(R.id.comment_post_button);

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        if(datasnapshot.exists())
                        {
                            String UserName = datasnapshot.child("username").getValue().toString();

                            ValidateComment(UserName);

                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds:datasnapshot.getChildren())
                {
                    Comments data = ds.getValue(Comments.class);
                    Commentsdata.add(data);
                }
                commentsAdapter = new CommentsAdapter(Commentsdata);
                CommentsList.setAdapter(commentsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public CommentsViewHolder(@NonNull View itemView, View mView) {
            super(itemView);
            this.mView = itemView;
        }

    }

    private void ValidateComment(String userName) {

        String commentText = CommentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this,"Please write text to comment...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yy");
            final  String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final  String saveCurrentTime = currentTime.format(calForDate.getTime());

            final  String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap CommentsMap = new HashMap();
                CommentsMap.put("uid",current_user_id);
                CommentsMap.put("comment",commentText);
                CommentsMap.put("date",saveCurrentDate);
                CommentsMap.put("time",saveCurrentTime);
                CommentsMap.put("username",userName);
            PostsRef.child(RandomKey).updateChildren(CommentsMap)
            .addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(CommentsActivity.this,"Comment posted Succesfully....",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(CommentsActivity.this,"Error Occured,Try again...",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
