package com.jght.nearbyplaces.Repository.remote_data_source.retrofit;

import com.jght.nearbyplaces.Repository.models.google_places.NearbyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiPlacesInterface {

    @GET("maps/api/place/nearbysearch/json")
    Call<NearbyPlaces> getNearbyPlaces(@Query("location") String location, @Query("radius") String radius, @Query("type") String type, @Query("keyword") String keyword, @Query("key") String apiKey);


}
