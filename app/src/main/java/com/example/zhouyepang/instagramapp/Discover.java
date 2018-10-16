package com.example.zhouyepang.instagramapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import java.util.*;
import 	android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;


public class Discover extends AppCompatActivity {

    DatabaseReference users;
    ArrayList<String> userName;
    List<String> names = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = new ArrayList<String>();

        setContentView(R.layout.activity_discover);
        names = new ArrayList<String>();
        users = MainActivity.database.child("users");
        users.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                names.add(user.getDisplayName().toString());
                Log.v("debug","name = " +user.getDisplayName().toString());
                display(names);
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



    public void display (List<String> names){
        RecyclerView userNamesView = (RecyclerView) findViewById(R.id.suggestedNameView);
        SuggestUserDisplay adapter = new SuggestUserDisplay(names);
        userNamesView.setAdapter(adapter);
        userNamesView.setLayoutManager(new LinearLayoutManager(this));
    }

}
