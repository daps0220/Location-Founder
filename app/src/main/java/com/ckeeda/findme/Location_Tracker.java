package com.ckeeda.findme;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/**
 * Created by HP on 8/9/2015.
 */
public class Location_Tracker implements LocationListener {

    Context context;
    boolean isGPSEnable = false;
    boolean isNWEnable = false;
    static boolean location_found = false;
    static Location current_location;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    LocationManager location;

    Location_Tracker(Context con){
        this.context = con;
        location = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnable = location.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNWEnable = location.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPSEnable){
            Log.v("LOCATION", "IN GPS");
            current_location = location.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            location.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);

            if(current_location != null) {
                location_found = true;
                Log.v("LOCATION","LATITUDE:"+current_location.getLatitude());
                Log.v("LOCATION","LONGITUDE:"+current_location.getLongitude());
            }else {
                Log.v("LOCATION", "LOCATION NOT FOUND");
                current_location  = null;
                location_found = false;
            }
        }

        else if(isNWEnable){
            Log.v("LOCATION", "IN NW");
            current_location = location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
            if(current_location != null)
                location_found = true;
            else{
                Log.v("LOCATION", "LOCATION NOT FOUND");
                current_location  = null;
                location_found = false;
            }
        }

    }



    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
             current_location = location;
             location_found = true;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
