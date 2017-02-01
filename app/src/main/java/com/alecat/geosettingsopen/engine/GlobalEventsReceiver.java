package com.alecat.geosettingsopen.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.helper.TimebandHelper;


public class GlobalEventsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Boolean wasServiceRunning = sharedPref.getBoolean("service_started", true);
            if(wasServiceRunning){
                LocationService.startService(context);
                TimebandHelper.addTimeListenersByArea(context, AreaHelper.getCurrentArea(context));
                AreaTrainer.stopTraining(context);
            }
        }

        else if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction()) ||
                Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction()) ||
                intent.getAction().equals("system-enabled-changed")) {


            int systemStatus = SystemStatusChecker.checkStatus(context);

            if(systemStatus == SystemStatusChecker.SYSTEM_ENABLED){
                LocationService.startService(context);
            }
            else if(systemStatus == SystemStatusChecker.SYSTEM_DISABLED){
                LocationService.stopService(context);
            }
            else if(systemStatus == SystemStatusChecker.AIRPLANE_MODE_ENABLED){
                LocationService.stopService(context);
            }
            else if(systemStatus == SystemStatusChecker.LOCATION_SERVICE_DISABLED){
                LocationService.stopService(context);
            }
        }

        else if(intent.getAction().equals("area_time_condition_wake")){
            ChangeManager changeManager = new ChangeManager(context);
            changeManager.signalTimeEvent(intent.getExtras());
        }

        //Intent toogleServiceIntent = new Intent("system-status-changed");
        //LocalBroadcastManager.getInstance(context).sendBroadcast(toogleServiceIntent);

    }
}
