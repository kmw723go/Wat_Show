package com.team.project.wat_show.main_activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.team.project.wat_show.MainActivity;

import java.util.ArrayList;

public class main_viewPager_Adapter extends FragmentPagerAdapter {

    // 이 어댑터는 프래그먼트들을 가지고 있다.
    public ArrayList<Fragment> mData;

    public main_viewPager_Adapter(FragmentManager fm) {
        super(fm);

        mData = new ArrayList<>();
        mData.add(new Menu_liveList());
        mData.add(new Menu_videoContent());
        mData.add(new Menu_myList());
    }

    @Override
    public Fragment getItem(int position) {
        // 포지션에 맞는 프레그먼트 리턴
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
