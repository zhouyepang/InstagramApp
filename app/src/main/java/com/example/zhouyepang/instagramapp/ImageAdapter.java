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
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.Context;
import android.widget.ImageButton;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<Image> mDataset;
    private MainPage mActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public Button mLikeButton;
        public ImageButton mComment;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.textView2);
            mImageView = v.findViewById(R.id.imageView);
            mLikeButton = v.findViewById(R.id.likeButton);
            mComment= v.findViewById(R.id.comment);
        }
    }


    public ImageAdapter(ArrayList<Image> myDataset, MainPage activity) {
        mDataset = myDataset;
        mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Image image = (Image) mDataset.get(position);
        if (image.user != null) {
            holder.mTextView.setText(image.user.displayName);
        }
        Picasso.get().load(image.downloadUrl).into(holder.mImageView);

        holder.mLikeButton.setText("Like (" + image.likes + ")");
        if(image.hasLiked) {
            holder.mLikeButton.setBackgroundColor(mActivity.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mLikeButton.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
        }
        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.setLiked(image);
            }
        });
        holder.mComment.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                openSinglePost(v, image);
            }
        });
        holder.mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                openSinglePost(v, image);
            }
        });
    }
    public void openSinglePost(View view, Image image){
        Context context = view.getContext();
        Intent postPage = new Intent(context, DisplayPostDetail.class);
        postPage.putExtra("imageId",  image.key);
        context.startActivity(postPage);
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
