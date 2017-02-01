package com.alecat.geosettingsopen.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.activity.ProfileActivity;
import com.alecat.geosettingsopen.manager.AreaHelper;
import com.alecat.geosettingsopen.manager.ProfileHelper;
import com.alecat.geosettingsopen.model.AreaModel;
import com.alecat.geosettingsopen.model.ProfileModel;

import java.util.List;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ViewHolder> {

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

    public ProfileListAdapter(Context cxt, RecyclerView recyclerView, List<ProfileModel> profilesList) {
        mProfilesList = profilesList;
        mContext = cxt;
        mRecyclerView = recyclerView;
    }

    @Override
    public ProfileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        final ProfileModel profile = mProfilesList.get(position);
        TextView label = (TextView) holder.view.findViewById(R.id.profileItemName);
        label.setText(profile.name);


        TextView statusLabel = (TextView) holder.view.findViewById(R.id.profileItemStatus);

        List<AreaModel> profileAreas = AreaHelper.getAreasByProfile(mContext, profile.id);


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
            if(ProfileHelper.isProfileActive(mContext, profile.id)){
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
                ProfileHelper.ActivateProfile(mContext, profile.id, true);
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
                        ProfileHelper.deleteProfile(mContext, profileId);
                        List<ProfileModel> profileList = ProfileHelper.getAllProfiles(mContext);
                        ProfileListAdapter profileListAdapter = new ProfileListAdapter(mContext, mRecyclerView, profileList);
                        mRecyclerView.setAdapter(profileListAdapter);
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