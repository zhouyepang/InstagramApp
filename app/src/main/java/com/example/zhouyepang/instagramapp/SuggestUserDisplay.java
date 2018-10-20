package com.example.zhouyepang.instagramapp;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.*;


public class SuggestUserDisplay extends RecyclerView.Adapter<SuggestUserDisplay.ViewHolder>  {

        public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameText;
        public Button addFriend;





        public ViewHolder(View v) {
            super(v);
            userNameText = v.findViewById(R.id.user_name);
            addFriend = v.findViewById(R.id.add_button);
            }
        }


        private List<String> userInfo;
        private List<String> ids;
        private FirebaseUser currentUser;
        private DatabaseReference following;
        DatabaseReference loginedUser;
        private DatabaseReference following2;
        private Context mContext;


        // Pass in the contact array into the constructor
        public SuggestUserDisplay(List<String> userInfo, List<String> ids) {
            this.userInfo = userInfo;
            this.ids = ids;
            //mContext = context;

            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        public SuggestUserDisplay.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.suggest_user_card, parent, false);


            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }



        public void onBindViewHolder(SuggestUserDisplay.ViewHolder viewHolder, final int position) {
            String currUserName = userInfo.get(position);
            final String currID = ids.get(position);
            TextView usernameView = viewHolder.userNameText;
            usernameView.setText(currUserName);
            final Button addButton = viewHolder.addFriend;



            loginedUser = FirebaseDatabase.getInstance().getReference().child("following").child(currentUser.getUid());

            /*loginedUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()) {

                        if(dataSnapshot.child("followTo").hasChild(currID)) {
                            addButton.setVisibility(View.VISIBLE);
                            addButton.setText("ADDED");
                        } else if (currID.equals(currentUser.getUid().toString())) {
                            addButton.setVisibility(View.INVISIBLE);
                        } else {

                            addButton.setVisibility(View.VISIBLE);
                            addButton.setText("ADD");
                        }


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }); */

            if(currID.equals(currentUser.getUid().toString())){
                addButton.setVisibility(View.INVISIBLE);
            } else if(addButton.getText().toString().isEmpty()) {

                addButton.setVisibility(View.VISIBLE);
                addButton.setText("ADD");

            } else {
                addButton.setVisibility(View.VISIBLE);
            }

                // 可能造成运行速度变慢的原因
                loginedUser.addChildEventListener(new ChildEventListener() {


                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                        if (dataSnapshot.hasChild(currID)) {

                            //addButton.setText("ADDED");
                            addButton.setVisibility(View.VISIBLE);
                            addButton.setText("FOLLOWED");

                        } else if (currID.equals(currentUser.getUid().toString())) {
                            addButton.setVisibility(View.INVISIBLE);
                        } else {
                            //addButton.setText("ADD");
                            addButton.setVisibility(View.VISIBLE);
                            addButton.setText("ADD");
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


            /*if(currID == currentUser.getUid()) {
                addButton.setText("ME");
            } else if(dataSnapshot.child("followTo").getKey() == currID) {

                addButton.setText("ADDED");

            } else {
                addButton.setText("ADD");
            } */
            //addButton.setText("ADD");

            viewHolder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followAuser(currentUser.getUid(),currID);
                    //following = FirebaseDatabase.getInstance().getReference().child("following").child(currentUser.getUid());
                    //following.setValue(currID);

                    //Toast.makeText(mContext, userInfo.get(position), Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(mContext, Profile.class);
                    //mContext.startActivity(intent);
                    addButton.setText("FOLLOWED");
                }
            });

        }

        private void followAuser(String uid, String currID) {

            following = FirebaseDatabase.getInstance().getReference().child("following").child(uid);
            following.child("followTo").child(currID).setValue(currID);
            following2 = FirebaseDatabase.getInstance().getReference().child("follower").child(currID);
            following2.child("whoFollowsMe").child(uid).setValue(uid);




        }



        public int getItemCount() {
            return userInfo.size();
        }






}
