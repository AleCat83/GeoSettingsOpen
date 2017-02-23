package com.alecat.geosettingsopen.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.engine.LocationService;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.models.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class PreferenceActivity extends BaseActivity{
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

            //set up exit profile

            final ListPreference listPreference = (ListPreference) findPreference("pref_default_exit_profile");
            setExitProfileValues(getActivity(), listPreference);

            //set up reset guide

            Preference resetGuideButton = findPreference("reset_guide");

            resetGuideButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Context ctx = preference.getContext();

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    sharedPreference.edit().putBoolean("guide_masterbutton_fired", false).apply();
                                    sharedPreference.edit().putBoolean("guide_masterbutton_off_fired", false).apply();
                                    sharedPreference.edit().putBoolean("guide_profileactivation_fired", false).apply();
                                    sharedPreference.edit().putBoolean("guide_map_fired", false).apply();
                                    sharedPreference.edit().putBoolean("guide_map_external_fired", false).apply();
                                    sharedPreference.edit().putBoolean("guide_profileoption_fired", false).apply();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage(getResources().getString(R.string.pref_options_summary_reset_guide)+"?")
                            .setPositiveButton(ctx.getResources().getString(R.string.general_yes), dialogClickListener)
                            .setNegativeButton(ctx.getResources().getString(R.string.general_no), dialogClickListener).show();
                    return true;
                }
            });
        }
    }

    private static void setExitProfileValues(Context ctx, ListPreference lp) {
        ArrayList<String> entries = new ArrayList<String>();
        ArrayList<String> entryValues = new ArrayList<String>();

        entries.add(ctx.getResources().getString(R.string.profile_no_profile));
        entryValues.add("0");

        List<ProfileModel> profileList = ProfileHelper.getAllProfiles(ctx);

        for (int i=0; i<profileList.size(); i++) {
            entryValues.add(String.valueOf(profileList.get(i).id));
            String profileName = profileList.get(i).name;
            if(profileName.length() > 15){
                profileName = profileName.substring(0,15)+"...";
            }
            entries.add(profileName);
        }

        lp.setEntries(entries.toArray(new String[entries.size()]));
        lp.setDefaultValue("0");
        lp.setEntryValues(entryValues.toArray(new String[entryValues.size()]));
    }
}