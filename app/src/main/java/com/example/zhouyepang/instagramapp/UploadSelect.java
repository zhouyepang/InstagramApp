package com.example.zhouyepang.instagramapp;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class UploadSelect extends AppCompatActivity  {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private UploadPhotoPagerAdapter uploadPhotoPagerAdapter;

    private TabLayout.Tab gallery;
    private TabLayout.Tab camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.upload_select);

        initViews();
        contextOfApplication = getApplicationContext();
    }
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
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


    }




}
