package com.alecat.geosettingsopen.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.alecat.geosettingsopen.managers.AreaManager;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.notifications.NotificationsManager;


/**
 * Created by alessandro on 17/03/16.
 */
public class AreaTrainer {

    public static boolean isTrainingActive(Context ctx){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
        Long trainingAreaId = sharedPreference.getLong("training_mode", -2);
        return trainingAreaId != -2;
    }

    public static void startTraining (Context ctx, Long areaId, Long trainingTime){

        if(!AreaManager.getCurrentArea(ctx).equals(areaId)){
            return;
        }
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPrefEditor.putLong("training_stop_time",trainingTime );
        sharedPrefEditor.putLong("training_mode", areaId);
        sharedPrefEditor.apply();
        NotificationsManager.sendStatusNotify(ctx, true);
    }

    public static void stopTraining(Context ctx){
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPrefEditor.putLong("training_mode", -2);
        sharedPrefEditor.putLong("training_stop_time", -1);
        sharedPrefEditor.apply();
        NotificationsManager.sendStatusNotify(ctx, false);
    }

    public static void trainArea(Context ctx, Location location, boolean areaWouldChange){

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(ctx);

        Long trainingAreaId = sharedPreference.getLong("training_mode", -2);

        if(!isTrainingActive(ctx)){
            return;
        }

        Long training_stop_time = sharedPreference.getLong("training_stop_time", -1);

        if (training_stop_time != -1 && System.currentTimeMillis() / 1000 > training_stop_time) {
            stopTraining(ctx);
            return;
        }

        AreaModel trainingArea = AreaManager.getArea(ctx, trainingAreaId);
        trainingArea.training_point_number = trainingArea.training_point_number+1;

        if(areaWouldChange){
            Location trainingAreaCenter = new Location("");
            trainingAreaCenter.setLatitude(trainingArea.latitude);
            trainingAreaCenter.setLongitude(trainingArea.longitude);
            int distance = Math.round(location.distanceTo(trainingAreaCenter));

            if (distance <= (trainingArea.radius + trainingArea.threshold + Math.max(25, (int) (trainingArea.radius * 0.1)))) {
                trainingArea.threshold = distance - trainingArea.radius + 5;
            }
        }


        if(trainingArea.training_point_number == 500){//from 499 to 500
            trainingArea.trained = true;
            stopTraining(ctx);

        }

        AreaManager.saveArea(ctx, trainingArea);


        return;
    }

    /*private static void generateNewAreaGhostBroadcast(Context ctx, Long ghostAreaId){
        Intent newGhostArea = new Intent("new-ghost-area");
        Bundle bundle = new Bundle();
        bundle.putLong("area", ghostAreaId);
        newGhostArea.putExtras(bundle);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(newGhostArea);
    }*/
}
