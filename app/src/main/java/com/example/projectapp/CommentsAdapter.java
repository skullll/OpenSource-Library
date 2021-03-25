package com.example.projectapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter {

    List<Comments> commentsdataList;


    public CommentsAdapter(List<Comments> commentsdataList) {
        this.commentsdataList = commentsdataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout,parent,false);
        CommentsViewHolder viewHolderclass = new CommentsViewHolder(view);


        return viewHolderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CommentsViewHolder viewHolderClass = (CommentsViewHolder)holder;

        Comments comments = commentsdataList.get(position);
        viewHolderClass.comments.setText(comments.getComment());
        viewHolderClass.fullname.setText(comments.getUsername());
        viewHolderClass.time.setText(comments.getTime());
        viewHolderClass.date.setText(comments.getDate());

    }

    @Override
    public int getItemCount() {
        return commentsdataList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder{

        TextView fullname, comments,date, time;
        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = (itemView.findViewById(R.id.comment_Username));
            comments = (itemView.findViewById(R.id.comment_text));
            date = (itemView.findViewById(R.id.comment_date));
            time = (itemView.findViewById(R.id.comment_time));
        }
    }
}
