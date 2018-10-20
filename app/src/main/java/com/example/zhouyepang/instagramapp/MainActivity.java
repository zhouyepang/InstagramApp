package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 123;
    public static DatabaseReference database;
    public static LookUpUserID lookUpUserID;
    public boolean isLogin = false;
    public String currUserName;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = new Intent(this, MainPage.class);
        //startActivity(intent);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance().getReference();

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fbUser != null) {
            // User already signed in
            String token = FirebaseInstanceId.getInstance().getToken();

            // save the user info in the database to users/UID/
            // we'll use the UID as part of the path
            User user = new User(fbUser.getUid(), fbUser.getDisplayName(), token);

            database.child("users").child(user.uid).setValue(user);

            Intent intent = new Intent(this, MainPage.class);
            startActivity(intent);

        }
        lookUpUserID = new LookUpUserID();
        userList();
    }

    public void signIn2(View view){
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder().setTheme(R.style.FirebaseLoginTheme).build(),
                MainActivity.RC_SIGN_IN);
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                // get the Firebase user
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                // get the FCM token
                String token = FirebaseInstanceId.getInstance().getToken();

                // save the user info in the database to users/UID/
                // we'll use the UID as part of the path
                User user = new User(fbUser.getUid(), fbUser.getDisplayName(), token);
                database.child("users").child(user.uid).setValue(user);

                // go to feed activity
                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
            } else {
                // Sign in failed, check response for error code
                if (response != null) {
                    Toast.makeText(this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                // get the FCM token
                String token = FirebaseInstanceId.getInstance().getToken();
                // save the user info in the database to users/UID/
                // we'll use the UID as part of the path
                this.user = new User(fbUser.getUid(), fbUser.getDisplayName(), token);
                database.child("users").child(user.uid).setValue(user);
                Toast.makeText(this, "Authenticated as " + fbUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                currUserName = this.user.getDisplayName();
                //TextView text = findViewById(R.id.userNameDisplay);
                this.isLogin = true;

                Intent intent = new Intent(this, MainPage.class);
                startActivity(intent);
            } else {
                // Sign in failed, check response for error code
                if (response != null) {
                    Toast.makeText(this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void userList() {
        DatabaseReference userId = MainActivity.database.child("users");
        userId.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                user = dataSnapshot.getValue(User.class);
                lookUpUserID.updateUserLists(user);
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


}
