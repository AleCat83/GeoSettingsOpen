package com.alecat.geosettingsopen.managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import com.alecat.geosettingsopen.database.DBhelper;
import com.alecat.geosettingsopen.engine.GlobalEventsReceiver;
import com.alecat.geosettingsopen.models.TimebandModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by alessandro on 25/01/15.
 */
public class TimebandManager {



    private static final String TABLE_NAME = "timeband";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_AREA_ID = "area_id";
    private static final String FIELD_START_HOUR= "start_hour";
    private static final String FIELD_START_MINUTE = "start_minute";
    private static final String FIELD_STOP_HOUR = "stop_hour";
    private static final String FIELD_STOP_MINUTE = "stop_minute";
    private static final String FIELD_MO = "mo";
    private static final String FIELD_TU = "tu";
    private static final String FIELD_WE = "we";
    private static final String FIELD_TH = "th";
    private static final String FIELD_FR = "fr";
    private static final String FIELD_SA = "sa";
    private static final String FIELD_SU = "su";



    public static void saveTimeCondition(Context ctx, TimebandModel timeCondition){

        DBhelper dbHelper = new DBhelper(ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(timeCondition.id == null){
            ContentValues cv = new ContentValues();

            cv.put(FIELD_AREA_ID, timeCondition.area_id);
            cv.put(FIELD_START_HOUR, timeCondition.start_hour);
            cv.put(FIELD_START_MINUTE, timeCondition.start_minute);
            cv.put(FIELD_STOP_HOUR, timeCondition.stop_hour);
            cv.put(FIELD_STOP_MINUTE, timeCondition.stop_minute);
            cv.put(FIELD_MO, timeCondition.mo);
            cv.put(FIELD_TU, timeCondition.tu);
            cv.put(FIELD_WE, timeCondition.we);
            cv.put(FIELD_TH, timeCondition.th);
            cv.put(FIELD_FR, timeCondition.fr);
            cv.put(FIELD_SA, timeCondition.sa);
            cv.put(FIELD_SU, timeCondition.su);

            try {
                timeCondition.id = db.insert(TABLE_NAME, null, cv);


            } catch (SQLiteException sqle) {

            }
        }
        else{
            ContentValues cv = new ContentValues();
            cv.put(FIELD_AREA_ID, timeCondition.area_id);
            cv.put(FIELD_START_HOUR, timeCondition.start_hour);
            cv.put(FIELD_START_MINUTE, timeCondition.start_minute);
            cv.put(FIELD_STOP_HOUR, timeCondition.stop_hour);
            cv.put(FIELD_STOP_MINUTE, timeCondition.stop_minute);
            cv.put(FIELD_MO, timeCondition.mo);
            cv.put(FIELD_TU, timeCondition.tu);
            cv.put(FIELD_WE, timeCondition.we);
            cv.put(FIELD_TH, timeCondition.th);
            cv.put(FIELD_FR, timeCondition.fr);
            cv.put(FIELD_SA, timeCondition.sa);
            cv.put(FIELD_SU, timeCondition.su);

            try {

                db.update(TABLE_NAME, cv, FIELD_ID + "=" + timeCondition.id, null);

            } catch (SQLiteException sqle) {

            }
        }


        checkTCOnActualArea(ctx, timeCondition.area_id);


        if(db != null && db.isOpen()){
            db.close();
        }





    }

    public static void deleteTimeCondition(Context ctx, Long id){

        TimebandModel timeCondition = getTimeCondition(ctx, id);

        if(timeCondition == null){
            return;
        }

        Long areaId = timeCondition.area_id;


        DBhelper dbHelper = new DBhelper(ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, FIELD_ID + "=?", new String[]{Long.toString(id)});
        }
        catch (SQLiteException sqle) {
        }

        if(db != null && db.isOpen()){
            db.close();
        }

        checkTCOnActualArea(ctx, areaId);


    }



    private static void checkTCOnActualArea(Context ctx, Long area_id){



        if(AreaManager.getCurrentArea(ctx).equals(area_id)){//se sono già nell'area rimuovo il listener e lo rimetto
            if(AreaManager.isAreaActivable(ctx, area_id)){//verifico se l'area è attivabile e nel caso attivo il profile
                AreaManager.activateAreaProfile(ctx, area_id);

            }
            else{
                AreaManager.activateExternalAreaProfile(ctx);
            }

            removeTimeListenersByArea(ctx, area_id);
            addTimeListenersByArea(ctx, area_id);

        }
    }





    public static List<TimebandModel> getAllTimeConditionByArea(Context ctx, Long area_id){

        DBhelper dbHelper = new DBhelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_AREA_ID, FIELD_START_HOUR, FIELD_START_MINUTE, FIELD_STOP_HOUR, FIELD_STOP_MINUTE, FIELD_MO, FIELD_TU, FIELD_WE, FIELD_TH,FIELD_FR, FIELD_SA, FIELD_SU},
                FIELD_AREA_ID + "=" + area_id, null, null, null, null, null);

        List<TimebandModel> tcList = new ArrayList<>();

        while (cursor.moveToNext()) {
            TimebandModel timebandModel = new TimebandModel(cursor.getLong(0), cursor.getLong(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6)>0, cursor.getInt(7)>0, cursor.getInt(8)>0, cursor.getInt(9)>0, cursor.getInt(10) >0, cursor.getInt(11)>0, cursor.getInt(12)>0);
            tcList.add(timebandModel);
        }

        cursor.close();

        if(db.isOpen()){
            db.close();
        }
        return tcList;

    }



    private static TimebandModel getTimeCondition(Context ctx, Long id){

        DBhelper dbHelper = new DBhelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{FIELD_ID, FIELD_AREA_ID, FIELD_START_HOUR, FIELD_START_MINUTE, FIELD_STOP_HOUR, FIELD_STOP_MINUTE, FIELD_MO, FIELD_TU, FIELD_WE, FIELD_TH,FIELD_FR, FIELD_SA, FIELD_SU},
                FIELD_ID + "=" + id, null, null, null, null, null);

        if(!cursor.moveToFirst()){
            return null;
        }


        TimebandModel timebandModel = new TimebandModel(cursor.getLong(0), cursor.getLong(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6)>0, cursor.getInt(7)>0, cursor.getInt(8)>0, cursor.getInt(9)>0, cursor.getInt(10) >0, cursor.getInt(11)>0, cursor.getInt(12)>0);
        cursor.close();


        if(db.isOpen()){
            db.close();
        }


        return timebandModel;
    }






    public static void addTimeListenersByArea(Context ctx, Long areaId){


        List<TimebandModel> timeConditions = getAllTimeConditionByArea(ctx, areaId);

        if(timeConditions.size() == 0){
            return ;
        }

        ArrayList<Long> alarmTimes = new ArrayList<>();

        for(TimebandModel timebandModel :timeConditions){

            Long activeTimestamp = getTimestamp(timebandModel.start_hour, timebandModel.start_minute);

            if(!alarmTimes.contains(activeTimestamp)){
              alarmTimes.add(activeTimestamp);
                addTimeListener(ctx, areaId, activeTimestamp);

            }

            Long deactiveTimestamp = getTimestamp(timebandModel.stop_hour, timebandModel.stop_minute);

            if(!alarmTimes.contains(deactiveTimestamp)){
                alarmTimes.add(deactiveTimestamp) ;
                addTimeListener(ctx, areaId, deactiveTimestamp);
            }
        }
    }




    private static Long getTimestamp(int hour, int minute){
        Calendar calActive = Calendar.getInstance();

        calActive.set(Calendar.HOUR_OF_DAY, hour);
        calActive.set(Calendar.MINUTE, minute);
        calActive.set(Calendar.SECOND, 0);

        if(calActive.before(Calendar.getInstance())){
            calActive.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calActive.getTimeInMillis();


    }






    private static PendingIntent getTimeConditionPI(Context ctx, Long areaID, Long time){


        Intent activateIntent = new Intent(ctx, GlobalEventsReceiver.class);

        Bundle extraActivationData = new Bundle();
        extraActivationData.putLong("area_id", areaID);
        activateIntent.putExtras(extraActivationData);

        activateIntent.setAction("area_time_condition_wake");

        int request_code = areaID.intValue() + time.intValue();

        return PendingIntent.getBroadcast(ctx, request_code, activateIntent, 0);

    }




    public static void removeTimeListenersByArea(Context ctx, Long areaId){


        List<TimebandModel> timeConditions = getAllTimeConditionByArea(ctx, areaId);

        if(timeConditions.size() == 0){
            return ;
        }


        ArrayList<Long> alarmTimes = new ArrayList<>();

        for(TimebandModel timebandModel :timeConditions){

            Long activeTimestamp = getTimestamp(timebandModel.start_hour, timebandModel.start_minute);

            if(!alarmTimes.contains(activeTimestamp)){
                alarmTimes.add(activeTimestamp);
                removeTimeListener(ctx, areaId, activeTimestamp);

            }

            Long deactiveTimestamp = getTimestamp(timebandModel.stop_hour, timebandModel.stop_minute);

            if(!alarmTimes.contains(deactiveTimestamp)){
                alarmTimes.add(deactiveTimestamp) ;
                removeTimeListener(ctx, areaId, deactiveTimestamp);
            }

        }
    }





    private static void addTimeListener(Context ctx, Long areaID, Long time){

        AlarmManager alarms ;
        alarms = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        alarms.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, getTimeConditionPI(ctx, areaID, time));

    }




    private static void removeTimeListener(Context ctx, Long areaID, long time){

        AlarmManager alarms ;
        alarms = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        alarms.cancel(getTimeConditionPI(ctx, areaID, time));

    }











}
