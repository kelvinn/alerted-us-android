package us.alerted.alerted;

import android.location.Location;

import org.apache.http.NameValuePair;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class Utils {

    public static JSONArray format(Location location) {
        JSONArray sList = new JSONArray();
        Double lon = location.getLongitude(); // Need to add Longitude first to list
        Double lat = location.getLatitude();
        sList.put(lon);
        sList.put(lat);
        return sList;
    }

    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}