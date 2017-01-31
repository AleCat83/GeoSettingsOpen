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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;




public class LocationServiceFast extends Service implements
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

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {

        int updateInterval = 5;
        int updateFastestInterval = 5;

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(updateInterval * 1000);
        locationRequest.setFastestInterval(updateFastestInterval * 1000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationListener = new GSLocationListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mLocationListener);

        log("Location services connesso.");
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    public static void startService(Context ctx){
        if(!hasRightPermissions(ctx)){
            return;
        }

        if(!isServiceStarted(ctx)){
            Intent i= new Intent(ctx, LocationServiceFast.class);
            ctx.startService(i);

            SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
            sharedPreferenceEditor.putBoolean("mode_fast", true);
            sharedPreferenceEditor.apply();

        }
    }

    public static void stopService(Context ctx){

        if(isServiceStarted(ctx)){
            Intent i= new Intent(ctx, LocationServiceFast.class);
            ctx.stopService(i);
        }

        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPreferenceEditor.putBoolean("mode_fast", false);
        sharedPreferenceEditor.apply();

    }

    public static boolean isServiceStarted(Context ctx){

        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationServiceFast.class.getName().equals(service.service.getClassName())) {
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