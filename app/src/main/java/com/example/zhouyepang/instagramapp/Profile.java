package com.example.zhouyepang.instagramapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Profile extends AppCompatActivity {

    private TextView following, followers, posts;
    private FirebaseUser currentUser;
    //private DatabaseReference following;
    DatabaseReference loginedUser;
    DatabaseReference loginedUser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        enableToolBar();

        following = (TextView) findViewById(R.id.following);
        followers = (TextView) findViewById(R.id.followers);
        posts = (TextView) findViewById(R.id.posts);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        loginedUser = FirebaseDatabase.getInstance().getReference().child("following").child(currentUser.getUid());
        loginedUser2 = FirebaseDatabase.getInstance().getReference().child("follower").child(currentUser.getUid());

        loginedUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    Long a = dataSnapshot.child("followTo").getChildrenCount();
                    following.setText("Following:"+ a);

                    //Long b = dataSnapshot.child("whoFollowsMe").getChildrenCount();
                    //followers.setText("Follower:"+ b);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loginedUser2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    //Long a = dataSnapshot.child("followTo").getChildrenCount();
                    //following.setText("Following:"+ a);

                    Long b = dataSnapshot.child("whoFollowsMe").getChildrenCount();
                    followers.setText("Follower:"+ b);

                }
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

    public void editProfile(View view) {
        Intent editIntent = new Intent(this, SetupActivity.class);
        startActivity(editIntent);
    }

    public void myPhotos(View view) {
        Intent myPhotosIntent = new Intent(this, MyPhotos.class);
        startActivity(myPhotosIntent);
    }
}
