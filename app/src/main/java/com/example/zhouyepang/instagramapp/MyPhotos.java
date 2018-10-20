package com.example.zhouyepang.instagramapp;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPhotos extends AppCompatActivity {



    public User user;

    private FirebaseAuth myAuth;
    private String currentUserID;

    // Jason
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    myPhotosAdapter mAdapter;
    ArrayList<Image> myImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photos);

        myAuth = FirebaseAuth.getInstance();





        // Setup the RecyclerView
        recyclerView = findViewById(R.id.myRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new myPhotosAdapter(myImages, MyPhotos.this);
        recyclerView.setAdapter(mAdapter);

        // get Current User ID
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Log.d("MyApp","I am here");

        // Get the latest 100 images
        Query imagesQuery = MainActivity.database.child("images").child("postImages").orderByKey().limitToFirst(100);
        imagesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // A new image has been added, add it to the displayed list
                final Image image = dataSnapshot.getValue(Image.class);

                // get the image user
                MainActivity.database.child("users/" + image.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        image.user = user;
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(image.userId.equals(currentUserID)) {

                    mAdapter.addImage(image);

                }

                Log.d("haha", ""+ image.key);
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

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photos);
    } */
}
