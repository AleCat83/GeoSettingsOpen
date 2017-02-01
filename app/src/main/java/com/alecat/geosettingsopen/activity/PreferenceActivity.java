package com.alecat.geosettingsopen.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.engine.LocationService;

public class PreferenceActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);

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

                    LocationService.stopService(PreferenceActivity.this);
                    LocationService.startService(PreferenceActivity.this);

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