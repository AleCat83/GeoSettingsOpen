package com.alecat.geosettingsopen.engine;

import android.content.Context;
import android.location.Location;

public class GSLocationListener implements com.google.android.gms.location.LocationListener {

    private final Context mContext;

    public GSLocationListener(Context context){
        this.mContext = context;
    }
    @Override
    public void onLocationChanged(Location location) {
        ChangeManager changeManager = new ChangeManager(mContext);
        changeManager.signalLocationEvent(location);
    }
}