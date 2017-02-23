package com.alecat.geosettingsopen.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.alecat.geosettingsopen.activity.ProfileListActivity;
import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.activity.ProfileActivity;
import com.alecat.geosettingsopen.engine.AreaTrainer;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.models.ProfileModel;

public class NotificationsHelper {

    public static void sendStatusNotify(Context ctx, boolean noisy){

        if(!isNotificationEnabled(ctx)){
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        if(noisy){
            builder.setSound(getNotificationSound(ctx));
        }

        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher));

        builder.setSmallIcon(R.drawable.ic_stat_logo);

        if(isNotificationSticky(ctx)){
            builder.setOngoing(true);
        }

        builder.setWhen(0); //hide notification time

        //set profile info
        Long activeProfileId = ProfileHelper.getActiveProfile(ctx);
        String activeProfileInfo = ctx.getResources().getString(R.string.profile_no_profile_active);

        if(!activeProfileId.equals(ProfileHelper.NO_PROFILE)){
            ProfileModel profileModel = ProfileHelper.getProfile(ctx, activeProfileId);
            if(profileModel != null){
                activeProfileInfo = ctx.getResources().getString(R.string.profile_profile_label)+": "+profileModel.name;
            }
        }

        builder.setContentTitle(activeProfileInfo);

        //set area info
        Long actualAreaId = AreaHelper.getCurrentArea(ctx);
        AreaModel actualAreaModel = null;

        String actualAreaInfo = "";

        if (!actualAreaId.equals(AreaHelper.EXTERNAL_AREA)){
            actualAreaModel = AreaHelper.getArea(ctx, actualAreaId);
            if(actualAreaModel != null){
                actualAreaInfo = ctx.getResources().getString(R.string.area_label_area) + ": " + actualAreaModel.name;
            }
        }
        else{
            actualAreaInfo = ctx.getResources().getString(R.string.area_external_area_default_name);
        }

        builder.setContentText(actualAreaInfo);

        if(actualAreaModel != null && !actualAreaModel.all_world && !actualAreaModel.trained){
            if(!AreaTrainer.isTrainingActive(ctx) && !actualAreaModel.trained){

                Intent trainingIntent = new Intent();
                trainingIntent.putExtra("area_id", actualAreaModel.id);
                trainingIntent.setAction(NotificationsReceiver.AREA_START_TRAINING);
                PendingIntent twoHoursPendingIntent = PendingIntent.getBroadcast(ctx, 1, trainingIntent, PendingIntent.FLAG_CANCEL_CURRENT);

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
        mNotifyMgr.notify(getNewNotificationID(ctx), builder.build());
    }

    public static void sendMessageNotify(Context ctx, String title, String content){

        if(isNotificationEnabled(ctx)){
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        builder.setSmallIcon(R.drawable.ic_stat_logo);

        builder.setWhen(0); //hide time

        RemoteViews notificationView = new RemoteViews(ctx.getPackageName(),
                R.layout.notification_message);

        notificationView.setTextViewText(R.id.notification_title, title);
        notificationView.setTextViewText(R.id.notification_content, content);

        builder.setContent(notificationView);

        NotificationManager mNotifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify((int) (System.currentTimeMillis()/1000), builder.build());
    }

    public static void hideServiceNotification(Context ctx){

        int notification_id = getOldNotificationID(ctx);

        if(notification_id != -1){

            NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(notification_id);

            setNewNotificationID(ctx, -1);
        }
    }

    private static boolean isNotificationEnabled(Context ctx){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getBoolean("pref_notification_enabled", true);
    }

    private static int getNewNotificationID(Context ctx){

        int notification_id = getOldNotificationID(ctx);

        if(notification_id == -1){
            notification_id = (int) (System.currentTimeMillis()/1000);
            setNewNotificationID(ctx, notification_id);
        }

        return notification_id;
    }

    private static int getOldNotificationID(Context ctx){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getInt("last_notification_id", -1);
    }

    private static void setNewNotificationID(Context ctx, int id){
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPrefEditor.putInt("last_notification_id", id);
        sharedPrefEditor.apply();
    }

    private static Uri  getNotificationSound(Context ctx){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return Uri.parse(sharedPref.getString("pref_notifications_sound", "DEFAULT_NOTIFICATION_URI"));
    }
    private static boolean isNotificationSticky(Context ctx){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getBoolean("pref_notification_sticky", true);
    }
}