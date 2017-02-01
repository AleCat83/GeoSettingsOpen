package com.alecat.geosettingsopen.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.activity.ProfileActivity;
import com.alecat.geosettingsopen.activity.ProfileListActivity;
import com.alecat.geosettingsopen.engine.AreaTrainer;
import com.alecat.geosettingsopen.manager.AreaHelper;
import com.alecat.geosettingsopen.manager.ProfileHelper;
import com.alecat.geosettingsopen.model.AreaModel;
import com.alecat.geosettingsopen.model.ProfileModel;

public class NotificationsManager {

    public static void sendStatusNotify(Context ctx, boolean noisy){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);

        if(!sharedPref.getBoolean("pref_notification_enabled", true)){
            return;
        }

        int notification_id = sharedPref.getInt("last_notification_id", -1);
        int now = (int) (System.currentTimeMillis()/1000);
        if(notification_id == -1){
            notification_id = now;
            SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
            sharedPrefEditor.putInt("last_notification_id", notification_id);
            sharedPrefEditor.apply();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        if(noisy){
            builder.setSound(Uri.parse(sharedPref.getString("pref_notifications_sound", "DEFAULT_NOTIFICATION_URI")));
        }

        builder.setSmallIcon(R.drawable.ic_stat_logo);

        boolean is_sticky = sharedPref.getBoolean("pref_notification_sticky", true);
        if(is_sticky){
            builder.setOngoing(true);
        }

        builder.setWhen(0); //hide notification time

        //set profile info
        Long activeProfileId = sharedPref.getLong("active_profile", -1);
        String activeProfileInfo = ctx.getResources().getString(R.string.profile_tips_system_active);

        if(activeProfileId != -1){
            ProfileModel profileModel = ProfileHelper.getProfile(ctx, activeProfileId);
            if(profileModel != null){
                activeProfileInfo = ctx.getResources().getString(R.string.profile_profile_label)+": "+profileModel.name;
            }
        }
        builder.setContentTitle(activeProfileInfo);

        //set area info
        Long actualAreaId = sharedPref.getLong("actual_area_id", -2);
        AreaModel actualAreaModel = null;

        String actualAreaInfo = "";

        if (actualAreaId != -2){
            actualAreaModel = AreaHelper.getArea(ctx, actualAreaId);
            if(actualAreaModel != null){
                actualAreaInfo = ctx.getResources().getString(R.string.area_label_area) + ": " + actualAreaModel.name;
            }
        }

        builder.setContentText(actualAreaInfo);

        if(actualAreaModel != null && !actualAreaModel.all_world && !actualAreaModel.trained){
            if(!AreaTrainer.isTrainingActive(ctx) && !actualAreaModel.trained){

                Intent trainingIntentntent = new Intent();
                trainingIntentntent.putExtra("area_id", actualAreaModel.id);
                trainingIntentntent.setAction(NotificationsReceiver.AREA_START_TRAINING);
                PendingIntent twoHoursPendingIntent = PendingIntent.getBroadcast(ctx, 1, trainingIntentntent, PendingIntent.FLAG_CANCEL_CURRENT);

                builder.setLights(ContextCompat.getColor(ctx, R.color.ColorAccent), 1000, 3000);

                NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.training_turnon_36,
                        ctx.getResources().getString(R.string.area_training_notify_advice),
                        twoHoursPendingIntent).build();

                builder.addAction(action);

            }
            else{
                Intent trainingIntent = new Intent();
                trainingIntent.setAction(NotificationsReceiver.AREA_STOP_TRAINING);
                PendingIntent trainingPendingIntent = PendingIntent.getBroadcast(ctx, 4, trainingIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                builder.setLights(ContextCompat.getColor(ctx, R.color.red), 500, 1000);

                NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                        R.drawable.training_turnoff_36,
                        ctx.getResources().getString(R.string.area_training_notify_title),
                        trainingPendingIntent).build();

                builder.addAction(action);

            }
        }

        Intent mainNotificationIntent;

        if(actualAreaModel != null){
            mainNotificationIntent = new Intent(ctx, ProfileActivity.class);
            mainNotificationIntent.putExtra("id", actualAreaModel.profile_id);
        }
        else{
            mainNotificationIntent = new Intent(ctx, ProfileListActivity.class);
        }
        PendingIntent pendingMainIntent = PendingIntent.getActivity(ctx, 0,
                mainNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingMainIntent);


        NotificationManager mNotifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notification_id, builder.build());
    }



    public static void hideServiceNotification(Context ctx){

        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);

        int notification_id = sharedPref.getInt("last_notification_id", -1);

        if(notification_id != -1){
            nMgr.cancel(notification_id);

            SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
            sharedPrefEditor.putInt("last_notification_id", -1);
            sharedPrefEditor.apply();
        }
    }
}