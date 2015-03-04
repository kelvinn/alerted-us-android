package us.alerted.alerted;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by kelvin on 1/03/15.
 */


public interface AlertsApi {

    @GET("/api/v1/alerts/")
    List<AlertGson> getMyThing(
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("cap_date_received") String cap_date_received);
}