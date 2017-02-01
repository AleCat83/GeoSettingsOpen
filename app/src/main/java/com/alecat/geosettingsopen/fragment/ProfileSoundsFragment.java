package com.alecat.geosettingsopen.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.manager.ProfileHelper;
import com.alecat.geosettingsopen.model.ProfileModel;

public class ProfileSoundsFragment extends Fragment {

    private Context ctx;
    private View mView;
    private Long mProfileID;

    public static final int PERMISSION_REQUEST_ACCESS_NOTIFICATION_POLICY = 10;
    public static final int PERMISSION_REQUEST_WRITE_STORAGE_RINGTONE = 11;
    public static final int PERMISSION_REQUEST_WRITE_STORAGE_NOTIFICATION = 12;

    public static final int REQUESTCODE_PICKRINGTONE = 21;
    public static final int REQUESTCODE_PICKNOTIFICATIONS = 22;

    public static ProfileSoundsFragment newInstance(Long profileId) {
        ProfileSoundsFragment profileSoundsFragment = new ProfileSoundsFragment();
        Bundle args = new Bundle();
        args.putLong("ProfileId", profileId);
        profileSoundsFragment.setArguments(args);
        return profileSoundsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mProfileID = getArguments().getLong("ProfileId");

        mView = inflater.inflate(R.layout.fragment_profile_sounds, container, false);

        initItems();

        return mView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser ) {
            showGuide();
        }
    }

    private void initItems(){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        RelativeLayout soundprofileTableLayout = (RelativeLayout) mView.findViewById(R.id.profile_soundprofile_container);

        SwitchCompat soundprofileSwitch = (SwitchCompat) mView.findViewById(R.id.profile_soundprofile_active);

        soundprofileSwitch.setChecked(profile.soundprofile_active);

        soundprofileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSoundProfileActive(isChecked);
            }
        });

        soundprofileTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                    }else{
                        chooseSoundProfile();
                    }
                }
                else{
                    chooseSoundProfile();
                }
            }
        });

        setSoundProfileVisual();

        //INIT VOLUMES

        RelativeLayout volumesTableLayout = (RelativeLayout) mView.findViewById(R.id.profile_volumes_container);

        volumesTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_profile_sounds_volumes, null);

                SeekBar notificationsVolumeSeekbar = (SeekBar) dialogView.findViewById(R.id.profile_notifications_volume_value);
                notificationsVolumeSeekbar.setProgress(profile.notifications_volume);

                SeekBar ringtonesVolumeSeekbar = (SeekBar) dialogView.findViewById(R.id.profile_ringtones_volume_value);
                ringtonesVolumeSeekbar.setProgress(profile.ringtones_volume);

                SeekBar mediaVolumeSeekbar = (SeekBar) dialogView.findViewById(R.id.profile_media_volume_value);
                mediaVolumeSeekbar.setProgress(profile.media_volume);

                SeekBar feedbackVolumeSeekbar = (SeekBar) dialogView.findViewById(R.id.profile_feedback_volume_value);
                feedbackVolumeSeekbar.setProgress(profile.feedback_volume);

                /*SeekBar alarmVolumeSeekbar = (SeekBar) dialogView.findViewById(R.id.profile_alarm_volume_value);
                alarmVolumeSeekbar.setProgress(profile.alarm_volume);*/


                builder.setView(dialogView)
                        .setPositiveButton(ctx.getResources().getString(R.string.general_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                AlertDialog resultsDialog = (AlertDialog) dialog;
                                SeekBar notificationsVolumeSeekbar = (SeekBar) resultsDialog.findViewById(R.id.profile_notifications_volume_value);
                                SeekBar ringtonesVolumeSeekbar = (SeekBar) resultsDialog.findViewById(R.id.profile_ringtones_volume_value);
                                SeekBar mediaVolumeSeekbar = (SeekBar) resultsDialog.findViewById(R.id.profile_media_volume_value);
                                SeekBar feedbackVolumeSeekbar = (SeekBar) resultsDialog.findViewById(R.id.profile_feedback_volume_value);

                                //SeekBar alarmVolumeSeekbar = (SeekBar) resultsDialog.findViewById(R.id.profile_alarm_volume_value);

                                saveVolumes(ringtonesVolumeSeekbar.getProgress(),
                                        notificationsVolumeSeekbar.getProgress(),
                                        mediaVolumeSeekbar.getProgress(),
                                        feedbackVolumeSeekbar.getProgress());
                                        //alarmVolumeSeekbar.getProgress());
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

        SwitchCompat volumesSwitch = (SwitchCompat) mView.findViewById(R.id.profile_volumes_active);

        volumesSwitch.setChecked(profile.volumes_active);

        volumesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveVolumesActive(isChecked);
            }
        });

        setVolumesVisual();

        //INIT RINGTONES URI

        RelativeLayout ringtonesContainer = (RelativeLayout) mView.findViewById(R.id.profile_ringtone_uri_container);
        setRingtoneVisual();

        ringtonesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_STORAGE_RINGTONE);
                } else {
                    chooseRingtone();
                }
            }


        });

        SwitchCompat ringtonesUriSwitch = (SwitchCompat) mView.findViewById(R.id.profile_ringtone_uri_active);
        ringtonesUriSwitch.setChecked(profile.ringtones_uri_active);

        ringtonesUriSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveRingtonesUriActive(isChecked);
            }
        });

        //INIT NOTIFICATIONS URI

        RelativeLayout notificationsUriContainer = (RelativeLayout) mView.findViewById(R.id.profile_notifications_uri_container);
        setNotificationsVisual();

        notificationsUriContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_STORAGE_NOTIFICATION);
                } else {
                    chooseNotificationSound();
                }
            }


        });

        SwitchCompat notificationsUriSwitch = (SwitchCompat) mView.findViewById(R.id.profile_notifications_uri_active);
        notificationsUriSwitch.setChecked(profile.notifications_uri_active);

        notificationsUriSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveNotificationsUriActive(isChecked);
            }
        });

    }

    private void setSoundProfileVisual(){

        final ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        String[] soundProfileLabels = getResources().getStringArray(R.array.soundprofile_values);

        String text = "";
        if(profile.soundprofile_active){
            text = soundProfileLabels[profile.soundprofile];
        }
        TextView textView = (TextView) mView.findViewById(R.id.profile_soundprofile_value);
        textView.setText(text);
    }

    private void saveSoundProfile(int indexOfSelected){


        final ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        profile.soundprofile = indexOfSelected;
        ProfileHelper.saveProfile(getContext(), profile);

        saveSoundProfileActive(true);
    }

    private void saveSoundProfileActive(Boolean value){

        final ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        profile.soundprofile_active = value;
        ProfileHelper.saveProfile(getContext(), profile);

        SwitchCompat soundprofileSwitch = (SwitchCompat) mView.findViewById(R.id.profile_soundprofile_active);
        soundprofileSwitch.setChecked(value);

        setSoundProfileVisual();

        Toast.makeText(ctx, getActivity().getResources().getString(R.string.general_saved), Toast.LENGTH_SHORT).show();

    }

    private void saveVolumes(int ringtones, int notifications, int media, int feedbacks/*, int alarm*/){


        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);


        profile.ringtones_volume = ringtones;
        profile.notifications_volume = notifications;
        profile.media_volume = media;
        profile.feedback_volume = feedbacks;
        //profile.alarm_volume = alarm;

        ProfileHelper.saveProfile(getContext(),profile);

        saveVolumesActive(true);

    }

    private void saveVolumesActive(Boolean value){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        profile.volumes_active = value;
        ProfileHelper.saveProfile(getContext(),profile);

        SwitchCompat volumesSwitch = (SwitchCompat) mView.findViewById(R.id.profile_volumes_active);
        volumesSwitch.setChecked(value);

        setVolumesVisual();

        Toast.makeText(ctx, getActivity().getResources().getString(R.string.general_saved), Toast.LENGTH_SHORT).show();

    }

    private void setVolumesVisual(){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        String value = "";

        if(profile.volumes_active) {
            value = getString(R.string.profile_options_values_ringtones) + ": ";
            value = value + String.valueOf(profile.ringtones_volume) + "% ";

            value = value + getString(R.string.profile_options_values_notifications) + ": ";
            value = value + String.valueOf(profile.notifications_volume) + "% ";

            value = value + getString(R.string.profile_options_values_media) + ": ";
            value = value + String.valueOf(profile.media_volume) + "% \n";

            value = value + getString(R.string.profile_options_values_feedback) + ": ";
            value = value + String.valueOf(profile.feedback_volume) + "% ";

            /*value = value + getString(R.string.profile_options_values_alarm) + ": ";
            value = value + String.valueOf(profile.alarm_volume) + "% ";*/
        }

        TextView textView = (TextView) mView.findViewById(R.id.profile_volumes_value);
        textView.setText(value);
    }

    private void saveRingtone(Uri ringtoneUri){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);
        profile.ringtones_uri = ringtoneUri.toString();
        ProfileHelper.saveProfile(getContext(), profile);
        saveRingtonesUriActive(true);

    }

    private void saveRingtonesUriActive(Boolean value){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        profile.ringtones_uri_active = value;
        ProfileHelper.saveProfile(getContext(),profile);

        SwitchCompat ringtoneUriSwitch = (SwitchCompat) mView.findViewById(R.id.profile_ringtone_uri_active);
        ringtoneUriSwitch.setChecked(value);

        setRingtoneVisual();

    }

    private void setRingtoneVisual(){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        String ringtonesTitle = "";

        if(profile.ringtones_uri_active){
            if(profile.ringtones_uri != null) {
                Uri ringtoneUri = Uri.parse(profile.ringtones_uri);
                Ringtone ringTone = RingtoneManager.getRingtone(ctx, ringtoneUri);
                ringtonesTitle = ringTone.getTitle(getActivity());
            }
        }

        TextView textView = (TextView) mView.findViewById(R.id.profile_ringtone_uri_value);
        textView.setText(ringtonesTitle);
    }

    private void saveNotification(Uri notificationUri){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        profile.notifications_uri = notificationUri.toString();
        ProfileHelper.saveProfile(getContext(),profile);

        saveNotificationsUriActive(true);

    }

    private void saveNotificationsUriActive(Boolean value){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        profile.notifications_uri_active = value;
        ProfileHelper.saveProfile(getContext(),profile);

        SwitchCompat notificationUriSwitch = (SwitchCompat) mView.findViewById(R.id.profile_notifications_uri_active);
        notificationUriSwitch.setChecked(value);
        setNotificationsVisual();

    }

    private void setNotificationsVisual(){

        ProfileModel profile = ProfileHelper.getProfile(getContext(), mProfileID);

        String ringtonesTitle = "";

        if(profile.notifications_uri_active){
            if(profile.notifications_uri != null){
                Uri ringtoneUri = Uri.parse(profile.notifications_uri);
                Ringtone ringTone = RingtoneManager.getRingtone(ctx, ringtoneUri);
                ringtonesTitle = ringTone.getTitle(getActivity());
            }
        }

        TextView textView = (TextView) mView.findViewById(R.id.profile_notifications_uri_value);
        textView.setText(ringtonesTitle);
        textView.setText(ringtonesTitle);

    }


    protected void chooseSoundProfile(){

        ProfileModel profile = ProfileHelper.getProfile(getContext(), mProfileID);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle(R.string.profile_options_label_soundprofile)
                .setSingleChoiceItems(R.array.soundprofile_values, profile.soundprofile,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                saveSoundProfile(which);
                                AlertDialog dialog = (AlertDialog) dialogInterface;
                                dialog.dismiss();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void chooseRingtone(){


        ProfileModel profile = ProfileHelper.getProfile(getContext(), mProfileID);

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, R.string.profile_tips_ringtone);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        if(profile.ringtones_uri != null){
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(profile.ringtones_uri));
        }
        startActivityForResult(intent, REQUESTCODE_PICKRINGTONE);
    }

    public void chooseNotificationSound(){

        ProfileModel profile = ProfileHelper.getProfile(getContext(),mProfileID);

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, R.string.profile_options_label_notifications_uri);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        if(profile.notifications_uri != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(profile.notifications_uri));
        }
        startActivityForResult(intent, REQUESTCODE_PICKNOTIFICATIONS);
    }

    public void showGuide(){

        if(getContext() == null){
            return;//// TODO: 26/01/17 find better solution
        }
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean guideProfileOptionFired = sharedPreference.getBoolean("guide_profileoption_fired", false);

        if(!guideProfileOptionFired){

        ImageView soundProfileSwitch = (ImageView) mView.findViewById(R.id.profile_soundprofile_icon);
        if(soundProfileSwitch == null){
            return;
        }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_PICKRINGTONE && data != null) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                saveRingtone(ringtoneUri);
            }
        } else if (requestCode == REQUESTCODE_PICKNOTIFICATIONS && data != null) {
            Uri notificationUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (notificationUri != null) {
                saveNotification(notificationUri);
            }
        }
    }
}