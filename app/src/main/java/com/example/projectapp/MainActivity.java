package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.media.Image;
import android.media.ImageWriter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef,LikeRef;

    String currentUserID;
    Boolean LikeChecker= false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        try {
            currentUserID = mAuth.getCurrentUser().getUid();
        }catch (NullPointerException ignored){

        }
        UsersRef = FirebaseDatabase.getInstance("https://project-app-33ac1-default-rtdb.firebaseio.com/").getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance("https://project-app-33ac1-default-rtdb.firebaseio.com/").getReference().child("Posts");
        PostsRef.keepSynced(true);
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");


        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);


        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);


        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });


        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();
            }
        });


        DisplayAllUsersPosts();
    }



    private void DisplayAllUsersPosts()
    {
        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(PostsRef, Posts.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {

            @Override
            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.all_posts_layout, parent, false);

                return new PostsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(PostsViewHolder viewHolder, int position, Posts model) {
                final String PostKey = getRef(position).getKey();

                viewHolder.setFullname(model.getFullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setTitle(model.getTitle());
                //viewHolder.setProfileimage( model.getProfileimage());
                viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                //Glide.with(viewHolder.mView.getContext()).load(model.getPostimage()).into();
                //Picasso.with(viewHolder.setPostimage();
                viewHolder.setLikeButtonStatus(PostKey);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(MainActivity.this,ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(clickPostIntent);
                    }
                });

                viewHolder.CommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent commentasIntent = new Intent(MainActivity.this,CommentsActivity.class);
                        commentasIntent.putExtra("PostKey",PostKey);
                        startActivity(commentasIntent);
                    }
                });

                viewHolder.LikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LikeChecker = true;

                        LikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                               if(LikeChecker.equals(true))
                               {
                                   if(datasnapshot.child(PostKey).hasChild(currentUserID))
                                   {
                                       LikeRef.child(PostKey).child(currentUserID).removeValue();
                                       LikeChecker=false;
                                   }
                                   else
                                   {
                                       LikeRef.child(PostKey).child(currentUserID).setValue(true);
                                       LikeChecker=false;
                                   }
                               }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
            }
        };
        adapter.startListening();
        postList.setAdapter(adapter);

    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        ImageButton LikeButton, CommentButton;
        TextView DisplayNoLikes;
        int countLikes;
        String currentUserID;
        DatabaseReference LikesRef;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            LikeButton = (ImageButton) mView.findViewById(R.id.like_button);
            CommentButton =(ImageButton) mView.findViewById(R.id.comment_button);
            DisplayNoLikes = (TextView) mView.findViewById(R.id.display_no_likes);

            LikesRef =FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserID =FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus(final String PostKey)
        {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    if(datasnapshot.child(PostKey).hasChild(currentUserID))
                    {
                        countLikes = (int) datasnapshot.child(PostKey).getChildrenCount();
                        LikeButton.setImageResource(R.drawable.like);
                        DisplayNoLikes.setText((Integer.toString(countLikes)+ (" Likes")));
                    }
                    else
                    {
                        countLikes = (int) datasnapshot.child(PostKey).getChildrenCount();
                        LikeButton.setImageResource(R.drawable.dislike);
                        DisplayNoLikes.setText(Integer.toString(countLikes)+(" Likes"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }

        public void setTitle(String title)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_title);
            PostDescription.setText(title);
        }

        public void setPostimage(Context ctx1, String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx1).load(postimage).into(PostImage);
            //Picasso.with().load(setPostimage()).into(PostImage);
            //Glide.with(PostImage.ctx1).load()
        }
    }


    private void SendUserToPostActivity()
    {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }



    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }



    private void CheckUserExistence()
    {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(MainActivity.this, setupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }



    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, loginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void UserMenuSelector(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                SendUserToProfileActivity();
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "Friend List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Find_Friends:
                SendUserToFindFriendsActivity();
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Settings:
                SendUserToSettingsActivity();
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }

    private void SendUserToSettingsActivity()
    {
        Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingIntent);
    }

    private void SendUserToProfileActivity()
    {
        Intent ProfIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(ProfIntent);
    }

    private void SendUserToFindFriendsActivity()
    {
        Intent FFIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(FFIntent);
    }

}
