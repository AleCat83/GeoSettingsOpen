package com.alecat.geosettingsopen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.helper.AreaHelper;

public class DialogAreaTimebandsFragment extends Fragment {

    private Long mAreaID;

    public static DialogAreaTimebandsFragment newInstance(Long areaID) {
        DialogAreaTimebandsFragment fragment = new DialogAreaTimebandsFragment();
        Bundle args = new Bundle();
        args.putLong("area_id", areaID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAreaID = getArguments().getLong("area_id");
        View view = inflater.inflate(R.layout.fragment_area_dialog_timebands, container, false);
        initItems(view);

        return view;
    }

    private void initItems(View view){

        TimebandListFragment timebandList = new TimebandListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("area_id", mAreaID);
        timebandList.setArguments(bundle);

        android.support.v4.app.FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.time_band_container, timebandList, "timeband_list");


        AreaHelper.activableBytime(getContext(), mAreaID);
        ft.commit();
    }
}
