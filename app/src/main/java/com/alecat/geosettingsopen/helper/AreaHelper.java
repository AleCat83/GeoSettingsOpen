package com.alecat.geosettingsopen.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.alecat.geosettingsopen.database.DBHelper;
import com.alecat.geosettingsopen.engine.AreaTrainer;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.models.ProfileModel;
import com.alecat.geosettingsopen.models.TimebandModel;
import com.alecat.geosettingsopen.notifications.NotificationsHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AreaHelper {

    public static final Long EXTERNAL_AREA = -1L;

    private static final String TABLE_NAME = "area";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_ADDRESS = "address";
    private static final String FIELD_LATITUDE = "latitude";
    private static final String FIELD_LONGITUDE = "longitude";
    private static final String FIELD_RADIUS = "radius";
    private static final String FIELD_THRESHOLD = "threshold";
    private static final String FIELD_PROFILE_ID = "profile_id";
    private static final String FIELD_GHOST = "ghost";
    private static final String FIELD_PARENT_AREA_ID = "parent_area_id";

    private static final String FIELD_TRAINED = "trained";
    private static final String FIELD_TRAINING_POINT_NUMBER = "training_point_number";

    private static final String FIELD_ALL_WORLD= "all_world";

    private static final String FIELD_EXIT_PROFILE= "exit_profile";

    public static void saveArea(Context ctx, AreaModel area){


        DBHelper dbHelper = new DBHelper(ctx);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(area.id == null){ //se l'area no nesiste la creo

            ContentValues cv = new ContentValues();

            cv.put(FIELD_NAME, area.name);
            cv.put(FIELD_ADDRESS, area.address);
            cv.put(FIELD_LATITUDE, area.latitude);
            cv.put(FIELD_LONGITUDE, area.longitude);
            cv.put(FIELD_RADIUS, area.radius);
            cv.put(FIELD_THRESHOLD, area.threshold);
            cv.put(FIELD_PROFILE_ID, area.profile_id);
            cv.put(FIELD_GHOST, area.ghost);
            cv.put(FIELD_PARENT_AREA_ID, area.parent_area_id);
            cv.put(FIELD_TRAINED, area.trained);
            cv.put(FIELD_TRAINING_POINT_NUMBER, area.training_point_number);
            cv.put(FIELD_ALL_WORLD, area.all_world);
            cv.put(FIELD_EXIT_PROFILE, area.exit_profile);

            try {
                area.id = db.insert(TABLE_NAME, null, cv);


            } catch (SQLiteException sqle) {

            }
        }
        else{
            ContentValues cv = new ContentValues();
            cv.put(FIELD_NAME, area.name);
            cv.put(FIELD_ADDRESS, area.address);
            cv.put(FIELD_LATITUDE, area.latitude);
            cv.put(FIELD_LONGITUDE, area.longitude);
            cv.put(FIELD_RADIUS, area.radius);
            cv.put(FIELD_THRESHOLD, area.threshold);
            cv.put(FIELD_PROFILE_ID, area.profile_id);
            cv.put(FIELD_GHOST, area.ghost);
            cv.put(FIELD_PARENT_AREA_ID, area.parent_area_id);
            cv.put(FIELD_TRAINED, area.trained);
            cv.put(FIELD_TRAINING_POINT_NUMBER, area.training_point_number);
            cv.put(FIELD_ALL_WORLD, area.all_world);
            cv.put(FIELD_EXIT_PROFILE, area.exit_profile);

            try {

                db.update(TABLE_NAME, cv, FIELD_ID + "=" + area.id, null);

            } catch (SQLiteException sqle) {

            }

            Intent intent = new Intent("area-modified");
            intent.putExtra("area_id", area.id);
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

        }

        if(db != null && db.isOpen()){
            db.close();
        }

    }

    public static void deleteArea(Context ctx, Long id){

        if(id.equals(getCurrentArea(ctx))){

            setCurrentArea(ctx, EXTERNAL_AREA);
            TimebandHelper.removeTimeListenersByArea(ctx, id);

            if(AreaTrainer.isTrainingActive(ctx)){
                AreaTrainer.stopTraining(ctx); //se sto facendo train sull'area corrente spengo il training
            }
        }

        /*List<AreaModel> ghostAreas = getAllGhostAreaByParent(ctx, id);
        if(!ghostAreas.isEmpty()){
            for (AreaModel ghostArea: ghostAreas){
                deleteArea(ctx, ghostArea.id);
            }
        }*/

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.delete(TABLE_NAME, FIELD_ID + "=?", new String[]{Long.toString(id)});

        }
        catch (SQLiteException sqle) {
        }

        if(db != null && db.isOpen()){
            db.close();
        }

        Intent intent = new Intent("area-deleted");
        intent.putExtra("area_id", id);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

    }

    public static List<AreaModel> getAllArea(Context ctx){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { FIELD_ID, FIELD_NAME , FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD, FIELD_EXIT_PROFILE}, null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0, cursor.getLong(13));
            areasList.add(area);
        }
        cursor.close();

        if(db.isOpen()){
            db.close();
        }

        return areasList;
    }

    private static List<AreaModel> getAllGhostAreaByParent(Context ctx, Long id){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_NAME, FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD, FIELD_EXIT_PROFILE},
                FIELD_PARENT_AREA_ID + "=" + id, null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0, cursor.getLong(13));areasList.add(area);
        }

        cursor.close();

        if(db.isOpen()){
            db.close();
        }
        return areasList;

    }

    public static List<AreaModel> getAllGhostAreaByProfile(Context ctx, Long id){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_NAME, FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD, FIELD_EXIT_PROFILE},
                FIELD_PROFILE_ID + " = " + id + " AND " + FIELD_GHOST + " = 1", null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0, cursor.getLong(13));areasList.add(area);
        }

        cursor.close();

        if(db.isOpen()){
            db.close();
        }
        return areasList;

    }

    public static List<AreaModel> getAllParentArea(Context ctx){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_NAME, FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD, FIELD_EXIT_PROFILE},
                FIELD_GHOST + "= 0", null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0, cursor.getLong(13));areasList.add(area);
        }

        cursor.close();

        if(db.isOpen()){
            db.close();
        }
        return areasList;

    }

    public static AreaModel getArea(Context ctx, Long id){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_NAME , FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD, FIELD_EXIT_PROFILE},
                FIELD_ID + "=" + id, null, null, null, null, null);

        if(!cursor.moveToFirst()){
            return null;
        }

        AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0, cursor.getLong(13));
        cursor.close();

        if(db.isOpen()){
            db.close();
        }

        return area;
    }

    public static List<AreaModel> getAreasByProfile(Context ctx, Long profile_id){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_NAME , FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD, FIELD_EXIT_PROFILE},
                FIELD_PROFILE_ID + "=" + profile_id, null, null, null, null, null);


        List<AreaModel> areasList = new ArrayList<>();
        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0, cursor.getLong(13));
            areasList.add(area);
        }

        cursor.close();

        if(db.isOpen()){
            db.close();
        }

        return areasList;

    }

    public static AreaModel getAreaByLatLng(Context ctx, Double lat, Double lng, Long actualArea){

        List<AreaModel> areas = getAllParentArea(ctx);

        /*if(actualArea != null){//se sono in un'area hanno rilevanza sia le aree Parent che le aree ghost relative all'area in cui sono. Se sono all'esterno solo le aree ghost
            List<AreaModel> ghostAreas = getAllGhostAreaByParent(ctx, actualArea);
            for (AreaModel ghostArea:ghostAreas){
                areas.add(0, ghostArea);
            }
        }*/

        for (AreaModel area : areas) {

            if(area.all_world){
                continue;
            }
            ProfileModel profile = ProfileHelper.getProfile(ctx, area.profile_id);
            if(!profile.active){
                continue;
            }

            float[] distance = new float[2];

            Location.distanceBetween(lat, lng, area.latitude, area.longitude, distance);

            if(distance[0] <= area.radius+area.threshold){/*sperimentale il fatto di usare nuovamente la soglia*/
                return area;
            }
        }

        return null;
    }

    public static boolean activableByTime(Context ctx, Long areaId){

        List<TimebandModel> timeConditions = TimebandHelper.getAllTimeConditionByArea(ctx, areaId);

        if(timeConditions.isEmpty()){
            return true;//if no tiem  bands are set the profile is always activable
        }

        Calendar actualTime = Calendar.getInstance();

        for (TimebandModel timeCondition : timeConditions) {

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, timeCondition.start_hour);
            startTime.set(Calendar.MINUTE, timeCondition.start_minute);
            startTime.set(Calendar.SECOND, 0);

            Calendar stopTime = Calendar.getInstance();
            stopTime.set(Calendar.HOUR_OF_DAY, timeCondition.stop_hour);
            stopTime.set(Calendar.MINUTE, timeCondition.stop_minute);
            stopTime.set(Calendar.SECOND, 0);

            if(stopTime.before(startTime)){
                stopTime.add(Calendar.DATE, 1);
            }

            switch (startTime.get(Calendar.DAY_OF_WEEK )){

                case Calendar.SUNDAY:

                    if(!timeCondition.su){
                        continue;
                    }
                    break;
                case Calendar.MONDAY:

                    if(!timeCondition.mo){
                        continue;
                    }
                    break;
                case Calendar.TUESDAY:

                    if(!timeCondition.tu){
                        continue;
                    }
                    break;
                case Calendar.WEDNESDAY:

                    if(!timeCondition.we){
                        continue;
                    }
                    break;
                case Calendar.THURSDAY:

                    if(!timeCondition.th){
                        continue;
                    }
                    break;
                case Calendar.FRIDAY:

                    if(!timeCondition.fr){
                        continue;
                    }
                    break;
                case Calendar.SATURDAY:

                    if(!timeCondition.sa){
                        continue;
                    }
                    break;
            }

            //Toast.makeText(ctx, stopTime.getTime().toString(), Toast.LENGTH_LONG).show();

            if (startTime.before(actualTime) && stopTime.after(actualTime)) {
                return true;
            }

        }
        return false;
    }

    public static boolean isAreaActivable(Context ctx, Long areaId) {
        return getCurrentArea(ctx).equals(areaId) && activableByTime(ctx, areaId) && activableByTime(ctx, areaId);
    }

    public static Long getCurrentArea(Context ctx){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreference.getLong("actual_area_id", -2);
    }

    public static Long getPreviewsArea(Context ctx){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreference.getLong("previews_area_id", -2);
    }

    public static void setCurrentArea(Context ctx, Long id_area){

        TimebandHelper.removeTimeListenersByArea(ctx, AreaHelper.getCurrentArea(ctx));

        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPreferenceEditor.putLong("previews_area_id", getCurrentArea(ctx));
        sharedPreferenceEditor.apply();

        sharedPreferenceEditor.putLong("actual_area_id", id_area);
        sharedPreferenceEditor.apply();

        if(id_area.equals(EXTERNAL_AREA)){
            TimebandHelper.addTimeListenersByArea(ctx, id_area);
        }
        NotificationsHelper.sendStatusNotify(ctx, false);
    }

    public static void activateAreaProfile (Context ctx, Long area_id){

        if(isAreaActivable(ctx, area_id)){
            AreaModel areaModel = getArea(ctx, area_id);
            if(areaModel != null){
                ProfileHelper.activateProfile(ctx, areaModel.profile_id, false);
            }
        }
    }

    private static int areasNumber(Context ctx){
        return getAllArea(ctx).size();
    }

}
