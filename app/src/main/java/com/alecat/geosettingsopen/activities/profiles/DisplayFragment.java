package com.alecat.geosettingsopen.activities.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.managers.ProfileManager;
import com.alecat.geosettingsopen.models.ProfileModel;

public class DisplayFragment extends Fragment {

    private Long mProfileID;
    private View mView;

    public static DisplayFragment newInstance(Long profileId) {
        DisplayFragment displayFragment = new DisplayFragment();
        Bundle args = new Bundle();
        args.putLong("ProfileId", profileId);
        displayFragment.setArguments(args);
        return displayFragment;
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile_display_fragment, container, false);

        mProfileID = getArguments().getLong("ProfileId");

        initItems(mView);

        return mView;
    }



    private void initItems(View view){

        ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

        if(profile == null){//in case something go wrong return to main activity
            Intent intent = new Intent(getActivity(), ProfileList.class);
            startActivity(intent);
            return;
        }

        //INIT BRIGHTNESS


        RelativeLayout brightnessTableLayout = (RelativeLayout) view.findViewById(R.id.profile_brightness_container);

        setBrightnessVisual();

        brightnessTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context ctx = getContext();

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                ProfileModel profile = ProfileManager.getProfile(ctx, mProfileID);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.profileoption_display_brightness, null);

                SeekBar brightnessSeekbar = (SeekBar) dialogView.findViewById(R.id.profile_brightness_level_value);
                brightnessSeekbar.setProgress(profile.brightness_level);

                CheckBox autoBrightnessCheck = (CheckBox) dialogView.findViewById(R.id.profile_brightness_automatic);

                autoBrightnessCheck.setChecked(profile.brightness_automatic);

                builder.setView(dialogView)
                        .setPositiveButton(ctx.getResources().getString(R.string.general_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                AlertDialog resultsDialog = (AlertDialog) dialog;
                                SeekBar brightnessSeekbar = (SeekBar) resultsDialog.findViewById(R.id.profile_brightness_level_value);
                                CheckBox autoBrightnessCheck = (CheckBox) resultsDialog.findViewById(R.id.profile_brightness_automatic);

                                saveBrightness(brightnessSeekbar.getProgress(),
                                        autoBrightnessCheck.isChecked());
                            }
                        })
                        .setNegativeButton(ctx.getResources().getString(R.string.general_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        SwitchCompat brightnessSwitch = (SwitchCompat) view.findViewById(R.id.profile_brightness_active);
        brightnessSwitch.setChecked(profile.brightness_active);

        brightnessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveBrightnessActive(isChecked);
            }
        });

        //INIT SCREEN ROTATION

        RelativeLayout automaticScreenRotationTableLayout = (RelativeLayout) view.findViewById(R.id.profile_automatic_screen_rotation_container);
        automaticScreenRotationTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context ctx = getContext();

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

                builder.setTitle(R.string.profile_options_label_automatic_screen_rotation)
                        .setSingleChoiceItems(R.array.profile_active_unactive, profile.automatic_screen_rotation,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        saveAutomaticScreenRotation(which);
                                        AlertDialog dialog = (AlertDialog) dialogInterface;
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        SwitchCompat automaticScreenRotationSwitch = (SwitchCompat) view.findViewById(R.id.profile_automatic_screen_rotation_active);
        automaticScreenRotationSwitch.setChecked(profile.automatic_screen_rotation_active);

        automaticScreenRotationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveAutomaticScreenRotationActive(isChecked);
            }
        });

        setAutomaticScreenRotationVisual();

        //INIT SCREEN TIMEOUT

        RelativeLayout screenTimeoutTableLayout = (RelativeLayout) view.findViewById(R.id.profile_screen_timeout_container);
        screenTimeoutTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context ctx = getContext();

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                ProfileModel profile = ProfileManager.getProfile(ctx,mProfileID);

                builder.setTitle(R.string.profile_options_label_screen_timeout)
                        .setSingleChoiceItems(R.array.screen_timeout_labels, profile.screen_timeout,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        saveScreenTimeout(which);
                                        AlertDialog dialog = (AlertDialog) dialogInterface;
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        SwitchCompat screenTimeoutSwitch = (SwitchCompat) view.findViewById(R.id.profile_screen_timeout_active);
        screenTimeoutSwitch.setChecked(profile.screen_timeout_active);

        screenTimeoutSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveScreenTimeoutActive(isChecked);
            }
        });

        setScreenTimeoutVisual();

    }

    private void saveBrightness(int level, boolean automatic){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx,mProfileID);

        profile.brightness_level = level;
        profile.brightness_automatic = automatic;
        ProfileManager.saveProfile(ctx, profile);

        saveBrightnessActive(true);
    }

    private void saveBrightnessActive(Boolean value){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx,mProfileID);

        profile.brightness_active = value;
        ProfileManager.saveProfile(ctx, profile);

        SwitchCompat brightnessSwitch = (SwitchCompat) mView.findViewById(R.id.profile_brightness_active);
        brightnessSwitch.setChecked(value);

        setBrightnessVisual();

    }

    private void setBrightnessVisual(){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx, mProfileID);

        TextView textView = (TextView) mView.findViewById(R.id.profile_brightness_value);

        String value = "";

        if(profile.brightness_active){
            if(profile.brightness_automatic){
                value = getString(R.string.profile_options_values_automatic);
            }
            else{
                value = getString(R.string.profile_options_label_brightness)+": "+profile.brightness_level+"%";
            }
        }
        textView.setText(value);
    }

    private void saveAutomaticScreenRotation(int value){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx, mProfileID);

        profile.automatic_screen_rotation = value;
        ProfileManager.saveProfile(ctx, profile);

        saveAutomaticScreenRotationActive(true);
    }



    private void saveAutomaticScreenRotationActive(Boolean value){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx, mProfileID);

        profile.automatic_screen_rotation_active = value;
        ProfileManager.saveProfile(ctx, profile);

        SwitchCompat automaticScreenRotation = (SwitchCompat) mView.findViewById(R.id.profile_automatic_screen_rotation_active);
        automaticScreenRotation.setChecked(value);

        setAutomaticScreenRotationVisual();

    }

    private void setAutomaticScreenRotationVisual(){

        ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

        String value = "";

        if(profile.automatic_screen_rotation_active){
            switch (profile.automatic_screen_rotation){
                case 0:
                    value = getString(R.string.profile_profile_deactivate);
                    break;
                case 1:
                    value = getString(R.string.profile_profile_activate);
                    break;
            }
        }

        TextView textView = (TextView) mView.findViewById(R.id.profile_automatic_screen_rotation_value);
        textView.setText(value);
    }

    private void saveScreenTimeout(int value){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx, mProfileID);

        profile.screen_timeout = value;
        ProfileManager.saveProfile(ctx, profile);

        saveScreenTimeoutActive(true);

    }

    private void saveScreenTimeoutActive(Boolean value){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx,mProfileID);

        profile.screen_timeout_active = value;
        ProfileManager.saveProfile(ctx,profile);


        SwitchCompat screenTimeoutSwitch = (SwitchCompat) mView.findViewById(R.id.profile_screen_timeout_active);
        screenTimeoutSwitch.setChecked(value);

        setScreenTimeoutVisual();

    }

    private void setScreenTimeoutVisual(){

        Context ctx = getContext();

        ProfileModel profile = ProfileManager.getProfile(ctx, mProfileID);

        String[] timeoutLabels = getResources().getStringArray(R.array.screen_timeout_labels);

        String text = timeoutLabels[profile.screen_timeout];

        TextView textView = (TextView) mView.findViewById(R.id.profile_screen_timeout_value);
        textView.setText(text);
    }
}