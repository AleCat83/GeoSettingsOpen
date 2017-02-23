package com.alecat.geosettingsopen.models;

public class TimebandModel {


    public Long id;
    public Long area_id;

    public int start_hour;
    public int start_minute;

    public int stop_hour;
    public int stop_minute;

    public boolean mo;
    public boolean tu;
    public boolean we;
    public boolean th;
    public boolean fr;
    public boolean sa;
    public boolean su;

    public TimebandModel(Long id,
                         Long area_id,
                         int start_hour,
                         int start_minute,
                         int stop_hour,
                         int stop_minute,
                         boolean mo,
                         boolean tu,
                         boolean we,
                         boolean th,
                         boolean fr,
                         boolean sa,
                         boolean su){
        this.id = id;
        this.area_id = area_id;
        this.start_hour = start_hour;
        this.start_minute = start_minute;
        this.stop_hour = stop_hour;
        this.stop_minute = stop_minute;
        this.mo = mo;
        this.tu = tu;
        this.we = we;
        this.th = th;
        this.fr = fr;
        this.sa = sa;
        this.su = su;
    }
}