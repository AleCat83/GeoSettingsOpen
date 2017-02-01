package com.alecat.geosettingsopen.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class SystemStatusChecker {

    public final static int SYSTEM_DISABLED = 0;
    public final static int AIRPLANE_MODE_ENABLED = 1;
    public final static int LOCATION_SERVICE_DISABLED = 2;
    public final static int SYSTEM_ENABLED = 3;

    public static int checkStatus(Context ctx){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean wasServiceRunning = sharedPref.getBoolean("service_started", true);

        //check if disabled

        if(!wasServiceRunning){
            return SYSTEM_DISABLED;
        }

        //check airplane mode

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if(Settings.System.getInt(ctx.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0){
                    return AIRPLANE_MODE_ENABLED;
                }
        } else {
            if(Settings.Global.getInt(ctx.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                return AIRPLANE_MODE_ENABLED;
            }
        }

        //check location status

        LocationManager lm = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!gps_enabled && !network_enabled){
            return LOCATION_SERVICE_DISABLED;
        }
        return SYSTEM_ENABLED;
    }
}
