package com.alecat.geosettingsopen.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.fragment.ProfileAreasFragment;
import com.alecat.geosettingsopen.fragment.ProfileDisplayFragment;
import com.alecat.geosettingsopen.fragment.ProfileNetsFragment;
import com.alecat.geosettingsopen.fragment.ProfileSoundsFragment;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.models.ProfileModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

public class ProfileActivity extends BaseActivity {


    private MyPagerAdapter adapterViewPager;

    private Long mProfileID = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(intent.hasExtra("id")){
            mProfileID = extras.getLong("id");
        }

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);

        vpPager.setOffscreenPageLimit(3);

        adapterViewPager = new MyPagerAdapter(this, getSupportFragmentManager(), mProfileID);
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(vpPager);

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        ProfileModel profileModel = ProfileHelper.getProfile(this, mProfileID);

        EditText profileNameText = (EditText) findViewById(R.id.profile_profilename);
        profileNameText.setText(profileModel.name);

        profileNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                saveName(s.toString());
            }
        });

    }


    private void saveName(String name) {
        ProfileModel profile = ProfileHelper.getProfile(this, mProfileID);
        profile.name = name;
        ProfileHelper.saveProfile(this, profile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ProfileAreasFragment profileAreasFragment = adapterViewPager.getAreaFragment();
        ProfileSoundsFragment soundFragment = adapterViewPager.getSoundFragment();

        if ((requestCode == ProfileSoundsFragment.REQUESTCODE_PICKRINGTONE || requestCode == ProfileSoundsFragment.REQUESTCODE_PICKNOTIFICATIONS) && data != null) {
            if(soundFragment != null) {
                soundFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        else if(requestCode == ProfileAreasFragment.REQUESTCODE_PLACE && data != null){
            if(profileAreasFragment !=null){
                profileAreasFragment.onActivityResult(requestCode, resultCode, data);
            }
        }

    }


    public void placeAutocomplete(View view){

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, ProfileAreasFragment.REQUESTCODE_PLACE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        ProfileSoundsFragment soundFragment = adapterViewPager.getSoundFragment();

        if(soundFragment == null){
            return;
        }

        switch (requestCode) {
            case ProfileSoundsFragment.PERMISSION_REQUEST_WRITE_STORAGE_RINGTONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    soundFragment.chooseRingtone();

                }
                return;
            }
            case ProfileSoundsFragment.PERMISSION_REQUEST_WRITE_STORAGE_NOTIFICATION:{

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    soundFragment.chooseNotificationSound();

                }
            }
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final int NUM_ITEMS = 4;
        private final Long mProfileID;
        private Context mContext;
        private ProfileAreasFragment mProfileAreasFragment;
        private ProfileSoundsFragment mProfileSoundsFragment;

        public ProfileAreasFragment getAreaFragment(){
            return mProfileAreasFragment;
        }
        public ProfileSoundsFragment getSoundFragment(){
            return mProfileSoundsFragment;
        }

        public MyPagerAdapter(Context context, FragmentManager fragmentManager, Long profile) {

            super(fragmentManager);
            this.mContext = context;
            this.mProfileID = profile;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    mProfileAreasFragment = ProfileAreasFragment.newInstance(mProfileID);
                    return mProfileAreasFragment;
                case 1:
                    mProfileSoundsFragment = ProfileSoundsFragment.newInstance(mProfileID);
                    return mProfileSoundsFragment;
                case 2:
                    return ProfileNetsFragment.newInstance(mProfileID);
                case 3:
                    return ProfileDisplayFragment.newInstance(mProfileID);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {

                case 0:
                    return mContext.getString(R.string.profile_tabs_label_area_activation);
                case 1:
                    return mContext.getString(R.string.profile_tabs_label_sounds);

                case 2:
                    return mContext.getString(R.string.profile_tabs_label_nets);

                case 3:
                    return mContext.getString(R.string.profile_tabs_label_display);
                default:
                    return null;
            }
        }
    }
}






