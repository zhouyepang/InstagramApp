package com.example.zhouyepang.instagramapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import java.util.*;
import 	android.support.v7.widget.LinearLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Discover extends AppCompatActivity {

    DatabaseReference users;
    ArrayList<String> userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = new ArrayList<String>();
        users = FirebaseDatabase.getInstance().getReference("users");
        users.addListenerForSingleValueEvent(valueEventListener);
        setContentView(R.layout.activity_discover);
    }

    public void display (ArrayList <String> names){
        RecyclerView userNamesView = (RecyclerView) findViewById(R.id.suggestedNameView);
        SuggestUserDisplay adapter = new SuggestUserDisplay(names);
        userNamesView.setAdapter(adapter);
        userNamesView.setLayoutManager(new LinearLayoutManager(this));
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                System.out.println(data.getValue(User.class).getDisplayName());
                userName.add(data.getValue(User.class).getDisplayName());
                System.out.println("list size, "+userName.size());
            }
            for (String i : userName){
                System.out.println("name: "+i);
            }
            display (userName);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
