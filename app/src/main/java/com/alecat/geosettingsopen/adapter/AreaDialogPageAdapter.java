package com.alecat.geosettingsopen.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.fragment.DialogAreaGeneralFragment;
import com.alecat.geosettingsopen.fragment.DialogAreaTimebandsFragment;
import com.alecat.geosettingsopen.fragment.DialogAreaTrainingFragment;

public class AreaDialogPageAdapter extends FragmentPagerAdapter {

    private final int NUM_ITEMS = 3;
    private Long areaID;
    private Context mCtx;


    public AreaDialogPageAdapter(FragmentManager fm, Long areaID, Context mCtx) {
        super(fm);
        this.areaID = areaID;
        this.mCtx = mCtx;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment different title
                return DialogAreaGeneralFragment.newInstance(areaID);
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return DialogAreaTimebandsFragment.newInstance(areaID);
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return DialogAreaTrainingFragment.newInstance(areaID);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return mCtx.getString(R.string.area_dialog_first_tab);
            case 1:
                return mCtx.getString(R.string.area_dialog_second_tab);
            case 2:
                return mCtx.getString(R.string.area_dialog_third_tab);
            default:
                return null;
        }
    }
}