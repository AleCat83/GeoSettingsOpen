package com.alecat.geosettingsopen.activities.profiles;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.dialogs.TimeBandListFragment;
import com.alecat.geosettingsopen.managers.ProfileManager;
import com.alecat.geosettingsopen.models.ProfileModel;

public class GeneralFragment extends Fragment {

    private static final Long mAreaDefaultID = 1L;
    private Long mProfileID;

    public static GeneralFragment newInstance(Long profileId) {
        GeneralFragment generalFragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putLong("ProfileId", profileId);
        generalFragment.setArguments(args);
        return generalFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mProfileID = getArguments().getLong("ProfileId");

        ProfileModel profile = ProfileManager.getProfile(getContext(),mProfileID);

        View view = inflater.inflate(R.layout.profile_activation_time_fragment, container, false);

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

        ProfileModel profile = ProfileManager.getProfile(getContext(),mProfileID);
        profile.active = state;
        ProfileManager.saveProfile(getContext(),profile);

    }

}









