package com.example.zhouyepang.instagramapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.*;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameText;
        public TextView comment;
        public ViewHolder(View v) {
            super(v);
            userNameText = v.findViewById(R.id.comment_user_name);
            comment = v.findViewById(R.id.comment_content);
        }
    }

    private ArrayList<Comment> commentContent;

    public CommentAdapter(ArrayList<Comment> commentConent) {
        this.commentContent = commentConent;
    }

    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(CommentAdapter.ViewHolder viewHolder, int position) {

        Comment comment = commentContent.get(position);
        String userName =  MainActivity.lookUpUserID.lookUpUserNamebyId(comment.getUserId())+ " : ";
        String commentContent = comment.getContent();
        TextView usernameView = viewHolder.userNameText;
        TextView commentView = viewHolder.comment;
        System.out.println("user name : "+userName);
        System.out.println("comment content : "+commentContent);
        commentView.setText(commentContent);
        usernameView.setText(userName);
    }

    public int getItemCount() {
        return commentContent.size();
    }


}
