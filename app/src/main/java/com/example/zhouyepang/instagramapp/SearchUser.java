package com.example.zhouyepang.instagramapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import java.util.*;
import 	android.support.v7.widget.LinearLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ChildEventListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

public class SearchUser extends AppCompatActivity {

    EditText searchName;
    String searchedName;
    ArrayList<String> searchedNames;
    DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        searchedNames = new ArrayList<String>();
        searchName = findViewById(R.id.searchIput);
        searchedName = searchName.getText().toString();
        users = MainActivity.database.child("users");
    }

    public void searchUser(View view){
        users.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getDisplayName().equals(searchedName) && searchedNames.size() == 0) {
                    searchedNames.add(user.getDisplayName().toString());
                }
                if (searchedNames.size()>0) {
                    displaySearchedUser(searchedNames);
                    Toast.makeText(getApplicationContext(),"User Found!",Toast.LENGTH_LONG).show();
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
    public void findUser(View view){
        searchedNames.clear();
        searchName = findViewById(R.id.searchIput);
        searchedName = searchName.getText().toString();
        searchUser(view);
    }
    public void displaySearchedUser(List<String> names){
        RecyclerView userNamesView = (RecyclerView) findViewById(R.id.searchResultView);
        SuggestUserDisplay adapter = new SuggestUserDisplay(names);
        userNamesView.setAdapter(adapter);
        userNamesView.setLayoutManager(new LinearLayoutManager(this));
    }
}
