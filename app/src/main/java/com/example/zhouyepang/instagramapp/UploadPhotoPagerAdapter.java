package com.example.zhouyepang.instagramapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UploadPhotoPagerAdapter extends FragmentPagerAdapter {
    private String[] fgTitles = new String[]{"Gallery", "Camera"};

    public UploadPhotoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new TakePhoto();
        }
        return new Gallery();
    }

    @Override
    public int getCount() {
        return fgTitles.length;
    }

    //Bind ViewPager and TabLayout, to have tab text with position
    @Override
    public CharSequence getPageTitle(int position) {
        return fgTitles[position];
    }
}
