package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import java.lang.reflect.Array;

/**
 * Display who likes the post
 * who comment the post
 * and allow user to leave their comment
 */
public class DisplayPostDetail extends AppCompatActivity {
    private String imageId;
    private String currentUserId;
    private String content;
    private Button confirm;
    DatabaseReference comments;
    ArrayList <Comment> commentContents;
    ArrayList <String> userNames;
    private ArrayList<String> likedNames;
    private TextView likeContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        likedNames = new ArrayList<String>();
        imageId = intent.getStringExtra("imageId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.activity_display_post_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Likes And Comments");
        confirm = (Button) findViewById(R.id.ConfirmComment);
        comments = MainActivity.database.child("comments");
        commentContents = new ArrayList<Comment>();
        userNames = new ArrayList<String>();
        likeContent = (TextView) findViewById(R.id.likesDiplay);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComment(v);
            }
        });

        comments.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                if(comment.getImageId().equals(imageId)) {
                    commentContents.add(comment);
                    displayComments(commentContents);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        displayUserLiked();
    }
    // set the comments from the database
    public void setComment(View view){
        EditText enteredContent = (EditText) findViewById(R.id.commentContent);
        content = enteredContent.getText().toString();
        Comment newComment = new Comment(imageId, currentUserId, content);
        String key = MainActivity.database.child("comments").push().getKey();
        MainActivity.database.child("comments").child(key).setValue(newComment);
    }
    // display the comments
    public void displayComments (ArrayList<Comment> commentContents){
        RecyclerView commentView = (RecyclerView) findViewById(R.id.commentSection);
        CommentAdapter adapter = new CommentAdapter(commentContents);
        commentView.setAdapter(adapter);
        commentView.setLayoutManager(new LinearLayoutManager(this));
    }

    // display which user likes the post
    public void displayUserLiked(){
        DatabaseReference userLikes = MainActivity.database.child("likes");
        userLikes.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Like like = dataSnapshot.getValue(Like.class);
                if(like.imageId.equals(imageId)) {
                    String userName = MainActivity.lookUpUserID.lookUpUserNamebyId(like.userId);
                    likedNames.add(userName);
                }
                String names = "";
                for(String i : likedNames){
                    names = names + i + " , ";
                }
                names = names + " "+likedNames.size()+" users liked this post.";
                likeContent.setText(names);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}