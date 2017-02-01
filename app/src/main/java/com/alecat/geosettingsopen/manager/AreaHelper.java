package com.alecat.geosettingsopen.manager;

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
import android.util.Log;

import com.alecat.geosettingsopen.database.DBHelper;
import com.alecat.geosettingsopen.engine.AreaTrainer;
import com.alecat.geosettingsopen.model.AreaModel;
import com.alecat.geosettingsopen.model.ProfileModel;
import com.alecat.geosettingsopen.model.TimebandModel;
import com.alecat.geosettingsopen.notifications.NotificationsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AreaHelper {

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

    private static final Long EXTENAL_AREA_ID = 1L;

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

            setNoArea(ctx);
            TimebandHelper.removeTimeListenersByArea(ctx, id);

            if(AreaTrainer.isTrainingActive(ctx)){
                AreaTrainer.stopTraining(ctx);
            }
        }

        List<AreaModel> ghostAreas = getAllGhostAreaByParent(ctx, id);
        if(!ghostAreas.isEmpty()){
            for (AreaModel ghostArea: ghostAreas){
                deleteArea(ctx, ghostArea.id);
            }
        }

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
        Cursor cursor = db.query(TABLE_NAME, new String[] { FIELD_ID, FIELD_NAME , FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD}, null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0);
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
                new String[]{FIELD_ID, FIELD_NAME, FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD},
                FIELD_PARENT_AREA_ID + "=" + id, null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0);
            areasList.add(area);
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
                new String[]{FIELD_ID, FIELD_NAME, FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD},
                FIELD_PROFILE_ID + " = " + id + " AND " + FIELD_GHOST + " = 1", null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0);
            areasList.add(area);
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
                new String[]{FIELD_ID, FIELD_NAME, FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD},
                FIELD_GHOST + "= 0", null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0);
            areasList.add(area);
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
                new String[]{FIELD_ID, FIELD_NAME , FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD},
                FIELD_ID + "=" + id, null, null, null, null, null);

        if(!cursor.moveToFirst()){
            return null;
        }




        AreaModel areaModel = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0);

        cursor.close();


        if(db.isOpen()){
            db.close();
        }


        return areaModel;
    }


    public static List<AreaModel> getAreasByProfile(Context ctx, Long profile_id){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_NAME , FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD},
                FIELD_PROFILE_ID + "=" + profile_id, null, null, null, null, null);


        List<AreaModel> areasList = new ArrayList<>();
        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0);
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


        if(actualArea != null){//se sono in un'area hanno rilevanza sia le aree Parent che le aree ghost relative all'area in cui sono. Se sono all'esterno solo le aree ghost
            List<AreaModel> ghostAreas = getAllGhostAreaByParent(ctx, actualArea);
            for (AreaModel ghostArea:ghostAreas){
                areas.add(0, ghostArea);
            }
        }



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

        //se non esistono aree devo restituire la prima area oesterna disponibile


        List<AreaModel> outerAreas = getOuterAreas(ctx);



        return outerAreas.get(0);
    }



    private static List<AreaModel> getOuterAreas(Context ctx){

        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_NAME, FIELD_ADDRESS, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_RADIUS, FIELD_THRESHOLD, FIELD_PROFILE_ID, FIELD_GHOST, FIELD_PARENT_AREA_ID, FIELD_TRAINED, FIELD_TRAINING_POINT_NUMBER, FIELD_ALL_WORLD},
                FIELD_ALL_WORLD + "= 1", null, null, null, null, null);

        List<AreaModel> areasList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AreaModel area = new AreaModel(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5), cursor.getInt(6), cursor.getLong(7), cursor.getInt(8)>0, cursor.getLong(9), cursor.getInt(10) >0, cursor.getInt(11), cursor.getInt(12) >0);
            areasList.add(area);
        }

        cursor.close();

        if(db.isOpen()){
            db.close();
        }
        return areasList;

    }





    public static void includePointInArea(Context ctx, AreaModel area, Location point){


        Location areaLocation = new Location("areaLocation");
        areaLocation.setLatitude(area.latitude);
        areaLocation.setLongitude(area.longitude);

        Float distance = point.distanceTo(areaLocation);

        int aprx_distance = distance.intValue();


        if(area.radius+area.threshold <= aprx_distance){
            area.threshold = aprx_distance - area.radius + 5;
            saveArea(ctx, area);
        }
    }





    public static boolean activableBytime(Context ctx, Long areaId){


        List<TimebandModel> timeConditions = TimebandHelper.getAllTimeConditionByArea(ctx, areaId);

        if(timeConditions.isEmpty()){
            return true;//se non ho specificato orari il profilo è attivabile
        }

        Calendar actualTime = Calendar.getInstance();


        for (TimebandModel timeCondition : timeConditions) {



            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, timeCondition.start_hour);
            startTime.set(Calendar.MINUTE, timeCondition.start_minute);
            startTime.set(Calendar.SECOND, 0);

            //Toast.makeText(ctx, String.valueOf(startTime.get(Calendar.DAY_OF_WEEK )), Toast.LENGTH_LONG).show();


            Calendar stopTime = Calendar.getInstance();
            stopTime.set(Calendar.HOUR_OF_DAY, timeCondition.stop_hour);
            stopTime.set(Calendar.MINUTE, timeCondition.stop_minute);
            stopTime.set(Calendar.SECOND, 0);

            if(stopTime.before(startTime)){
                stopTime.add(Calendar.DATE, 1);
            }

            //Toast.makeText(ctx, stopTime.getTime().toString(), Toast.LENGTH_LONG).show();,

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
        return getCurrentArea(ctx).equals(areaId) && activableBytime(ctx, areaId) && activableBytime(ctx, areaId);
    }


    public static Long getCurrentArea(Context ctx){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreference.getLong("actual_area_id", -2);
    }

    public static void setCurrentArea(Context ctx, Long id_area){

        TimebandHelper.removeTimeListenersByArea(ctx, AreaHelper.getCurrentArea(ctx));


        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPreferenceEditor.putLong("actual_area_id", id_area);
        sharedPreferenceEditor.apply();


        TimebandHelper.addTimeListenersByArea(ctx, id_area);
        NotificationsManager.sendStatusNotify(ctx, false);
    }


    private static void setNoArea(Context ctx){

        SharedPreferences.Editor sharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sharedPreferenceEditor.putLong("actual_area_id", -2);
        sharedPreferenceEditor.apply();

        NotificationsManager.sendStatusNotify(ctx, false);
    }

    private static void log(Context ctx, String text){

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean debug = sharedPreference.getBoolean("pref_debug_mode", false);

        if (debug){
            Log.d("geo-settings-debug", text);
        }
    }

    public static void activateExternalAreaProfile (Context ctx) {
        AreaModel areaModel = getArea(ctx, EXTENAL_AREA_ID);
        if(areaModel != null){
            ProfileHelper.ActivateProfile(ctx, areaModel.profile_id, false);
        }
    }

    public static void activateAreaProfile (Context ctx, Long area_id){

        if(isAreaActivable(ctx, area_id)){
            AreaModel areaModel = getArea(ctx, area_id);
            if(areaModel != null){
                ProfileHelper.ActivateProfile(ctx, areaModel.profile_id, false);
            }
        }
    }

    private static int areasNumber(Context ctx){
        return getAllArea(ctx).size();
    }

}
