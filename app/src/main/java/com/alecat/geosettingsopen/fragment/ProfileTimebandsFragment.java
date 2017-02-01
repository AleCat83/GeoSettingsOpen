package com.alecat.geosettingsopen.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.manager.ProfileHelper;
import com.alecat.geosettingsopen.model.ProfileModel;

public class ProfileTimebandsFragment extends Fragment {

    private static final Long mAreaDefaultID = 1L;
    private Long mProfileID;

    public static ProfileTimebandsFragment newInstance(Long profileId) {
        ProfileTimebandsFragment profileTimebandsFragment = new ProfileTimebandsFragment();
        Bundle args = new Bundle();
        args.putLong("ProfileId", profileId);
        profileTimebandsFragment.setArguments(args);
        return profileTimebandsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mProfileID = getArguments().getLong("ProfileId");

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        View view = inflater.inflate(R.layout.fragment_profile_timebands, container, false);

        /*SwitchCompat profileActive = (SwitchCompat) view.findViewById(R.id.profile_active);

        profileActive.setChecked(profile.active);


        profileActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveActive(isChecked);

            }
        });*/

        TimeBandListFragment timebandList = new TimeBandListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("area_id", mAreaDefaultID);
        timebandList.setArguments(bundle);

        android.support.v4.app.FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.time_band_container, timebandList, "timeband_list");
        ft.commit();

        return view;
    }

    private void saveActive(Boolean state) {

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);
        profile.active = state;
        ProfileHelper.saveProfile(getContext(),profile);

    }

}









