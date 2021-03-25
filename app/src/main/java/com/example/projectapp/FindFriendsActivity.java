package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton Searchbutton;
    private EditText Searchinputtext;
    private List<FindFriends> findFriendsData;
    private HelperFindFriendsAdapter findFriendsAdapter;

    private RecyclerView SearchResultList;
    private DatabaseReference allUsersDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Find Friends");

        SearchResultList = (RecyclerView) findViewById(R.id.search_result);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));
        findFriendsData = new ArrayList<>();

        Searchbutton = (ImageButton) findViewById(R.id.search_friends_button);
        Searchinputtext = (EditText) findViewById(R.id.search_box);

        Searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = Searchinputtext.getText().toString();

                SearchPeopleandFriends(searchBoxInput);
            }
        });
    }

    private void SearchPeopleandFriends(String searchBoxInput) {

        Toast.makeText(this,"Searching....", Toast.LENGTH_LONG).show();

        Query searchpeopleandfriendsQuery = allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");


        searchpeopleandfriendsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds:datasnapshot.getChildren())
                {
                    FindFriends data = ds.getValue(FindFriends.class);
                    findFriendsData.add(data);
                }
                findFriendsAdapter = new HelperFindFriendsAdapter(findFriendsData);
                SearchResultList.setAdapter(findFriendsAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public  static class  FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
         public FindFriendsViewHolder(View itemView)
         {
             super(itemView);
             mView = itemView;
         }
    }
}
