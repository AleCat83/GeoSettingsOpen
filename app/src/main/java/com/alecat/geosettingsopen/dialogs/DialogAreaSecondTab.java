package com.alecat.geosettingsopen.dialogs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.managers.AreaManager;

public class DialogAreaSecondTab extends Fragment {

    private Long mAreaID;

    public static DialogAreaSecondTab newInstance(Long areaID) {
        DialogAreaSecondTab fragment = new DialogAreaSecondTab();
        Bundle args = new Bundle();
        args.putLong("area_id", areaID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAreaID = getArguments().getLong("area_id");
        View view = inflater.inflate(R.layout.dialog_area_second_tab, container, false);
        initItems(view);

        return view;
    }

    private void initItems(View view){

        TimeBandListFragment timebandList = new TimeBandListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("area_id", mAreaID);
        timebandList.setArguments(bundle);

        android.support.v4.app.FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.time_band_container, timebandList, "timeband_list");


        AreaManager.activableBytime(getContext(), mAreaID);
        ft.commit();
    }
}
