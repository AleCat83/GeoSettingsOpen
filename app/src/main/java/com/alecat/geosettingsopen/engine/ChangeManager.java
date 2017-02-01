package com.alecat.geosettingsopen.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.model.AreaModel;


public class ChangeManager  {

    private final Context mContext;


    public ChangeManager(Context context){
        this.mContext = context;
    }

    public void signalLocationEvent(Location location) {

        Long actualAreaId = AreaHelper.getCurrentArea(this.mContext);

        if (!isBetterLocation(location)) {
            return;
        }
        newLocationAccepted(location);

        AreaModel areaTarget = AreaHelper.getAreaByLatLng(mContext,location.getLatitude(), location.getLongitude(), actualAreaId);

        if (!timeToChange(areaTarget.id)) {
            if(AreaTrainer.isTrainingActive(mContext)){
                AreaTrainer.trainArea(mContext, location, false);
            }
            return;
        }

        if(!AreaTrainer.isTrainingActive(mContext)){

            AreaHelper.setCurrentArea(this.mContext, areaTarget.id);

            if(AreaHelper.activableBytime(mContext, areaTarget.id)) {
                ProfileHelper.ActivateProfile(mContext, areaTarget.profile_id, false);
            }

        }
        else{
            AreaTrainer.trainArea(mContext, location, true);
        }
    }

    public void signalTimeEvent(Bundle eventData){

        Long areaId = eventData.getLong("area_id");

        if(AreaHelper.isAreaActivable(mContext, areaId)) {
            AreaModel areaModel = AreaHelper.getArea(mContext, areaId);
            if(areaModel != null){
                ProfileHelper.ActivateProfile(mContext, areaModel.profile_id, false);
            }
        }
        else{
            ProfileHelper.ActivateProfile(mContext, 1L, false);
        }

    }

    private boolean isBetterLocation(Location location) {

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        Float currentBestAccuracy= sharedPreference.getFloat("currentBestAccuracy", -1);
        Long currentBestTime= sharedPreference.getLong("currentBestTime", -1);
        String currentBestProvider= sharedPreference.getString("currentBestProvider", null);

        if (currentBestAccuracy == -1 || currentBestTime == -1 || currentBestProvider == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestTime;
        boolean isSignificantlyNewer = timeDelta > 120000;
        boolean isSignificantlyOlder = timeDelta < -120000;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestAccuracy);
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestProvider);

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private boolean timeToChange(Long area){


        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        Long newAreaId = sharedPreference.getLong("new_area_id", -2);
        Long actualAreaId = AreaHelper.getCurrentArea(this.mContext);
        Boolean mode_fast = sharedPreference.getBoolean("mode_fast", false);

        int newAreaCounter = sharedPreference.getInt("new_area_counter", 0);

        /*AreaModel areaModel = AreaManager.getArea(mContext, area);

        if (areaModel != null && areaModel.ghost) {
            if (areaModel.parent_area_id.equals(actualAreaId)) {
                log("sono in un area ghost, non esco!");
                return false;
            }
        }*/

        if(actualAreaId.equals(area)){ //reset the loop

            sharedPreferenceEditor.putInt("new_area_counter", 0);
            sharedPreferenceEditor.putLong("new_area_id", -2);

            if(mode_fast) { //stop fast service
                LocationServiceFast.stopService(this.mContext);
            }
            sharedPreferenceEditor.apply();
            return false; //is not time to change area
        }

        else if(area.equals(newAreaId)){ // new are already recognized, waiting confirm
            if(newAreaCounter == 2){ //confirmed

                if(mode_fast) { //stop fast service
                    LocationServiceFast.stopService(this.mContext);
                }
                sharedPreferenceEditor.putInt("new_area_counter", 0);
                sharedPreferenceEditor.putLong("new_area_id", -2);
                sharedPreferenceEditor.apply();
                return true; // is time to change
            }
            else{ //need other confirmation
                newAreaCounter = newAreaCounter+1;
                sharedPreferenceEditor.putInt("new_area_counter", newAreaCounter);
                sharedPreferenceEditor.apply();
                return false; //is not tiem to change yet
            }
        }
        else{ // it seems that we are in a new area but we need confirmation
            sharedPreferenceEditor.putLong("new_area_id", area);
            sharedPreferenceEditor.putInt("new_area_counter", 0);
            sharedPreferenceEditor.apply();

            LocationServiceFast.startService(this.mContext);

            return false;
        }


    }

    private void newLocationAccepted(Location location){


        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        sharedPreferenceEditor.putFloat("currentBestAccuracy", location.getAccuracy());
        sharedPreferenceEditor.putLong("currentBestTime", location.getTime());
        sharedPreferenceEditor.putString("currentBestProvider", location.getProvider());
        sharedPreferenceEditor.putLong("current_lat", Double.doubleToRawLongBits(location.getLatitude()));
        sharedPreferenceEditor.putLong("current_lng", Double.doubleToRawLongBits(location.getLongitude()));
        sharedPreferenceEditor.apply();
    }

    private void log(String text){

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        Boolean debug = sharedPreference.getBoolean("pref_debug_mode", false);

        if (debug){
            Log.d("geo-settings-debug", text);
        }
    }
}