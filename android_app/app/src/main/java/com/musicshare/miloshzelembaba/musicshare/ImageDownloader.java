package com.musicshare.miloshzelembaba.musicshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by miloshzelembaba on 2017-02-07.
 */

 abstract public class ImageDownloader{

    public static void getBitmapFromURL(final String src, final View v) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        URL url = new URL(src);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        ImageView albumCover = (ImageView) v.findViewById(R.id.album_cover);
                        albumCover.setVisibility(View.VISIBLE);
                        albumCover.setImageBitmap(myBitmap);
                    } catch (IOException e) {
                        // Log exception
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
