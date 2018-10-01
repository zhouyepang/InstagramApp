package com.example.zhouyepang.instagramapp;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class UploadSelect extends AppCompatActivity  {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private UploadPhotoPagerAdapter uploadPhotoPagerAdapter;
    private Button btnSelect, btnNext;
    private ImageView photoPreview;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private File output;
    private Uri imageUri;
    private TabLayout.Tab gallery;
    private TabLayout.Tab camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.upload_select);

        initViews();
    }

    private void initViews() {

        //bind ViewPagerAdapter with Fragment
        mViewPager= (ViewPager) findViewById(R.id.viewPager);
        uploadPhotoPagerAdapter = new UploadPhotoPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(uploadPhotoPagerAdapter);

        //bind tab layout with pager adapter
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //tab position
        gallery = mTabLayout.getTabAt(0);
        camera = mTabLayout.getTabAt(1);

        //initial image view
        photoPreview = (ImageView) findViewById(R.id.photoPreview);
        //bind button
        btnSelect = (Button) findViewById(R.id.btn_click_select);
        btnNext = (Button) findViewById(R.id.btn_click_next);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_click_select:
                choosePhoto();
                break;
            case R.id.btn_click_next:

                break;
            default:
                break;
        }
    }

    void choosePhoto(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_CODE_PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        photoPreview.setImageBitmap(bit);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("tag",e.getMessage());
                        Toast.makeText(this,"Crash",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.i("Fail:", "Fail");
                }
                break;
            default:
                break;
        }
    }
}
