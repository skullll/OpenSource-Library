package com.example.projectapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HelperFindFriendsAdapter extends RecyclerView.Adapter {

    List<FindFriends> findFriendsList ;

    public HelperFindFriendsAdapter(List<FindFriends> findFriendsList) {
        this.findFriendsList = findFriendsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout,parent,false);
        ViewHolderclass viewHolderclass = new ViewHolderclass(view);

        return viewHolderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderclass viewHolderclass = (ViewHolderclass)holder;

        FindFriends findFriends = findFriendsList.get(position);
        viewHolderclass.fullname.setText(findFriends.getFullname());
        viewHolderclass.aboutyou.setText(findFriends.getAboutyou());
        viewHolderclass.profileimage.setText(findFriends.getProfileimage());

    }

    @Override
    public int getItemCount() {
        return findFriendsList.size();
    }

    public class ViewHolderclass extends RecyclerView.ViewHolder
    {
        TextView fullname , aboutyou, profileimage ;
        public ViewHolderclass(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.comments_user_name);
            aboutyou = itemView.findViewById(R.id.comments_about_you);
            profileimage = itemView.findViewById(R.id.comments_profile_image);
        }
    }
}
