package com.alecat.geosettingsopen.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.engine.LocationService;
import com.alecat.geosettingsopen.engine.SystemStatusChecker;


public class BaseActivity extends AppCompatActivity {


    final private int REQUEST_LOCATION_PERMISSION = 1;
    private SwitchCompat mMasterSwitch;

    private final BroadcastReceiver mSystemStatusChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAlertMessages();
        }
    };

    private final BroadcastReceiver mProfileActivatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int fineLocationCheck = ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (fineLocationCheck != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        }

    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mSystemStatusChangedReceiver,
                new IntentFilter("system-status-changed"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mProfileActivatedReceiver,
                new IntentFilter("profile-activated"));
        super.onStart();

        showAlertMessages();

    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSystemStatusChangedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mProfileActivatedReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.supportInvalidateOptionsMenu();

        checkWriteSettingsPermissiion();

    }

    @Override
    protected void onPause(){
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();


        inflater.inflate(R.menu.action_bar_menu, menu);


        mMasterSwitch = (SwitchCompat)menu.findItem(R.id.mainswitch).getActionView().findViewById(R.id.enableProfilesMaster);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean wasServiceRunning = sharedPref.getBoolean("service_started", false);


        Boolean serviceActive = LocationService.isServiceStarted(this);

        if(wasServiceRunning && serviceActive){
            mMasterSwitch.setChecked(true);
        }
        else if(wasServiceRunning){
            startService();
            mMasterSwitch.setChecked(true);
        }
        else if(serviceActive){
            stopService();
            mMasterSwitch.setChecked(false);
        }
        else{
            mMasterSwitch.setChecked(false);
        }

        mMasterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mMasterSwitch = (SwitchCompat) buttonView;

                if (isChecked) {
                    int fineLocationCheck = ContextCompat.checkSelfPermission(BaseActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION);
                    if (fineLocationCheck != PackageManager.PERMISSION_GRANTED) {
                        askLocationPermission();
                    } else {
                        startService();
                    }

                } else {

                    stopService();

                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_settings:

                Intent i1 = new Intent(this, PreferenceActivity.class);
                startActivity(i1);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAlertMessages() {


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean tooltipEnabled = sharedPref.getBoolean("pref_tooltip_enabled", true);
        if (!tooltipEnabled) {
            return;
        }


        int systemStatus = SystemStatusChecker.checkStatus(this);

        String message = "";

        if (systemStatus == SystemStatusChecker.AIRPLANE_MODE_ENABLED) {
            message = getResources().getString(R.string.profile_tips_airplane_mode_enabled);
        } else if (systemStatus == SystemStatusChecker.LOCATION_SERVICE_DISABLED) {
            message = getResources().getString(R.string.profile_tips_locations_services_enabled);
        }


        if(!message.equals("")){
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .show();
        }

    }

    private void checkWriteSettingsPermissiion(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(this)){
                Intent writeSettingIntent = new Intent();
                writeSettingIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                writeSettingIntent.setData(Uri.parse("package:" + getPackageName()));
                this.startActivity(writeSettingIntent);
            }
        }

    }

    private void askLocationPermission(){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                } else {
                    if(mMasterSwitch != null) {
                        mMasterSwitch.setChecked(false);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void startService(){

        if (!LocationService.isServiceStarted(BaseActivity.this)) {
            LocationService.startService(BaseActivity.this);
        }

        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this).edit();
        sharedPreferenceEditor.putBoolean("service_started", true);
        sharedPreferenceEditor.apply();

    }

    private void stopService(){

        if (LocationService.isServiceStarted(BaseActivity.this)) {
            LocationService.stopService(BaseActivity.this);
        }

        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this).edit();
        sharedPreferenceEditor.putBoolean("service_started", false);
        sharedPreferenceEditor.apply();

    }
}