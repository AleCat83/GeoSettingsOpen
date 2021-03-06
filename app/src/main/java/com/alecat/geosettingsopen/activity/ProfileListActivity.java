package com.alecat.geosettingsopen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.alecat.geosettingsopen.adapter.ProfileListAdapter;
import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.models.ProfileModel;

import java.util.List;


public class ProfileListActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ProfileListAdapter mProfilesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRecyclerView = (RecyclerView) findViewById(R.id.profile_recycler_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mProfilesRecyclerAdapter = new ProfileListAdapter(this, mRecyclerView, ProfileHelper.getAllProfiles(this), AreaHelper.getAllArea(this));
        mRecyclerView.setAdapter(mProfilesRecyclerAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh(){

        List<ProfileModel> profileList = ProfileHelper.getAllProfiles(this);
        mProfilesRecyclerAdapter = new ProfileListAdapter(this, mRecyclerView, profileList, AreaHelper.getAllArea(this));
        mRecyclerView.setAdapter(mProfilesRecyclerAdapter);

    }

    public void addProfile(View view){

        ProfileModel profileModel = new ProfileModel(
                null,
                this.getResources().getString(R.string.profile_new_profile),
                true,
                0,
                false,
                0,
                false,
                0,
                0,
                false,
                0,
                0,
                0,
                0,
                0,
                false,
                "",
                false,
                "",
                false,
                0,
                0,
                false,
                false,
                0,
                false,
                0,
                false,
                0,
                false,
                0,
                false);


        Long profileID = ProfileHelper.saveProfile(this, profileModel);
        if(profileID != null){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", profileID);
            startActivity(intent);
        }
    }
}