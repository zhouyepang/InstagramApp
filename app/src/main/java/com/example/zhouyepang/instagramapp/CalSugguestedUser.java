package com.example.zhouyepang.instagramapp;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.*;


public class CalSugguestedUser {

    private  DatabaseReference userFollowing;
    private FirebaseUser currentUser;
    String currentUserID;
    private ArrayList<String> suggestedID;

    public CalSugguestedUser(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID =  currentUser.getUid();
        suggestedID = new ArrayList<String>();
        Log.v("suggest user ", currentUserID);
        System.out.println("current user id : "+ currentUserID);
        userFollowing = FirebaseDatabase.getInstance().getReference().child("following").child(currentUserID).child("followTo");
        userFollowing .addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String followingUserID = dataSnapshot.getKey();
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
                if(!suggestedID.contains(dataSnapshot.getKey())) {
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
    public ArrayList<String> getSuggestedUserList(){
        return suggestedID;
    }
}
