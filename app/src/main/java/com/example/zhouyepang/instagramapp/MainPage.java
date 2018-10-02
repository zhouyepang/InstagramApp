package com.example.zhouyepang.instagramapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.MissingFormatArgumentException;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public boolean isLogin = false;
    public String currUserName;
    public User user;
    RetriveData database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = new RetriveData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Using AuthUI to implement Firebase Login
    // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
    public void signIn() {
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder().setTheme(R.style.FirebaseLoginTheme).build(),
                MainActivity.RC_SIGN_IN);
    }

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
                MainActivity.database.child("users").child(user.uid).setValue(user);
                Toast.makeText(this, "Authenticated as " + fbUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                currUserName = this.user.getDisplayName();
                TextView text = findViewById(R.id.userNameDisplay);
                this.isLogin = true;
            } else {
                // Sign in failed, check response for error code
                if (response != null) {
                    Toast.makeText(this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.following) {
            // Handle the camera action
        } else if (id == R.id.follower) {

        } else if (id == R.id.bluetooth) {

        } else if (id == R.id.photoEditor) {

        } else if (id == R.id.login) {
            signIn();
        } else if (id == R.id.logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void UserFeed(View view) {
        Intent mainPageIntent = new Intent(this, MainPage.class);
        startActivity(mainPageIntent);
    }
    public void discover(View view) {
        Intent searchIntent = new Intent(this, Discover.class);
        startActivity(searchIntent);
    }
    public void uploadImage(View view) {
        Intent uploadIntent = new Intent(this, UploadSelect.class);
        startActivity(uploadIntent);
    }
    public void ActivityFeed(View view) {

    }

    public void Profile(View view) {
        Intent uploadIntent = new Intent(this, PhotoEditor.class);
        startActivity(uploadIntent);
        //Intent searchIntent = new Intent(this, Profile.class);
        //startActivity(searchIntent);
    }
}
