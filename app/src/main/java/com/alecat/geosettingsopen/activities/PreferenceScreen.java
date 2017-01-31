package com.alecat.geosettingsopen.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.engine.LocationService;

/**
 * Created by alessandro on 19/08/15.
 */
public class PreferenceScreen extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preference_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction()
                .replace(R.id.preference_container, new SettingsFragment())
                .commit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.OnSharedPreferenceChangeListener listener
                
                = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if(key.equals("pref_geolocalization_accuracy") || key.equals("pref_normal_interval") ||
                        key.equals("pref_notification_sticky") || key.equals("pref_notification_enabled")){

                    LocationService.stopService(PreferenceScreen.this);
                    LocationService.startService(PreferenceScreen.this);

                }
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);

    }



    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}