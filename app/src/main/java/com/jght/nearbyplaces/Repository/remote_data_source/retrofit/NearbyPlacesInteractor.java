package com.jght.nearbyplaces.Repository.remote_data_source.retrofit;

import android.location.Location;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jght.nearbyplaces.Repository.models.google_places.NearbyPlaces;

import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NearbyPlacesInteractor {

    private final static String TAG = NearbyPlacesInteractor.class.getSimpleName();
    private final static String RADIUS_SEARCH = "500";
    private final static String TYPE_SEARCH = "restaurant"; //TODO: DEBE BUSCAR PRINCIPALMENTE RESTAURANTES SIN EMBARGO PUEDE SER GENERAL
    private final static String BASE_URL = "https://maps.googleapis.com/";

    private GoogleApiPlacesInterface service;
    private Call<NearbyPlaces> nearbyPlacesCall;
    private NearbyPlaces nearbyPlaces;

    private Logger logger;


    public NearbyPlacesInteractor() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new okhttp3.OkHttpClient()) //OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(GoogleApiPlacesInterface.class);
    }

    public void searchNearbyPlaces(final String searchText, final Location location, final String key, final RetrofitListener interactorListener) {
        nearbyPlaces = new NearbyPlaces();

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String sLocation = latitude + "," + longitude;

        nearbyPlacesCall = service.getNearbyPlaces(sLocation, RADIUS_SEARCH, TYPE_SEARCH, searchText, key); //getString(R.string.google_api_key));

        nearbyPlacesCall.enqueue(new Callback<NearbyPlaces>() {
            @Override
            public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {

                if (response.body() != null && response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    interactorListener.onFailure(new Exception()); //new Throwable(response.errorBody().string()));
                    Logger.getAnonymousLogger().throwing(TAG,"searchNearbyPlaces", new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<NearbyPlaces> call, Throwable exec) {
                interactorListener.onFailure(new Exception());
                Logger.getAnonymousLogger().throwing(TAG,"searchNearbyPlaces", new Throwable(exec.getMessage()));
            }
        });
    }

    public interface RetrofitListener<T> extends OnFailureListener, OnSuccessListener<T> {
        void showMessage(String var1);
    }


}
