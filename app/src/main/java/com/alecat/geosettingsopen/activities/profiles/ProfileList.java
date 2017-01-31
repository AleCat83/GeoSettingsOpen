package com.alecat.geosettingsopen.activities.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.activities.BaseActivity;
import com.alecat.geosettingsopen.managers.AreaManager;
import com.alecat.geosettingsopen.managers.ProfileManager;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.models.ProfileModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;


public class ProfileList extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ProfilesRecyclerAdapter mProfilesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<ProfileModel> profileList = ProfileManager.getAllProfiles(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.profile_recycler_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mProfilesRecyclerAdapter = new ProfilesRecyclerAdapter(this, mRecyclerView, profileList);
        mRecyclerView.setAdapter(mProfilesRecyclerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh(){

        List<ProfileModel> profileList = ProfileManager.getAllProfiles(this);
        mProfilesRecyclerAdapter = new ProfilesRecyclerAdapter(this, mRecyclerView, profileList);
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


        Long profileID = ProfileManager.saveProfile(this, profileModel);
        if(profileID != null){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", profileID);
            startActivity(intent);
        }
    }
}

class ProfilesRecyclerAdapter extends RecyclerView.Adapter<ProfilesRecyclerAdapter.ViewHolder> {

    private List<ProfileModel> mProfilesList;
    private Context mContext;
    private RecyclerView mRecyclerView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ProfilesRecyclerAdapter(Context cxt, RecyclerView recyclerView, List<ProfileModel> profilesList) {
        mProfilesList = profilesList;
        mContext = cxt;
        mRecyclerView = recyclerView;
    }

    @Override
    public ProfilesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profilelist_singleitem, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        final ProfileModel profile = mProfilesList.get(position);
        TextView label = (TextView) holder.view.findViewById(R.id.profileItemName);
        label.setText(profile.name);


        TextView statusLabel = (TextView) holder.view.findViewById(R.id.profileItemStatus);

        List<AreaModel> profileAreas = AreaManager.getAreasByProfile(mContext, profile.id);


        if(!profile.active){
            statusLabel.setText(mContext.getResources().getString(R.string.profile_tips_profile_off));
        }
        else if(profileAreas.size() == 0 && profile.id != 1){
            statusLabel.setText(mContext.getResources().getString(R.string.profile_tips_no_areas));
        }
        else if(!profile.willDoSomething()){
            statusLabel.setText(mContext.getResources().getString(R.string.profile_tips_profile_do_nothing));
        }
        else{
            if(ProfileManager.isProfileActive(mContext, profile.id)){
                statusLabel.setText(mContext.getResources().getString(R.string.profile_profile_active));
            }
            else{
                statusLabel.setText(mContext.getResources().getString(R.string.profile_tips_profile_activabile));
            }
        }

        RelativeLayout container = (RelativeLayout) holder.view.findViewById(R.id.profile_container);

        container.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("id", profile.id);
                mContext.startActivity(intent);
            }
        });

        container.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ProfileManager.ActivateProfile(mContext, profile.id, true);
                return true;
            }
        });

        ImageButton deleteButton = (ImageButton) holder.view.findViewById(R.id.delete_button);
        LinearLayout deleteButtonArea = (LinearLayout) holder.view.findViewById(R.id.delete_area);

        View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile(profile.id);
            }
        };

        if(profile.id != 1){
            deleteButtonArea.setOnClickListener(deleteListener);
            deleteButton.setOnClickListener(deleteListener);

        }
        else{
            ((ViewGroup) deleteButton.getParent()).removeView(deleteButton);
        }

    }

    @Override
    public int getItemCount() {
        return mProfilesList.size();
    }


    private void deleteProfile(final Long profileId){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder
                .setTitle(mContext.getResources().getString(R.string.general_are_you_sure)+"?")
                .setMessage(mContext.getResources().getString(R.string.profile_delete_profile_message))
                .setPositiveButton(mContext.getResources().getString(R.string.general_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ProfileManager.deleteProfile(mContext, profileId);
                        List<ProfileModel> profileList = ProfileManager.getAllProfiles(mContext);
                        ProfilesRecyclerAdapter profilesRecyclerAdapter = new ProfilesRecyclerAdapter(mContext, mRecyclerView, profileList);
                        mRecyclerView.setAdapter(profilesRecyclerAdapter);
                    }
                })
                .setNegativeButton(mContext.getResources().getString(R.string.general_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}