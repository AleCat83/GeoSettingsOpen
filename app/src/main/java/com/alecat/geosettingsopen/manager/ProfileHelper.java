package com.alecat.geosettingsopen.manager;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.database.DBHelper;
import com.alecat.geosettingsopen.model.AreaModel;
import com.alecat.geosettingsopen.model.ProfileModel;
import com.alecat.geosettingsopen.notifications.NotificationsManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileHelper {

    private static final String TABLE_NAME = "profile";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_WIFI = "wifi";
    private static final String FIELD_WIFI_ACTIVE = "wifi_active";
    private static final String FIELD_BLUETOOTH_ACTIVE = "bluetooth_active";
    private static final String FIELD_BLUETOOTH = "bluetooth";
    private static final String FIELD_MOBILE_DATA = "mobile_data";


    private static final String FIELD_SOUNDPROFILE = "soundprofile";
    private static final String FIELD_RINGTONES_VOLUME = "ringtones_volume";
    private static final String FIELD_NOTIFICATIONS_VOLUME = "notifications_volume";
    private static final String FIELD_MEDIA_VOLUME = "media_volume";
    private static final String FIELD_FEEDBACK_VOLUME = "feedback_volume";
    private static final String FIELD_ALARM_VOLUME = "alarm_volume";

    private static final String FIELD_RINGTONES = "ringtones_uri";
    private static final String FIELD_NOTIFICATIONS_SOUND = "notifications_uri";
    private static final String FIELD_VIBRATION = "vibration";
    private static final String FIELD_SOUNDPROFILE_ACTIVE = "soundprofile_active";
    private static final String FIELD_VOLUMES_ACTIVE = "volumes_active";
    private static final String FIELD_RINGTONES_URI_ACTIVE = "ringtones_uri_active";
    private static final String FIELD_NOTIFICATIONS_URI_ACTIVE = "notifications_uri_active";

    private static final String FIELD_BRIGHTNESS_LEVEL = "brightness_level";
    private static final String FIELD_BRIGHTNESS_AUTOMATIC = "brightness_automatic";
    private static final String FIELD_BRIGHTNESS_ACTIVE = "brightness_active";

    private static final String FIELD_NOTIFICATIONS_LED = "notifications_led";
    private static final String FIELD_NOTIFICATIONS_LED_ACTIVE = "notifications_led_active";


    private static final String FIELD_AUTOMATIC_SCREEN_ROTATION = "automatic_screen_rotation";
    private static final String FIELD_AUTOMATIC_SCREEN_ROTATION_ACTIVE = "automatic_screen_rotation_active";


    private static final String FIELD_SCREEN_TIMEOUT = "screen_timeout";
    private static final String FIELD_SCREEN_TIMEOUT_ACTIVE = "screen_timeout_active";

    private static final String FIELD_SMART_SCREEN = "smart_screen";
    private static final String FIELD_SMART_SCREEN_ACTIVE = "smart_screen_active";

    public static Long saveProfile(Context ctx, ProfileModel profile){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        if(profile.id == null){

            ContentValues cv = new ContentValues();
            cv.put(FIELD_NAME, profile.name);
            cv.put(FIELD_ACTIVE, profile.active);
            cv.put(FIELD_WIFI, profile.wifi);
            cv.put(FIELD_WIFI_ACTIVE, profile.wifi_active);
            cv.put(FIELD_BLUETOOTH, profile.bluetooth);
            cv.put(FIELD_BLUETOOTH_ACTIVE, profile.bluetooth_active);
            cv.put(FIELD_MOBILE_DATA, profile.mobile_data);

            cv.put(FIELD_SOUNDPROFILE, profile.soundprofile);
            cv.put(FIELD_SOUNDPROFILE_ACTIVE, profile.soundprofile_active);
            cv.put(FIELD_RINGTONES_VOLUME, profile.ringtones_volume);
            cv.put(FIELD_NOTIFICATIONS_VOLUME, profile.notifications_volume);
            cv.put(FIELD_MEDIA_VOLUME, profile.media_volume);
            cv.put(FIELD_FEEDBACK_VOLUME, profile.feedback_volume);
            cv.put(FIELD_ALARM_VOLUME, profile.alarm_volume);

            cv.put(FIELD_VOLUMES_ACTIVE, profile.volumes_active);
            cv.put(FIELD_RINGTONES, profile.ringtones_uri);
            cv.put(FIELD_RINGTONES_URI_ACTIVE, profile.ringtones_uri_active);
            cv.put(FIELD_NOTIFICATIONS_SOUND, profile.notifications_uri);
            cv.put(FIELD_NOTIFICATIONS_URI_ACTIVE, profile.notifications_uri_active);
            cv.put(FIELD_VIBRATION, profile.vibration);

            cv.put(FIELD_BRIGHTNESS_LEVEL, profile.brightness_level);
            cv.put(FIELD_BRIGHTNESS_AUTOMATIC, profile.brightness_automatic);
            cv.put(FIELD_BRIGHTNESS_ACTIVE, profile.brightness_active);
            cv.put(FIELD_NOTIFICATIONS_LED, profile.notifications_led);
            cv.put(FIELD_NOTIFICATIONS_LED_ACTIVE, profile.notifications_led_active);
            cv.put(FIELD_AUTOMATIC_SCREEN_ROTATION, profile.automatic_screen_rotation);
            cv.put(FIELD_AUTOMATIC_SCREEN_ROTATION_ACTIVE, profile.automatic_screen_rotation_active);
            cv.put(FIELD_SCREEN_TIMEOUT, profile.screen_timeout);
            cv.put(FIELD_SCREEN_TIMEOUT_ACTIVE, profile.screen_timeout_active);
            cv.put(FIELD_SMART_SCREEN, profile.smart_screen);
            cv.put(FIELD_SMART_SCREEN_ACTIVE, profile.smart_screen_active);


            try {
                profile.id = db.insert(TABLE_NAME, null, cv);

            } catch (SQLiteException sqle) {
                return 0L;
            }
        }
        else{
            ContentValues cv = new ContentValues();
            cv.put(FIELD_NAME, profile.name);
            cv.put(FIELD_ACTIVE, profile.active);
            cv.put(FIELD_WIFI, profile.wifi);
            cv.put(FIELD_WIFI_ACTIVE, profile.wifi_active);
            cv.put(FIELD_BLUETOOTH, profile.bluetooth);
            cv.put(FIELD_BLUETOOTH_ACTIVE, profile.bluetooth_active);
            cv.put(FIELD_MOBILE_DATA, profile.mobile_data);


            cv.put(FIELD_SOUNDPROFILE, profile.soundprofile);
            cv.put(FIELD_SOUNDPROFILE_ACTIVE, profile.soundprofile_active);
            cv.put(FIELD_RINGTONES_VOLUME, profile.ringtones_volume);
            cv.put(FIELD_NOTIFICATIONS_VOLUME, profile.notifications_volume);
            cv.put(FIELD_MEDIA_VOLUME, profile.media_volume);
            cv.put(FIELD_FEEDBACK_VOLUME, profile.feedback_volume);
            cv.put(FIELD_ALARM_VOLUME, profile.alarm_volume);

            cv.put(FIELD_VOLUMES_ACTIVE, profile.volumes_active);
            cv.put(FIELD_RINGTONES, profile.ringtones_uri);
            cv.put(FIELD_RINGTONES_URI_ACTIVE, profile.ringtones_uri_active);
            cv.put(FIELD_NOTIFICATIONS_SOUND, profile.notifications_uri);
            cv.put(FIELD_NOTIFICATIONS_URI_ACTIVE, profile.notifications_uri_active);
            cv.put(FIELD_VIBRATION, profile.vibration);

            cv.put(FIELD_BRIGHTNESS_LEVEL, profile.brightness_level);
            cv.put(FIELD_BRIGHTNESS_AUTOMATIC, profile.brightness_automatic);
            cv.put(FIELD_BRIGHTNESS_ACTIVE, profile.brightness_active);
            cv.put(FIELD_NOTIFICATIONS_LED, profile.notifications_led);
            cv.put(FIELD_NOTIFICATIONS_LED_ACTIVE, profile.notifications_led_active);
            cv.put(FIELD_AUTOMATIC_SCREEN_ROTATION, profile.automatic_screen_rotation);
            cv.put(FIELD_AUTOMATIC_SCREEN_ROTATION_ACTIVE, profile.automatic_screen_rotation_active);
            cv.put(FIELD_SCREEN_TIMEOUT, profile.screen_timeout);
            cv.put(FIELD_SCREEN_TIMEOUT_ACTIVE, profile.screen_timeout_active);
            cv.put(FIELD_SMART_SCREEN, profile.smart_screen);
            cv.put(FIELD_SMART_SCREEN_ACTIVE, profile.smart_screen_active);


            try {

                db.update(TABLE_NAME, cv, FIELD_ID + "=" + profile.id, null);

            } catch (SQLiteException sqle) {
                return 0L;
            }
        }


        if(db.isOpen()){
            db.close();
        }


        return profile.id;


    }

    public static void deleteProfile(Context ctx, Long id){


        List<AreaModel> areaList = AreaHelper.getAreasByProfile(ctx, id);

        //elimino prima le aree
        for (int i = 0; i < areaList.size(); i++) {
            AreaModel area = areaList.get(i);
            AreaHelper.deleteArea(ctx, area.id);
        }

        //controllo se e attivo, nel caso abilito default

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Long active_profile = sharedPref.getLong("active_profile", -1);

        if(active_profile.equals(id)){
            ActivateProfile(ctx, 1L, false);
        }

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, FIELD_ID + "=?", new String[]{Long.toString(id)});

        } catch (SQLiteException sqle) {
        }


        if(db != null && db.isOpen()){
            db.close();
        }

    }

   public static List<ProfileModel> getAllProfiles(Context ctx) {

       DBHelper dbHelper = new DBHelper(ctx);
       SQLiteDatabase db = dbHelper.getReadableDatabase();
       Cursor cursor  = db.query(TABLE_NAME, new String[]{
               FIELD_ID,
               FIELD_NAME,
               FIELD_ACTIVE,
               FIELD_WIFI,
               FIELD_WIFI_ACTIVE,
               FIELD_BLUETOOTH,
               FIELD_BLUETOOTH_ACTIVE,
               FIELD_MOBILE_DATA,
               FIELD_SOUNDPROFILE,
               FIELD_SOUNDPROFILE_ACTIVE,
               FIELD_RINGTONES_VOLUME,
               FIELD_NOTIFICATIONS_VOLUME,
               FIELD_MEDIA_VOLUME,
               FIELD_FEEDBACK_VOLUME,
               FIELD_ALARM_VOLUME,
               FIELD_VOLUMES_ACTIVE,
               FIELD_RINGTONES,
               FIELD_RINGTONES_URI_ACTIVE,
               FIELD_NOTIFICATIONS_SOUND,
               FIELD_NOTIFICATIONS_URI_ACTIVE,
               FIELD_VIBRATION,
               FIELD_BRIGHTNESS_LEVEL,
               FIELD_BRIGHTNESS_AUTOMATIC,
               FIELD_BRIGHTNESS_ACTIVE,
               FIELD_NOTIFICATIONS_LED,
               FIELD_NOTIFICATIONS_LED_ACTIVE,
               FIELD_AUTOMATIC_SCREEN_ROTATION,
               FIELD_AUTOMATIC_SCREEN_ROTATION_ACTIVE,
               FIELD_SCREEN_TIMEOUT,
               FIELD_SCREEN_TIMEOUT_ACTIVE,
               FIELD_SMART_SCREEN,
               FIELD_SMART_SCREEN_ACTIVE
       }, null, null, null, null, null, null);


       List<ProfileModel> profileList = new  ArrayList<>();

       while (cursor.moveToNext()) {
            ProfileModel profile = cursorToProfile(cursor);


            profileList.add(profile);
       }


       cursor.close();

       if(db.isOpen()){
           db.close();
       }



       return profileList;
   }



    public static List<ProfileModel> getEnabledProfiles(Context ctx){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{
                        FIELD_ID,
                        FIELD_NAME,
                        FIELD_ACTIVE,
                        FIELD_WIFI,
                        FIELD_WIFI_ACTIVE,
                        FIELD_BLUETOOTH,
                        FIELD_BLUETOOTH_ACTIVE,
                        FIELD_MOBILE_DATA,
                        FIELD_SOUNDPROFILE,
                        FIELD_SOUNDPROFILE_ACTIVE,
                        FIELD_RINGTONES_VOLUME,
                        FIELD_NOTIFICATIONS_VOLUME,
                        FIELD_MEDIA_VOLUME,
                        FIELD_FEEDBACK_VOLUME,
                        FIELD_ALARM_VOLUME,
                        FIELD_VOLUMES_ACTIVE,
                        FIELD_RINGTONES,
                        FIELD_RINGTONES_URI_ACTIVE,
                        FIELD_NOTIFICATIONS_SOUND,
                        FIELD_NOTIFICATIONS_URI_ACTIVE,
                        FIELD_VIBRATION,
                        FIELD_BRIGHTNESS_LEVEL,
                        FIELD_BRIGHTNESS_AUTOMATIC,
                        FIELD_BRIGHTNESS_ACTIVE,
                        FIELD_NOTIFICATIONS_LED,
                        FIELD_NOTIFICATIONS_LED_ACTIVE,
                        FIELD_AUTOMATIC_SCREEN_ROTATION,
                        FIELD_AUTOMATIC_SCREEN_ROTATION_ACTIVE,
                        FIELD_SCREEN_TIMEOUT,
                        FIELD_SCREEN_TIMEOUT_ACTIVE,
                        FIELD_SMART_SCREEN,
                        FIELD_SMART_SCREEN_ACTIVE
                },
                FIELD_ACTIVE + "=" + 1, null, null, null, null, null);


        List<ProfileModel> profileList = new  ArrayList<>();

        while (cursor.moveToNext()) {
            ProfileModel profile = cursorToProfile(cursor);
            profileList.add(profile);
        }



        cursor.close();
        if(db.isOpen()){
            db.close();
        }



        return profileList;
    }




   public static ProfileModel getProfile(Context ctx, Long id){

       DBHelper dbHelper = new DBHelper(ctx);
       SQLiteDatabase db = dbHelper.getReadableDatabase();
       Cursor cursor = db.query(TABLE_NAME,
               new String[]{
                       FIELD_ID,
                       FIELD_NAME,
                       FIELD_ACTIVE,
                       FIELD_WIFI,
                       FIELD_WIFI_ACTIVE,
                       FIELD_BLUETOOTH,
                       FIELD_BLUETOOTH_ACTIVE,
                       FIELD_MOBILE_DATA,
                       FIELD_SOUNDPROFILE,
                       FIELD_SOUNDPROFILE_ACTIVE,
                       FIELD_RINGTONES_VOLUME,
                       FIELD_NOTIFICATIONS_VOLUME,
                       FIELD_MEDIA_VOLUME,
                       FIELD_FEEDBACK_VOLUME,
                       FIELD_ALARM_VOLUME,
                       FIELD_VOLUMES_ACTIVE,
                       FIELD_RINGTONES,
                       FIELD_RINGTONES_URI_ACTIVE,
                       FIELD_NOTIFICATIONS_SOUND,
                       FIELD_NOTIFICATIONS_URI_ACTIVE,
                       FIELD_VIBRATION,
                       FIELD_BRIGHTNESS_LEVEL,
                       FIELD_BRIGHTNESS_AUTOMATIC,
                       FIELD_BRIGHTNESS_ACTIVE,
                       FIELD_NOTIFICATIONS_LED,
                       FIELD_NOTIFICATIONS_LED_ACTIVE,
                       FIELD_AUTOMATIC_SCREEN_ROTATION,
                       FIELD_AUTOMATIC_SCREEN_ROTATION_ACTIVE,
                       FIELD_SCREEN_TIMEOUT,
                       FIELD_SCREEN_TIMEOUT_ACTIVE,
                       FIELD_SMART_SCREEN,
                       FIELD_SMART_SCREEN_ACTIVE},
               FIELD_ID + "=" + id, null, null, null, null, null);



       ProfileModel profile = null;

       if (cursor.moveToNext()) {
            profile = cursorToProfile(cursor);
       }

       cursor.close();

       if(db.isOpen()){
           db.close();
       }


       return profile;
   }

    public static void ActivateProfile(Context ctx, Long id, boolean overwrite) {

        if (!overwrite) {
            if(isProfileActive(ctx, id)){
                return;
            }
        }

        ProfileModel profile = getProfile(ctx,id);

        if(!profile.active){
            return;
        }

        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPreferenceEditor.putLong("active_profile", profile.id);
        sharedPreferenceEditor.apply();

        if(profile.bluetooth_active){
            changeBluetoothState(profile.bluetooth);
        }

        if (profile.wifi_active) {
            changeWIFIState(ctx, profile.wifi);
        }

        if(profile.volumes_active){
            changeRingtonesVolume(ctx, profile.ringtones_volume);
            changeNotificationsVolume(ctx, profile.notifications_volume);
            changeMediaVolume(ctx, profile.media_volume);
            changeFeedbackVolume(ctx, profile.feedback_volume);
            //changeAlarmVolume(ctx, profile.alarm_volume);// TODO: 26/01/17 to check, doesn't work
        }

        if(profile.soundprofile_active){
            changeSoundProfile(ctx, profile.soundprofile);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(ctx)){
                return;
            }
        }

        if(profile.ringtones_uri_active){
            if(profile.ringtones_uri != null) {
                changeRingtones(ctx, Uri.parse(profile.ringtones_uri));
            }
        }

        if(profile.notifications_uri_active){
            if(profile.notifications_uri != null){
                changeNotifications(ctx, Uri.parse(profile.notifications_uri));
            }
        }

        if(profile.brightness_active){
            changeBrightness(ctx, profile.brightness_level);
            changeAutoBrightness(ctx, profile.brightness_automatic);
        }

        if(profile.screen_timeout_active){
            int[] timeoutValues = ctx.getResources().getIntArray(R.array.screen_timeout_values);
            changeScreenTimeout(ctx, timeoutValues[profile.screen_timeout]);
        }

        if(profile.automatic_screen_rotation_active){
            changeScreenRotation(ctx, profile.automatic_screen_rotation);
        }

        NotificationsManager.sendStatusNotify(ctx, true);
        Intent intent = new Intent("profile-activated");
        intent.putExtra("id", profile.id);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

    }


    private static void changeBluetoothState(int state){

        Boolean newState = null;

        if (state == 1){
            newState = true;
        }
        else if (state == 0){
            newState = false;
        }

        if(newState != null){
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (newState) {
                mBluetoothAdapter.enable();
            }
            else{
                mBluetoothAdapter.disable();
            }
        }
    }


    private static void changeWIFIState(Context ctx, int state){

        Boolean newState = null;

        if (state == 1){
            newState = true;
        }
        else if (state == 0){
            newState = false;
        }

        if(newState != null){
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(newState);
        }
    }

    public static boolean isProfileActive(Context ctx, Long profile_id){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);

        Long active_profile = sharedPref.getLong("active_profile", -1);


        return !(!profile_id.equals(active_profile) || active_profile == -1);

    }

    private static void changeSoundProfile(Context ctx, int state){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if(!notificationManager.isNotificationPolicyAccessGranted()){
                return;
            }
        }

        final AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        switch(state){
            case 0:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case 1:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case 2:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
        }

    }

    private static void changeRingtonesVolume(Context ctx,int value){

        AudioManager manager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_RING);

        int volume = maxVolume*value/100;

        manager.setStreamVolume(AudioManager.STREAM_RING, volume, 0);

    }



    private static void changeAlarmVolume(Context ctx, int value){

        AudioManager manager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_RING);

        int volume = maxVolume*value/100;

        manager.setStreamVolume(AudioManager.STREAM_ALARM , volume, 0);

    }

    private static void changeNotificationsVolume(Context ctx, int value){

        AudioManager manager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);

        int volume = maxVolume*value/100;

        manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);

    }

    private static void changeMediaVolume(Context ctx, int value){

        AudioManager manager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int volume = maxVolume*value/100;

        manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

    }

    private static void changeFeedbackVolume(Context ctx, int value){

        AudioManager manager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);

        int volume = maxVolume*value/100;

        manager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);

    }


    private static void changeRingtones(Context ctx, Uri uri){
        RingtoneManager.setActualDefaultRingtoneUri(ctx, RingtoneManager.TYPE_RINGTONE, uri);
    }


    private static void changeNotifications(Context ctx, Uri uri){
        RingtoneManager.setActualDefaultRingtoneUri(ctx, RingtoneManager.TYPE_NOTIFICATION, uri);
    }

    private static void changeAutoBrightness(Context ctx, Boolean value){

        if (value) {
            Settings.System.putInt(ctx.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
        else{
            Settings.System.putInt(ctx.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }


    }

    private static void changeBrightness(Context ctx, int value){

        if (value == -1) {
            return;
        }

        Settings.System.putInt(
                ctx.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                value*255/100);

        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        Settings.System.putInt(
                ctx.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                value*255/100);

    }

    private static void changeScreenTimeout(Context ctx, int value){

        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT,
                value);
    }

    private static void changeScreenRotation(Context ctx, int value) {
        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION,
                value);
    }

    private static ProfileModel cursorToProfile(Cursor cursor){

        return new ProfileModel(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getInt(2)>0,
                cursor.getInt(3),
                cursor.getInt(4)>0,
                cursor.getInt(5),
                cursor.getInt(6)>0,
                cursor.getInt(7),
                cursor.getInt(8),
                cursor.getInt(9)>0,
                cursor.getInt(10),
                cursor.getInt(11),
                cursor.getInt(12),
                cursor.getInt(13),
                cursor.getInt(14),
                cursor.getInt(15)>0,
                cursor.getString(16),
                cursor.getInt(17)>0,
                cursor.getString(18),
                cursor.getInt(19)>0,
                cursor.getInt(20),
                cursor.getInt(21),
                cursor.getInt(22)>0,//brightness_automatic
                cursor.getInt(23)>0,//brightness_active
                cursor.getInt(24),
                cursor.getInt(25)>0,
                cursor.getInt(26),
                cursor.getInt(27)>0,
                cursor.getInt(28),
                cursor.getInt(29)>0,
                cursor.getInt(30),
                cursor.getInt(31)>0
        );
    }

    private static int profilesNumber(Context ctx){
        return getAllProfiles(ctx).size();
    }

}
