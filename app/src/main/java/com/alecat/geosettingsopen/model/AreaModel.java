package com.alecat.geosettingsopen.model;

public class AreaModel {

    public Long id;
    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public int radius;
    public int threshold;
    public Long profile_id;
    public boolean ghost;
    public Long parent_area_id;
    public boolean trained;
    public int training_point_number;
    public boolean all_world;

    public AreaModel (Long id,String name, String address, double latitude, double longitude, int radius,int threshold,  Long profile_id, boolean ghost, Long parent_area_id, boolean trained, int training_point_number, boolean all_world){
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.threshold = threshold;
        this.profile_id = profile_id;
        this.ghost = ghost;
        this.parent_area_id = parent_area_id;
        this.trained = trained;
        this.training_point_number = training_point_number;
        this.all_world = all_world;
    }
}