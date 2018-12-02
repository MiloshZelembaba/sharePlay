package com.miloshzelembaba.play.api.Services;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.miloshzelembaba.play.Image.ImageStore;
import com.miloshzelembaba.play.Models.Song;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

abstract public class ImageDownloader{

    public static void getBitmapFromURL(final Song song, final ImageView albumCover, final Activity activity) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        final ImageStore imageStore = ImageStore.getInstance();
                        String imageUrl = song.getImageUrl();
                        Bitmap tmp;
                        if (!imageStore.has(imageUrl)) {
                            URL url = new URL(song.getImageUrl());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            tmp = BitmapFactory.decodeStream(input);
                            imageStore.storeBitmap(imageUrl, tmp);
                            System.out.println("downloading image");
                        } else {
                            tmp = imageStore.getBitmap(imageUrl);
                            System.out.println("got image from image store");
                        }

                        final Bitmap myBitmap = tmp;


                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                song.setImage(myBitmap);
//                                ImageView albumCover = (ImageView) v.findViewById(R.id.song_image);
                                albumCover.setVisibility(View.VISIBLE);
                                albumCover.setImageBitmap(myBitmap);
//                              adapter.notifyDataSetChanged();
                            }
                        });
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
