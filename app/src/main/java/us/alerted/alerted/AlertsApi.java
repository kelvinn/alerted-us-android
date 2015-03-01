package us.alerted.alerted;

import android.database.Observable;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by kelvin on 1/03/15.
 */


public interface AlertsApi {

    @GET("/api/v1/alerts/")
    AlertNew getMyThing(
            @Query("lat") String lat,
            @Query("lng") String lng);
}