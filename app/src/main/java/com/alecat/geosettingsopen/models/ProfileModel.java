package com.alecat.geosettingsopen.models;

/**
 * Created by alessandro on 17/01/15.
 */
public class ProfileModel {

    public Long id;
    public String name;
    public boolean active;
    public int wifi;
    public boolean wifi_active;
    public int bluetooth;
    public boolean bluetooth_active;
    public int mobile_data;

    public int soundprofile;

    public int ringtones_volume;
    public int notifications_volume;
    public int media_volume;
    public int feedback_volume;
    public int alarm_volume;
    public String ringtones_uri;
    public String notifications_uri;
    public int vibration;

    public boolean soundprofile_active;
    public boolean volumes_active;
    public boolean ringtones_uri_active;
    public boolean notifications_uri_active;



    public int brightness_level;
    public boolean brightness_automatic;
    public boolean brightness_active;
    public int notifications_led;
    public boolean notifications_led_active;
    public int automatic_screen_rotation;
    public boolean automatic_screen_rotation_active;
    public int screen_timeout;
    public boolean screen_timeout_active;
    public int smart_screen;
    public boolean smart_screen_active;


    public ProfileModel (
            Long id,
            String name,
            Boolean active,
            int wifi,
            Boolean wifi_active,
            int bluetooth,
            Boolean bluetooth_active,
            int mobile_data,
            int soundprofile,
            Boolean soundprofile_active,
            int ringtones_volume,
            int notifications_volume,
            int media_volume,
            int feedback_volume,
            int alarm_volume,
            Boolean volumes_active,
            String ringtones_uri,
            Boolean ringtones_uri_active,
            String notifications_uri,
            Boolean notifications_uri_active,
            int vibration,
            int brightness_level,
            Boolean brightness_automatic,
            Boolean brightness_active,
            int notifications_led,
            Boolean notifications_led_active,
            int automatic_screen_rotation,
            Boolean automatic_screen_rotation_active,
            int screen_timeout,
            Boolean screen_timeout_active,
            int smart_screen,
            Boolean smart_screen_active){
        this.id = id;
        this.name = name;
        this.active = active;
        this.wifi = wifi;
        this.wifi_active = wifi_active;
        this.bluetooth = bluetooth;
        this.bluetooth_active = bluetooth_active;
        this.mobile_data = mobile_data;
        this.soundprofile = soundprofile;
        this.soundprofile_active = soundprofile_active;
        this.ringtones_volume = ringtones_volume;
        this.notifications_volume = notifications_volume;
        this.media_volume = media_volume;
        this.feedback_volume = feedback_volume;
        this.alarm_volume = alarm_volume;
        this.volumes_active = volumes_active;
        this.ringtones_uri = ringtones_uri;
        this.ringtones_uri_active = ringtones_uri_active;
        this.notifications_uri = notifications_uri;
        this.notifications_uri_active = notifications_uri_active;
        this.vibration = vibration;

        this.brightness_level = brightness_level;
        this.brightness_automatic = brightness_automatic;
        this.brightness_active = brightness_active;
        this.notifications_led = notifications_led;
        this.notifications_led_active = notifications_led_active;
        this.automatic_screen_rotation = automatic_screen_rotation;
        this.automatic_screen_rotation_active = automatic_screen_rotation_active;
        this.screen_timeout = screen_timeout;
        this.screen_timeout_active = screen_timeout_active;
        this.smart_screen = smart_screen;
        this.smart_screen_active = smart_screen_active;


    }


    public boolean willDoSomething(){

        return this.soundprofile_active ||
                this.volumes_active ||
                this.ringtones_uri_active ||
                this.notifications_uri_active ||
                this.wifi_active ||
                this.bluetooth_active ||
                this.brightness_active ||
                this.automatic_screen_rotation_active ||
                this.screen_timeout_active;

    }
}