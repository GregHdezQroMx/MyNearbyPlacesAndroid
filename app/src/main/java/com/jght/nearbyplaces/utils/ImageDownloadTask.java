package com.jght.nearbyplaces.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {

    private String TAG = ImageDownloadTask.class.getSimpleName();
    private Bitmap bitmap = null;
    private int index;
    private InterfacePhotos interfacePhotos;

    public ImageDownloadTask(final InterfacePhotos interfacePhotos, final int index) {
        this.interfacePhotos = interfacePhotos;
        this.index = index;
    }
    @Override
    protected Bitmap doInBackground(String... url) {
        try{
            // Starting image download
            bitmap = downloadImage(url[0]);
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap photo) {
        // Showing download completion message
        interfacePhotos.asignBitmapPhoto(photo, index);
        Log.d( TAG, "Image downloaded successfully");
    }

    private Bitmap downloadImage(String strUrl) throws IOException {
        Bitmap bitmap=null;
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);

            /** Creating an http connection to communcate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            /** Connecting to url */
            urlConnection.connect();

            /** Reading data from url */
            iStream = urlConnection.getInputStream();

            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);

        }catch(Exception e){
            Log.d(TAG, e.toString());
        }finally{
            iStream.close();
        }
        return bitmap;
    }

    public interface InterfacePhotos {
        void asignBitmapPhoto(final Bitmap bPhoto, final int index);
    }
}
