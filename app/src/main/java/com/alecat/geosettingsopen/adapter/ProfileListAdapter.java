package com.alecat.geosettingsopen.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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
import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.models.ProfileModel;

import java.util.List;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ViewHolder> {

    private List<ProfileModel> mProfilesList;
    private List<AreaModel> mAreaList;
    private Context mContext;
    private RecyclerView mRecyclerView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ProfileListAdapter(Context cxt, RecyclerView recyclerView, List<ProfileModel> profilesList, List<AreaModel> areaList) {
        mProfilesList = profilesList;
        mContext = cxt;
        mRecyclerView = recyclerView;
        mAreaList = areaList;
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
        TextView profileTitleTextView = (TextView) holder.view.findViewById(R.id.profileItemName);

        String profileTitle = profile.name;

        if(ProfileHelper.isProfileActive(mContext, profile.id)){
            profileTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.ColorAccent));
        }


        profileTitleTextView.setText(profileTitle);


        /*TextView statusLabel = (TextView) holder.view.findViewById(R.id.profileItemStatus);

        List<AreaModel> profileAreas = AreaHelper.getAreasByProfile(mContext, profile.id);


        if(!profile.active){
            statusLabel.setText(mContext.getResources().getString(R.string.profile_tips_profile_off));
        }
        else if(profileAreas.size() == 0){
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

        statusLabel.setText(getAreaInfo(profile.id), holder.view.findViewById(R.id.profileItemStatusFirst));*/


        TextView itemStatusTextView = (TextView) holder.view.findViewById(R.id.profileItemStatus);
        itemStatusTextView.setText(getAreaInfo(profile.id));



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
                ProfileHelper.activateProfile(mContext, profile.id, true);
                refresh();
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

        deleteButtonArea.setOnClickListener(deleteListener);
        deleteButton.setOnClickListener(deleteListener);

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
                        refresh();
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


    private String getAreaInfo(Long id){

        String areaInfoEnter = "";
        String areaInfoExit = "";

        for (AreaModel areaModel : mAreaList) {

            if(areaModel.profile_id == id){
                areaInfoEnter+=" "+areaModel.name+",";
            }
            else if(areaModel.exit_profile == id ||
                    (areaModel.exit_profile.equals(ProfileHelper.DEFAUL_PROFILE) && ProfileHelper.getDefaultProfile(mContext).equals(id))){
                areaInfoExit+=" "+areaModel.name+",";
            }
        }


        if(areaInfoEnter.length() > 0){
            areaInfoEnter = areaInfoEnter.substring(0, areaInfoEnter.length()-1);
        }
        if(areaInfoExit.length() > 0){
            areaInfoExit = areaInfoExit.substring(0, areaInfoExit.length()-1);
        }



        if(areaInfoEnter.length() == 0 && areaInfoExit.length() == 0){
            return mContext.getResources().getString(R.string.profile_tips_no_areas);
        }
        else if(areaInfoEnter.length() >0 && areaInfoExit.length() == 0){
            return mContext.getResources().getString(R.string.general_in)+ ": "+areaInfoEnter;
        }
        else if(areaInfoEnter.length() == 0 && areaInfoExit.length()>0){
            return mContext.getResources().getString(R.string.general_out)+ ": "+areaInfoExit;
        }
        else{
            return mContext.getResources().getString(R.string.general_in)+ ": "+areaInfoEnter+" - "+ mContext.getResources().getString(R.string.general_out)+ ": "+areaInfoExit;
        }
    }


    private void refresh(){
        ProfileListAdapter profileListAdapter = new ProfileListAdapter(mContext, mRecyclerView, ProfileHelper.getAllProfiles(mContext), AreaHelper.getAllArea(mContext));
        mRecyclerView.setAdapter(profileListAdapter);
    }

}