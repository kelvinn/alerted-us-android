package us.alerted.alerted;

import android.location.Location;
import org.json.JSONArray;

public class Utils {

    public static JSONArray format(Location location) {
        JSONArray sList = new JSONArray();
        Double lon = location.getLongitude(); // Need to add Longitude first to list
        Double lat = location.getLatitude();
        sList.put(lon);
        sList.put(lat);
        return sList;
    }
}