package com.jght.nearbyplaces.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jght.nearbyplaces.R;
import com.jght.nearbyplaces.Repository.models.google_places.NearbyPlaces;
import com.jght.nearbyplaces.Repository.models.google_places.Result;
import com.jght.nearbyplaces.Repository.remote_data_source.retrofit.NearbyPlacesInteractor;
import com.jght.nearbyplaces.view.MainActivity;
import com.jght.nearbyplaces.view.NearbyPlaceDetailFragment;
import com.jght.nearbyplaces.view.NearbyPlacesAdapter;

import java.util.List;

public class GeolocationUtils {
    private Context context;
    private static GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Location actualLocation;
    private String sActualLocation = "";
    private String searchNearbyPlacesText = "";
    private NearbyPlaces nearbyPlaces;
    private List<Marker> markers;
    private Handler mHandler = new Handler();
    private Marker marker;
    private GoogleMap gMap;
    private String key;
    private String searchText;
    private static final int GPS_REQUEST = 1111;
    private static final int GPS_ON = 0;

    public GeolocationUtils(String searchNearbyPlacesText, final Context context) {
        this.context = context;
        this.searchText = searchNearbyPlacesText;
        this.key = key;
    }

    /*
    public void loadMapAndPutMarkers() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        checkGpsEnabled();
    }

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }

    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e("TAG", "SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("TAG", "RESOLUTION_REQUIRED");
                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("TAG", "GPS NO DISPONIBLE");
                        break;
                }
            }
        });
    }

*/
    private void checkGpsEnabled() {
        if (isGpsEnabled()) {
            obtenerUbicacion();
        } else {
            showInfoAlert();
        }
    }

    private boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showInfoAlert() {
        new AlertDialog.Builder(context)
                .setTitle("GPS")
                .setMessage("Habilite los permisos de ubicación")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //habilitarUbicacion();
                    }
                })
                .show();

    }

//    public void habilitarUbicacion() {
//        final Intent intent = new Intent();
//        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivityForResult(intent, GPS_REQUEST);
//    }

    private void obtenerUbicacion() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actualLocation = devolverUbicacion();
                if (actualLocation != null) {
                    markLocationAndNearbyPaces(actualLocation);
                } else {
                    Toast.makeText(context, "No se pudo obtener geolocalización, intente más tarde", Toast.LENGTH_LONG).show();
                }
            }
        }, 10000);

    }

    private Location devolverUbicacion() {
        Location location = null;
        LocationListener mlocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //volver a obtener localizacion y lugares
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //habilitarUbicacion();
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            if (locationManager != null) {
                //Existe GPS_PROVIDER obtiene ubicación
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location == null) { //Trata con NETWORK_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
                if (locationManager != null) {
                    //Existe NETWORK_PROVIDER obtiene ubicación
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            if (location == null) { //Trata con PASSIVE_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mlocListener);
                if (locationManager != null) {
                    //Existe PASSIVE_PROVIDER obtiene ubicación
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }
        }
        if (location != null) {
            return location;
        } else {
            //Toast.makeText(this, "No se pudo obtener geolocalización", Toast.LENGTH_LONG).show();
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            return lastKnownLocation;
        }
    }

    private void markLocationAndNearbyPaces(final Location location) {
        if (location != null) {
            Marker marker;
            actualLocation = location;
            sActualLocation = actualLocation.getLatitude() + "," + actualLocation.getLongitude();

            LatLng myPosition = new LatLng( actualLocation.getLatitude(), actualLocation.getLongitude());
            marker = gMap.addMarker(new MarkerOptions().position(myPosition));
            marker.setTitle("Mi Ubicación");
            marker.setIcon(vectorToBitmap(R.drawable.ic_my_location));

            CameraPosition camera = new CameraPosition.Builder()
                    .target(myPosition)
                    .zoom(15) // LIMIT 21, 1 mundo 5 masa continental 10 ciudades 15 vista de calles 20 vista de edificios
                    .bearing(0) //0 - 365°, Orientación hacia el este en grados
                    .tilt(60) //0 - 90°,  inclinacion 3d edificios
                    .build();

            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            //getNearbyPlaces(searchNearbyPlacesText, actualLocation );
        }
    }


    /*
    private void getNearbyPlaces(final String searchText, final Location myLocation) {
        final NearbyPlacesInteractor interactor = new NearbyPlacesInteractor();

        interactor.searchNearbyPlaces(searchText, myLocation, context.getString(R.string.google_api_key), new NearbyPlacesInteractor.RetrofitListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //nearbyPlaces.getResults().clear();
            }

            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
            }


            @Override
            public void onSuccess(Object result) {
                nearbyPlaces = NearbyPlaces.getInstance( ((NearbyPlaces) result).getHtmlAttributions(),
                        ((NearbyPlaces) result).getResults(),
                        ((NearbyPlaces) result).getStatus() );

                // Asign adapter to Recyclerview
                nearbyPlacesAdapter =  new NearbyPlacesAdapter(nearbyPlaces, new NearbyPlacesAdapter.NearbyPlacesClickListener() {
                    @Override
                    public void onClick(View vista, int position) {
                        // TODO launch detail fragment
                        //nearbyPlaces.getResults().get(position).getPhotos().get(0).setbPhoto(iv);
                        //nearbyPlacesAdapter.getNearbyPlacesList().get(position).getPhotos().get(0).getbPhoto()
                        NearbyPlaceDetailFragment fragment = NearbyPlaceDetailFragment.getInstance(nearbyPlaces.getResults().get(position), "");
                        Utils.loadFragment(getActivity(), R.id.flContainer, fragment,false);
                        ((MainActivity) getActivity()).setSupportActrionBarTitle("Nearby Place Detail");
                    }
                });
                binding.rvNearPlaces.setHasFixedSize(true);
                binding.rvNearPlaces.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvNearPlaces.setAdapter(nearbyPlacesAdapter );

                createNearbyPlacesMarkers(nearbyPlaces);

                binding.avLoader.setVisibility(View.GONE);
            }
        });

    }

     */

    private void createNearbyPlacesMarkers(NearbyPlaces places) {

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng;

        gMap.clear();
        markers.clear();

        for (Result place: places.getResults()) {
            latLng = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
            markerOptions.position(latLng);

            marker = gMap.addMarker(markerOptions);
            marker.setTitle(place.getName());
            marker.setSnippet(place.getVicinity());
            marker.setIcon(vectorToBitmap(R.drawable.ic_location));

            place.setIdMarker(marker.getId());

            markers.add(marker);
        }

    }

    private void setBitmapMarker (final Bitmap bitmap) {
        marker.setIcon( BitmapDescriptorFactory.fromBitmap(bitmap));
    }


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private int getMarkerPosition(Marker marker) {
        int index = 0;
        for (Result place : nearbyPlaces.getResults()){
            if ( place.getIdMarker().equals(marker.getId()) ){
                return index;
            }
            index++;
        }
        return index;
    }
}
