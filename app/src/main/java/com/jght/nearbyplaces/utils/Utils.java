package com.jght.nearbyplaces.utils;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.jght.nearbyplaces.Repository.models.google_places.NearbyPlaces;
import com.jght.nearbyplaces.Repository.models.google_places.Photo;
import com.jght.nearbyplaces.Repository.models.google_places.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class Utils {

    /**
     *
     * @param newFragment Es la instancia del fragmento a cargar
     */
    public static void loadFragment(final Activity activity, final int idContainer, final Fragment newFragment, final boolean firstFragment) {
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(idContainer, newFragment);
        //First Fragment not add to stack
        if (!firstFragment) { fragmentTransaction.addToBackStack(null); }
        fragmentTransaction.commit();
    }

    public static void loadAllPlacesPhotos(final NearbyPlaces nearbyPlaces, final String key) {
        for (Result place: nearbyPlaces.getResults()) {
            if (place.getPhotos() != null) {
                loadPlacePhotos(place.getPhotos(), key);
            }
        }

    }

    public static void loadPlacePhotos(final List<Photo> photos, final String key) {
        ImageDownloadTask[] imageDownloadTask = new ImageDownloadTask[photos.size()];

        int width = 280; //(int)(mMetrics.widthPixels*3)/4;
        int height = 200; //(int)(mMetrics.heightPixels*1)/2;

        String url = "https://maps.googleapis.com/maps/api/place/photo?";
        String keyBrowser = "key=" + key;
        String sensor = "sensor=true";
        String maxWidth="maxwidth=" + width;
        String maxHeight = "maxheight=" + height;
        url = url + keyBrowser + "&" + sensor + "&" + maxWidth + "&" + maxHeight;

        int index;
        // Traversing through all the photoreferences
        for(index=0;index<photos.size();index++){
            // Creating a task to download index-th photo
            imageDownloadTask[index] = new ImageDownloadTask(new ImageDownloadTask.InterfacePhotos() {
                @Override
                public void asignBitmapPhoto(final Bitmap bPhoto, final int index) {
                    photos.get(index).setbPhoto(bitmap2ByteArray(bPhoto));
                }
            }, index);

            String photoReference = "photoreference="+photos.get(index).getPhotoReference();

            // URL for downloading the photo from Google Services
            url = url + "&" + photoReference;

            // Downloading i-th photo from the above url
            imageDownloadTask[index].execute(url);
        }
    }

    public static void loadPlacePhoto(Photo photo, final ImageView imageView, final int index, final String key) {

        final ImageDownloadTask imageDownloadTask;

        int width = 280; //(int)(mMetrics.widthPixels*3)/4;
        int height = 200; //(int)(mMetrics.heightPixels*1)/2;

        String url = "https://maps.googleapis.com/maps/api/place/photo?";
        String keyBrowser = "key=" + key;
        String sensor = "sensor=true";
        String maxWidth="maxwidth=" + width;
        String maxHeight = "maxheight=" + height;
        url = url + keyBrowser + "&" + sensor + "&" + maxWidth + "&" + maxHeight;


        // Creating a task to download index-th photo
        imageDownloadTask = new ImageDownloadTask(new ImageDownloadTask.InterfacePhotos() {
            @Override
            public void asignBitmapPhoto(final Bitmap bPhoto,final int index) {
                imageView.setImageBitmap(bPhoto);
            }
        }, index);

        String photoReference = "photoreference="+photo.getPhotoReference();

        // URL for downloading the photo from Google Services
        url = url + "&" + photoReference;

        // Downloading i-th photo from the above url
        imageDownloadTask.execute(url);

    }

    public static byte [] bitmap2ByteArray(final Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public static Bitmap byteArray2Bitmap(final byte [] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

}
