package com.alecat.geosettingsopen.dialogs;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alecat.geosettingsopen.R;

public class DialogArea extends DialogFragment {

    private Long mAreaID;

    private BroadcastReceiver mDeletedArea = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("area_id")) {

                Bundle bundle = intent.getExtras();
                if(mAreaID.equals(bundle.get("area_id"))){
                    Dialog dialog = getDialog();
                    if(dialog != null){
                        getDialog().dismiss();
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_area, container,
                false);

        mAreaID = getArguments().getLong("area_id");

        DialogAreaAdapter mPagerAdapter = new DialogAreaAdapter(getChildFragmentManager(), mAreaID, getContext());

        ViewPager mViewPager = (ViewPager) dialogView.findViewById(R.id.dialog_area_pager);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) dialogView.findViewById(R.id.dialog_area_tab);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDeletedArea,
                new IntentFilter("area-deleted"));

        return dialogView;
    }
}

class DialogAreaAdapter extends FragmentPagerAdapter {

    private final int NUM_ITEMS = 3;
    private Long areaID;
    private Context mCtx;


    public DialogAreaAdapter(FragmentManager fm, Long areaID, Context mCtx) {
        super(fm);
        this.areaID = areaID;
        this.mCtx = mCtx;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment different title
                return DialogAreaFirstTab.newInstance(areaID);
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return DialogAreaSecondTab.newInstance(areaID);
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return DialogAreaThirdTab.newInstance(areaID);
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





