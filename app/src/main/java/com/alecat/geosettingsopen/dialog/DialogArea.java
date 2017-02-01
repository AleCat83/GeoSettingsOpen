package com.alecat.geosettingsopen.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.adapter.AreaDialogPageAdapter;

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

        AreaDialogPageAdapter mPagerAdapter = new AreaDialogPageAdapter(getChildFragmentManager(), mAreaID, getContext());

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