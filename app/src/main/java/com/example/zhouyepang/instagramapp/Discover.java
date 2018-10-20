package com.example.zhouyepang.instagramapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import java.util.*;
import 	android.support.v7.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;

public class Discover extends AppCompatActivity {

    DatabaseReference users;
    ArrayList<String> userName;
    List<String> suggestedNames = new ArrayList<String>();
    ImageButton searchPageButton;
    List<String> ids = new ArrayList<String>();
    private  DatabaseReference userFollowing;
    private FirebaseUser currentUser;
    String currentUserID;
    List <String> alreadyFollow;
    private ArrayList<String> suggestedID;
    //SuggestUserDisplay adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = new ArrayList<String>();
        setContentView(R.layout.activity_discover);
        suggestedNames = new ArrayList<String>();
        ids = new ArrayList<String>();
        users = MainActivity.database.child("users");
        CalSugguestedUser calSugguestedUser = new CalSugguestedUser();
        alreadyFollow = new ArrayList <String>();
        searchPageButton = findViewById(R.id.searchPageButton);
        enableToolBar();
        CalSugguestedUser();
        searchPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPage(v);
            }
        });
        users.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                suggestedNames.add(user.getDisplayName().toString());
                ids.add(user.getUid());
                //adapter2 = new SuggestUserDisplay(suggestedNames);

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void CalSugguestedUser(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID =  currentUser.getUid();
        suggestedID = new ArrayList<String>();
        Log.v("suggest user ", currentUserID);
        System.out.println("current user id : "+ currentUserID);
        userFollowing = FirebaseDatabase.getInstance().getReference().child("following").child(currentUserID).child("followTo");
        userFollowing .addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String followingUserID = dataSnapshot.getKey();
                alreadyFollow.add(followingUserID);
                displaySuggstedUser(suggestedNames, ids);
                lookUpFollowingUserFollows(followingUserID);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void lookUpFollowingUserFollows(String userID){
        userFollowing = FirebaseDatabase.getInstance().getReference().child("following").child(currentUserID).child("followTo");
        userFollowing .addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!alreadyFollow.contains(dataSnapshot.getKey())) {
                    System.out.println("Following follows to : " + dataSnapshot.getKey());
                    suggestedID.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void enableToolBar(){
        View includeView = (View) findViewById(R.id.parent_Layout);
        ImageButton button1 = (ImageButton)includeView.findViewById(R.id.UserFeed);
        ImageButton button2 = (ImageButton)includeView.findViewById(R.id.uploadImage);
        ImageButton button3 = (ImageButton)includeView.findViewById(R.id.Search);
        ImageButton button4 = (ImageButton)includeView.findViewById(R.id.profileDisplay);
        ImageButton button5 = (ImageButton)includeView.findViewById(R.id.bluetoothButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFeed(v);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(v);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discover(v);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile(v);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth(v);
            }
        });

    }
    public void UserFeed(View view) {
        Intent mainPageIntent = new Intent(this, MainPage.class);
        startActivity(mainPageIntent);
    }

    public void uploadImage(View view) {
        Intent uploadIntent = new Intent(this, UploadSelect.class);
        startActivity(uploadIntent);
    }

    public void Profile(View view) {
        Intent searchIntent = new Intent(this, Profile.class);
        startActivity(searchIntent);
    }
    public void discover(View view) {
        Intent searchIntent = new Intent(this, Discover.class);
        startActivity(searchIntent);
    }
    public void bluetooth(View view) {
        Intent bluetoothIntent = new Intent(this, Bluetooth.class);
        startActivity(bluetoothIntent);
    }

    public void displaySuggstedUser (List<String> names, List<String> uIDs){
        RecyclerView userNamesView = (RecyclerView) findViewById(R.id.suggestedNameView);
        SuggestUserDisplay adapter = new SuggestUserDisplay(names, uIDs);
        userNamesView.setAdapter(adapter);
        userNamesView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void openSearchPage(View view){
        Intent searchPage = new Intent(this,  SearchUser.class);
        startActivity(searchPage);
    }
}
