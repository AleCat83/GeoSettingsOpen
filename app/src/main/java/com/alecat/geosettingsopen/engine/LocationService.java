package com.alecat.geosettingsopen.engine;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.alecat.geosettingsopen.notifications.NotificationsHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private com.google.android.gms.location.LocationListener mLocationListener;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        NotificationsHelper.sendStatusNotify(this, false);


        return Service.START_STICKY;
    }


    @Override

    public void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
            mGoogleApiClient.disconnect();
            log("geosettings-debug: servizio annullato.");
        }
        NotificationsHelper.hideServiceNotification(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String accuracy;
        Integer updateInterval;
        Integer updateFastestInterval;

        accuracy = sharedPref.getString("pref_geolocalization_accuracy", "balanced");
        updateInterval = Integer.valueOf(sharedPref.getString("pref_normal_interval", "30"));
        updateFastestInterval = 10;

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(updateInterval * 1000);
        locationRequest.setFastestInterval(updateFastestInterval * 1000);


        if (accuracy.equals("balanced")) {
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else if (accuracy.equals("high")) {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        mLocationListener = new GSLocationListener(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mLocationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public static void startService(Context ctx){
        if(!hasRightPermissions(ctx)){
            return;
        }
        if(!isServiceStarted(ctx)){
            Intent i= new Intent(ctx, LocationService.class);
            ctx.startService(i);
        }
    }

    public static void stopService(Context ctx){

        if(isServiceStarted(ctx)){
            Intent i= new Intent(ctx, LocationService.class);
            ctx.stopService(i);
        }
    }

    public static boolean isServiceStarted(Context ctx){

        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;


    }

    private void log(String text){

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean debug = sharedPreference.getBoolean("pref_debug_mode", false);

        if (debug){
            Log.d("geo-settings-debug", text);
        }
    }

    private static boolean hasRightPermissions(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.System.canWrite(ctx)) {
                return false;
            }
            int permissionCheck = ContextCompat.checkSelfPermission(ctx,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}