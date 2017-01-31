package com.alecat.geosettingsopen.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alecat.geosettingsopen.engine.AreaTrainer;

/**
 * Created by alessandro on 20/08/15.
 */
public class NotificationsReceiver extends BroadcastReceiver {

    public static final String AREA_START_TRAINING = "area.training.request";
    public static final String AREA_STOP_TRAINING = "area.stoptraining.request";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(AREA_START_TRAINING)) {

            Bundle extra = intent.getExtras();
            Long areaID = extra.getLong("area_id");
            Long training_stop_time = System.currentTimeMillis()/1000+(2*60*60);//2 hour period as default for notification training start

            if(areaID != -2){
                AreaTrainer.startTraining(context, areaID, training_stop_time );
            }
        }
        else if(intent.getAction().equals(AREA_STOP_TRAINING)) {
            AreaTrainer.stopTraining(context);
        }
    }
}