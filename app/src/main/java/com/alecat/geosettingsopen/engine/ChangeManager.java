package com.alecat.geosettingsopen.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.models.AreaModel;


public class ChangeManager  {

    private final Context mContext;


    public ChangeManager(Context context){
        this.mContext = context;
    }

    public void signalLocationEvent(Location location) {

        Long actualAreaId = AreaHelper.getCurrentArea(this.mContext);

        if (!isBetterLocation(location)) {//check if location received is good
            return;
        }
        newLocationAccepted(location);

        AreaModel areaTarget = AreaHelper.getAreaByLatLng(mContext,location.getLatitude(), location.getLongitude(), actualAreaId);

        Boolean isChangeAreaPossible = timeToChange(areaTarget == null ? AreaHelper.EXTERNAL_AREA : areaTarget.id); //check if is ok to change area

        if(AreaTrainer.isTrainingActive(mContext)){//if we are in training mode we do not change profile or area but we train the area itself
            AreaTrainer.trainArea(mContext, location, isChangeAreaPossible);
        }
        else if(isChangeAreaPossible){//if we are not in training and the location lead to a change

            AreaHelper.setCurrentArea(this.mContext, areaTarget == null ? AreaHelper.EXTERNAL_AREA : areaTarget.id);//if area target is null we use -1 as default

            if(areaTarget == null ){//if we are outside the ares
                AreaModel oldAreaModel = AreaHelper.getArea(mContext, actualAreaId);
                ProfileHelper.activateProfile(mContext, oldAreaModel == null ? ProfileHelper.DEFAUL_PROFILE : oldAreaModel.exit_profile, false);
            }
            else if(AreaHelper.activableByTime(mContext, areaTarget.id)) {
                ProfileHelper.activateProfile(mContext, areaTarget.profile_id, false);
            }
        }
    }

    public void signalTimeEvent(Bundle eventData){

        Long areaId = eventData.getLong("area_id");

        if(AreaHelper.isAreaActivable(mContext, areaId)) {
            AreaModel areaModel = AreaHelper.getArea(mContext, areaId);
            if(areaModel != null){
                ProfileHelper.activateProfile(mContext, areaModel.profile_id, false);
            }
        }
        else{
            AreaModel areaModel = AreaHelper.getArea(mContext, areaId);
            if(areaModel != null) {//check further
                ProfileHelper.activateProfile(mContext, areaModel.exit_profile, false);//sperimentale
            }
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

    private boolean timeToChange(Long targetAreaID){

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        Long newAreaId = sharedPreference.getLong("new_area_id", -2);
        Long actualAreaId = AreaHelper.getCurrentArea(this.mContext);
        Boolean mode_fast = sharedPreference.getBoolean("mode_fast", false);

        int newAreaCounter = sharedPreference.getInt("new_area_counter", 0);

        if(actualAreaId.equals(targetAreaID)){ //reset the loop

            sharedPreferenceEditor.putInt("new_area_counter", 0);
            sharedPreferenceEditor.putLong("new_area_id", -2);

            if(mode_fast) { //stop fast service
                LocationServiceFast.stopService(this.mContext);
            }
            sharedPreferenceEditor.apply();
            return false; //is not time to change area
        }

        else if(targetAreaID.equals(newAreaId)){ // new are already recognized, waiting confirm
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
            sharedPreferenceEditor.putLong("new_area_id", targetAreaID);
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