package com.example.zhouyepang.instagramapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*public class myPhotosAdapter {
} */

public class myPhotosAdapter extends RecyclerView.Adapter<myPhotosAdapter.ViewHolder> {
    private ArrayList<Image> mDataset;
    private MyPhotos mActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView mTextView;
        public ImageView mImageView;
        //public Button mLikeButton;

        public ViewHolder(View v) {
            super(v);
            //mTextView = v.findViewById(R.id.textView2);
            mImageView = v.findViewById(R.id.imageView);
            //mLikeButton = v.findViewById(R.id.likeButton);
        }
    }

    public myPhotosAdapter(ArrayList<Image> myDataset, MyPhotos activity) {
        mDataset = myDataset;
        mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public myPhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_image_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Image image = (Image) mDataset.get(position);
        //if (image.user != null) {
          //  holder.mTextView.setText(image.user.displayName);
        //}
        Picasso.get().load(image.downloadUrl).into(holder.mImageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addImage(Image image) {
        mDataset.add(0, image);
        notifyDataSetChanged();
    }

}

