package com.alecat.geosettingsopen.activities.profiles;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.activities.BaseActivity;
import com.alecat.geosettingsopen.managers.ProfileManager;
import com.alecat.geosettingsopen.models.ProfileModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

/**
 * Created by alessandro on 23/08/15.
 */
public class ProfileActivity extends BaseActivity {


    private MyPagerAdapter adapterViewPager;

    private Long mProfileID = null;

    PublisherInterstitialAd mPublisherInterstitialAd;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_detail);

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


        ProfileModel profileModel = ProfileManager.getProfile(this, mProfileID);

        EditText profileNameText = (EditText) findViewById(R.id.profile_profilename);
        profileNameText.setText(profileModel.name);
        profileNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveName(v.getText().toString());
                }
                return false;
            }
        });
    }

    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .build();

        mPublisherInterstitialAd.loadAd(adRequest);
    }

    private void saveName(String name) {
        ProfileModel profile = ProfileManager.getProfile(this, mProfileID);
        profile.name = name;
        ProfileManager.saveProfile(this, profile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AreaFragment areaFragment = adapterViewPager.getAreaFragment();
        SoundsFragment soundFragment = adapterViewPager.getSoundFragment();

        if ((requestCode == SoundsFragment.REQUESTCODE_PICKRINGTONE || requestCode == SoundsFragment.REQUESTCODE_PICKNOTIFICATIONS) && data != null) {
            if(soundFragment != null) {
                soundFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        else if(requestCode == AreaFragment.REQUESTCODE_PLACE && data != null){
            if(areaFragment !=null){
                areaFragment.onActivityResult(requestCode, resultCode, data);
            }
        }

    }


    public void placeAutocomplete(View view){

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, AreaFragment.REQUESTCODE_PLACE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        SoundsFragment soundFragment = adapterViewPager.getSoundFragment();

        switch (requestCode) {
            case SoundsFragment.PERMISSION_REQUEST_WRITE_STORAGE_RINGTONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    soundFragment.chooseRingtone();

                }
                return;
            }
            case SoundsFragment.PERMISSION_REQUEST_WRITE_STORAGE_NOTIFICATION:{

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
        Context mContext;
        private AreaFragment mAreaFragment;
        private SoundsFragment mSoundsFragment;

        public AreaFragment getAreaFragment(){
            return mAreaFragment;
        }
        public SoundsFragment getSoundFragment(){
            return mSoundsFragment;
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


            if(mProfileID == 1){
                switch (position) {
                    case 0:
                        mSoundsFragment = SoundsFragment.newInstance(mProfileID);
                        return mSoundsFragment;
                    case 1:
                        return NetsFragment.newInstance(mProfileID);
                    case 2:
                        return DisplayFragment.newInstance(mProfileID);
                    case 3:
                        return GeneralFragment.newInstance(mProfileID);
                    default:
                        return null;
                }
            }
            else{
                switch (position) {
                    case 0:
                        mAreaFragment = AreaFragment.newInstance(mProfileID);
                        return mAreaFragment;
                    case 1:
                        mSoundsFragment = SoundsFragment.newInstance(mProfileID);
                        return mSoundsFragment;
                    case 2:
                        return NetsFragment.newInstance(mProfileID);
                    case 3:
                        return DisplayFragment.newInstance(mProfileID);
                    default:
                        return null;
                }
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            if(mProfileID == 1) {
                switch (position) {

                    case 0:
                        return mContext.getString(R.string.profile_tabs_label_sounds);
                    case 1:
                        return mContext.getString(R.string.profile_tabs_label_nets);
                    case 2:
                        return mContext.getString(R.string.profile_tabs_label_display);
                    case 3:
                        return mContext.getString(R.string.profile_tabs_label_time_activation);
                    default:
                        return null;
                }
            }
            else{
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
}






