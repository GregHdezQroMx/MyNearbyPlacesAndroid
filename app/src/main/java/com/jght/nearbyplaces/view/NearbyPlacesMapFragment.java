package com.jght.nearbyplaces.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.jght.nearbyplaces.databinding.FragmentNearbyPlacesMapBinding;
import com.jght.nearbyplaces.utils.Utils;

import java.util.List;


public class NearbyPlacesMapFragment extends Fragment implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {

    private final static String TAG = NearbyPlacesMapFragment.class.getSimpleName();
    private static final String ARG_PLACE = "PLACE";
    private LocationManager locationManager;
    private Location actualLocation;
    private String sActualLocation = "";
    private String searchNearbyPlacesText = "";
    private NearbyPlaces nearbyPlaces;
    private List<Marker>  markers;
    private Handler mHandler = new Handler();
    private Marker marker;

    protected FragmentNearbyPlacesMapBinding binding;

    private GoogleMap gMap;
    private NearbyPlacesAdapter nearbyPlacesAdapter;

    private static final String NEARBY_PLACE = "NEARBY_PLACE";
    private static final int GPS_REQUEST = 1111;
    private static final int GPS_ON = 0;

    public static NearbyPlacesMapFragment getInstance(final String nearbyPlace) {
        final NearbyPlacesMapFragment fragment = new NearbyPlacesMapFragment();
        final Bundle args = new Bundle();
        args.putSerializable(NEARBY_PLACE, nearbyPlace);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchNearbyPlacesText = getArguments().getString(NEARBY_PLACE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNearbyPlacesMapBinding.inflate(inflater, container, false);

        initUI();

        return binding.rlNearbyPlaces.getRootView();
    }


    @SuppressLint("NewApi")
    private void initUI() {

        ((MainActivity) getActivity()).setSupportActrionBarTitle("Nearby Places");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        if (binding.mvNearbyPlaces != null) {
            binding.mvNearbyPlaces.onCreate(null);
            binding.mvNearbyPlaces.onResume();
            binding.mvNearbyPlaces.getMapAsync(this);
            binding.fabMyLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkGpsEnabled();
                }
            });
        }

    }


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
        new AlertDialog.Builder(binding.getRoot().getContext())
                .setTitle("GPS")
                .setMessage("Habilite los permisos de ubicación")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        habilitarUbicacion();
                    }
                })
                .show();

    }

    public void habilitarUbicacion() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, GPS_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GPS_REQUEST) {
            if (resultCode == GPS_ON) {
                obtenerUbicacion();
            } else {
                checkGpsEnabled();
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        //gMap.setMyLocationEnabled(true);
        checkGpsEnabled();

        //Set onInfoWindow Click listener
        gMap.setOnMarkerClickListener(this);

    }


    private void obtenerUbicacion() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actualLocation = devolverUbicacion();
                if (actualLocation != null) {
                    gMap.clear();
                    if (markers != null) {
                        markers.clear();
                    }
                    markLocationAndNearbyPaces(actualLocation);
                } else {
                    Toast.makeText(getActivity(), "No se pudo obtener geolocalización, intente más tarde", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);

    }

    private Location devolverUbicacion() {
       Location location = null;
        LocationListener mlocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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


        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            habilitarUbicacion();
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
            getNearbyPlaces(searchNearbyPlacesText, actualLocation );
        }
    }


    private void getNearbyPlaces(final String searchText, final Location myLocation) {
        final NearbyPlacesInteractor interactor = new NearbyPlacesInteractor();

        interactor.searchNearbyPlaces(searchText, myLocation, getString(R.string.google_api_key), new NearbyPlacesInteractor.RetrofitListener() {
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
                Utils.loadAllPlacesPhotos(nearbyPlaces, getString(R.string.google_api_key));

                // Asign adapter to Recyclerview
                nearbyPlacesAdapter =  new NearbyPlacesAdapter(nearbyPlaces, new NearbyPlacesAdapter.NearbyPlacesClickListener() {
                    @Override
                    public void onClick(View vista, int position) {
                        Intent intentDetail = new Intent( getActivity(), NearbyPlaceDetailActivity.class);
                        intentDetail.putExtra("POSITION", position);
                        startActivity(intentDetail);

//                        NearbyPlaceDetailFragment fragment = NearbyPlaceDetailFragment.getInstance(nearbyPlaces.getResults().get(position), "");
//                        Utils.loadFragment(getActivity(), R.id.flContainer, fragment,false);
//                        ((MainActivity) getActivity()).setSupportActrionBarTitle("Nearby Place Detail");
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

    private void createNearbyPlacesMarkers(NearbyPlaces places) {

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng;

        for (Result place: places.getResults()) {
            latLng = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
            markerOptions.position(latLng);

            marker = gMap.addMarker(markerOptions);
            marker.setTitle(place.getName());
            marker.setSnippet(place.getVicinity());
            marker.setIcon(vectorToBitmap(R.drawable.ic_location));

            place.setIdMarker(marker.getId());
        }

    }

    private void setBitmapMarker (final Bitmap bitmap) {
        marker.setIcon( BitmapDescriptorFactory.fromBitmap(bitmap));
    }


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int position = getMarkerPosition(marker);
        binding.rvNearPlaces.scrollToPosition(position);
        nearbyPlacesAdapter.selectItem(position);
        nearbyPlacesAdapter.notifyItemChanged(position);

        //Disable default behaviour
        return true;
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
