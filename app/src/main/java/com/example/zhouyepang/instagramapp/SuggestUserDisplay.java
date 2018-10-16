package com.example.zhouyepang.instagramapp;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
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

        // Pass in the contact array into the constructor
        public SuggestUserDisplay(List<String> userInfo) {
            this.userInfo = userInfo;
        }

        public SuggestUserDisplay.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.suggest_user_card, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        public void onBindViewHolder(SuggestUserDisplay.ViewHolder viewHolder, int position) {
            String currUserName = userInfo.get(position);
            TextView usernameView = viewHolder.userNameText;
            usernameView.setText(currUserName);
            Button addButton = viewHolder.addFriend;
            addButton.setText("ADD");
        }

        public int getItemCount() {
            return userInfo.size();
        }


}
