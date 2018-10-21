package com.example.zhouyepang.instagramapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import java.util.*;
import 	android.support.v7.widget.LinearLayoutManager;

import com.fasterxml.jackson.databind.jsontype.impl.MinimalClassNameIdResolver;
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
    List <String> alreadyFollow;
    public static ArrayList <String> SuggestedUserList;
    public static ArrayList <SuggestUserScore> SuggestedUserScoreList;
    public static int minSuggestedUserScore = 2;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = new ArrayList<String>();
        setContentView(R.layout.activity_discover);
        suggestedNames = new ArrayList<String>();
        ids = new ArrayList<String>();
        users = MainActivity.database.child("users");
        alreadyFollow = new ArrayList <String>();
        searchPageButton = findViewById(R.id.searchPageButton);
        enableToolBar();
        searchPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPage(v);
            }
        });

        SuggestedUserList = new ArrayList <String>();
        SuggestedUserScoreList = new ArrayList<SuggestUserScore>();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alreadyFollow.add(currentUserID);
        noneFollowingRelationSuggestion(currentUserID);
        calFollowings(currentUserID);
    }

    public void calFollowings(String currUserID) {
        DatabaseReference followingFollows = FirebaseDatabase.getInstance().getReference().child("following").child(currUserID).child("followTo");
        followingFollows.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                alreadyFollow.add(dataSnapshot.getKey());
                calSuggested(dataSnapshot.getKey());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }

        });
    }

    public void calSuggested(String follwingUserID){
        DatabaseReference followingFollows = FirebaseDatabase.getInstance().getReference().child("following").child(follwingUserID).child("followTo");
        followingFollows.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateTheRelationshipsUser(dataSnapshot.getKey());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }

        });
    }

    public void updateTheRelationshipsUser(String userId){
        if (!checkIfUserExist(userId)){
            SuggestUserScore us = new SuggestUserScore(userId);
            SuggestedUserScoreList.add(us);
        }
        for (SuggestUserScore us: SuggestedUserScoreList){
            if(us.isSameUser(userId)){
                us.updateScore();
            }
        }
        filterSuggestUser();
    }

    public boolean checkIfUserExist(String userId){
        for (SuggestUserScore us: SuggestedUserScoreList){
            if(us.isSameUser(userId)){
                return true;
            }
        }
        return false;
    }

    public void filterSuggestUser(){
        for (SuggestUserScore us: SuggestedUserScoreList) {
            if (us.getScore() >= minSuggestedUserScore) {
                if (!SuggestedUserList.contains(us.getUserId()) && !alreadyFollow.contains(us.getUserId()) && us.getUserId()!=currentUserID) {
                    SuggestedUserList.add(us.getUserId());
                    displaySuggstedUser (suggestedNames, SuggestedUserList);
                }
            }
        }
    }

    public void noneFollowingRelationSuggestion(final String currUserId){
        DatabaseReference followingFollows = FirebaseDatabase.getInstance().getReference().child("following");
        followingFollows.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.getKey().equals(currUserId) && SuggestedUserList.size() <= 5 && !alreadyFollow.contains(dataSnapshot.getKey()) && dataSnapshot.getKey()!=currentUserID){
                    SuggestedUserList.add(dataSnapshot.getKey());
                    displaySuggstedUser (suggestedNames, SuggestedUserList);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }

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
