package com.alecat.geosettingsopen.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alecat.geosettingsopen.R;

import java.util.ArrayList;

/**
 * Created by alessandro on 19/01/15.
 */
public class DBhelper extends SQLiteOpenHelper
{

    private static final String DBNAME="geosettingsel_db";
    private static final String CREATE_TABLE_PROFILE = "create table profile (_id integer primary key autoincrement," +
                                                                        "name text not null," +
                                                                        " active booblean," +
                                                                        " wifi int," +
                                                                        " wifi_active boolean,"+
                                                                        " bluetooth int,"+
                                                                        " bluetooth_active boolean,"+
                                                                        " mobile_data int,"+
                                                                        " soundprofile int,"+//sounds
                                                                        " soundprofile_active boolean,"+
                                                                        " ringtones_volume int,"+
                                                                        " notifications_volume int,"+
                                                                        " media_volume int,"+
                                                                        " feedback_volume int,"+
                                                                        " alarm_volume int," +
                                                                        " volumes_active boolean,"+
                                                                        " ringtones_uri string,"+
                                                                        " ringtones_uri_active booblean,"+
                                                                        " notifications_uri string,"+
                                                                        " notifications_uri_active boolean,"+
                                                                        " vibration int,"+
                                                                        " brightness_level int,"+//display
                                                                        " brightness_automatic boolean,"+
                                                                        " brightness_active boolean,"+
                                                                        " notifications_led int,"+
                                                                        " notifications_led_active boolean,"+
                                                                        " automatic_screen_rotation int,"+
                                                                        " automatic_screen_rotation_active boolean,"+
                                                                        " screen_timeout int,"+
                                                                        " screen_timeout_active boolean,"+
                                                                        " smart_screen int,"+
                                                                        " smart_screen_active boolean"+
                                                                        ")";



    private static final String CREATE_TABLE_AREA = "create table area (_id integer primary key autoincrement," +
                                                                        "name text,"+
                                                                        "address text not null,"+
                                                                        "latitude double,"+
                                                                        "longitude double,"+
                                                                        "radius int," +
                                                                        "threshold int," +
                                                                        "profile_id int,"+
                                                                        "ghost boolean," +
                                                                        "parent_area_id int," +
                                                                        "trained boolean," +
                                                                        "training_point_number int," +
                                                                        "all_world boolean)";


    private static final String CREATE_TABLE_TIME_BAND = "create table timeband (" +
                                                                    "_id integer primary key autoincrement,"+
                                                                    "area_id integer not null,"+
                                                                    "start_hour integer not null," +
                                                                    "start_minute integer not null,"+
                                                                    "stop_hour integer not null,"+
                                                                    "stop_minute integer not null," +
                                                                    "mo boolean not null," +
                                                                    "tu boolean not null," +
                                                                    "we boolean not null," +
                                                                    "th boolean not null," +
                                                                    "fr boolean not null," +
                                                                    "sa boolean not null," +
                                                                    "su boolean not null)";



    private static String INSERT_PROFILE_DEFAULT = "";
    private static String INSERT_AREA_DEFAULT = "";




    public DBhelper(Context context) {
        super(context, DBNAME, null, 6);//DB version
        INSERT_AREA_DEFAULT = "INSERT INTO area (name, address, latitude, longitude, radius, threshold, profile_id, all_world, ghost  ) VALUES('"+context.getResources().getString(R.string.area_external_area_default_name)+"', '', 0, 0, 0, 0, 1, 1, 0);";
        INSERT_PROFILE_DEFAULT = "INSERT INTO profile (name, active) VALUES('"+context.getResources().getString(R.string.profile_external_profile_name)+"', 1);";
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_AREA);
        db.execSQL(INSERT_PROFILE_DEFAULT);
        db.execSQL(INSERT_AREA_DEFAULT);
        db.execSQL(CREATE_TABLE_TIME_BAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS profile");
            db.execSQL("DROP TABLE IF EXISTS area");
            db.execSQL("DROP TABLE IF EXISTS timeband");

            db.execSQL(CREATE_TABLE_PROFILE);
            db.execSQL(CREATE_TABLE_AREA);
            db.execSQL(INSERT_AREA_DEFAULT);
            db.execSQL(INSERT_PROFILE_DEFAULT);
            db.execSQL(CREATE_TABLE_TIME_BAND);


        }


    }


    /*DEBUG*/

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            Cursor c = sqlDB.rawQuery(Query, null);

            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }




}