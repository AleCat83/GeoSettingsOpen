package com.alecat.geosettingsopen.activities.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.managers.ProfileManager;
import com.alecat.geosettingsopen.models.ProfileModel;

/**
 * Created by alessandro on 23/08/15.
 */
public class NetsFragment  extends Fragment {

    private Long mProfileID;
    private View mView;

    public static NetsFragment newInstance(Long profileId) {
        NetsFragment netsFragment = new NetsFragment();
        Bundle args = new Bundle();
        args.putLong("ProfileId", profileId);
        netsFragment.setArguments(args);
        return netsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mProfileID = getArguments().getLong("ProfileId");

        mView = inflater.inflate(R.layout.profile_nets_fragment, container, false);

        initItems(mView);

        return mView;
    }


    private void initItems(View view){

        ProfileModel profile = ProfileManager.getProfile(getContext(),mProfileID);

        if(profile == null){
            Intent intent = new Intent(getActivity(), ProfileList.class);
            startActivity(intent);
            return;
        }

        //INIT WIFI

        RelativeLayout wifiTableLayout = (RelativeLayout) view.findViewById(R.id.profile_wifi_container);
        wifiTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileModel profileModel = ProfileManager.getProfile(getContext(),mProfileID);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.profile_options_label_wifi)
                        .setSingleChoiceItems(R.array.profile_active_unactive, profileModel.wifi,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        saveWifi(which);
                                        AlertDialog dialog = (AlertDialog) dialogInterface;
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        SwitchCompat wifiSwitch = (SwitchCompat) view.findViewById(R.id.profile_wifi_active);
        wifiSwitch.setChecked(profile.wifi_active);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveWIFIActive(isChecked);
            }
        });

        setWIFIVisual();

        //INIT BLUETOOTH

        RelativeLayout bluetoothTableLayout = (RelativeLayout) view.findViewById(R.id.profile_bluetooth_container);
        bluetoothTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                ProfileModel profileModel = ProfileManager.getProfile(getContext(),mProfileID);

                builder.setTitle(R.string.profile_options_label_bluetooth)
                        .setSingleChoiceItems(R.array.profile_active_unactive, profileModel.bluetooth,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        saveBluetooth(which);
                                        AlertDialog dialog = (AlertDialog) dialogInterface;
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        SwitchCompat bluetoothSwitch = (SwitchCompat) view.findViewById(R.id.profile_bluetooth_active);
        bluetoothSwitch.setChecked(profile.bluetooth_active);

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveBluetoothActive(isChecked);
            }
        });

        setBluetoothVisual();

    }

    private void saveWifi(int value){

        ProfileModel profile = ProfileManager.getProfile(getContext(),mProfileID);

        profile.wifi = value;

        ProfileManager.saveProfile(getContext(), profile);

        saveWIFIActive(true);

    }


    private void saveWIFIActive(Boolean value){
        ProfileModel profile = ProfileManager.getProfile(getContext(),mProfileID);

        profile.wifi_active = value;
        ProfileManager.saveProfile(getContext(), profile);

        SwitchCompat wifiSwitch = (SwitchCompat) mView.findViewById(R.id.profile_wifi_active);
        wifiSwitch.setChecked(value);

        setWIFIVisual();
    }

    private void setWIFIVisual(){

        ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

        String value = "";
        if(profile.wifi_active){
            switch (profile.wifi){
                case 0:
                    value = getString(R.string.profile_profile_deactivate);
                    break;
                case 1:
                    value = getString(R.string.profile_profile_activate);
                    break;
            }
        }

        TextView textView = (TextView) mView.findViewById(R.id.profile_wifi_value);
        textView.setText(value);
    }


    private void saveBluetooth(int value){

        ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

        profile.bluetooth = value;

        ProfileManager.saveProfile(getContext(), profile);

        saveBluetoothActive(true);

    }


    private void saveBluetoothActive(Boolean value){
        ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

        profile.bluetooth_active = value;
        ProfileManager.saveProfile(getContext(), profile);

        SwitchCompat bluetoothSwitch = (SwitchCompat) mView.findViewById(R.id.profile_bluetooth_active);
        bluetoothSwitch.setChecked(value);

        setBluetoothVisual();

    }

    private void setBluetoothVisual(){

        ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

        String value = "";

        if(profile.bluetooth_active){
            switch (profile.bluetooth){
                case 0:
                    value = getString(R.string.profile_profile_deactivate);
                    break;
                case 1:
                    value = getString(R.string.profile_profile_activate);
                    break;
            }
        }

        TextView textView = (TextView) mView.findViewById(R.id.profile_bluetooth_value);
        textView.setText(value);
    }
}

