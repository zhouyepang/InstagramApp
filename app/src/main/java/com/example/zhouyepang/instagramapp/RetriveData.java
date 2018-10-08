package com.example.zhouyepang.instagramapp;
import android.util.Log;

import com.google.firebase.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

public class RetriveData {
    DatabaseReference users;
    ArrayList<String> userName;

    RetriveData(){
        userName = new ArrayList<String>();
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                userName.add(data.getValue(User.class).getDisplayName());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public boolean searchUserNameList(String name){
        users = FirebaseDatabase.getInstance().getReference("users");
        users.addListenerForSingleValueEvent(valueEventListener);
        for(String i : userName){
            if (i.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
