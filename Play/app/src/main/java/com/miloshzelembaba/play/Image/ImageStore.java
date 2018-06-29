package com.miloshzelembaba.play.Image;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miloshzelembaba on 2018-04-09.
 *
 * To be worked on in the future. This is for performance improvements with downloading pictures.
 * The essence would be we would store a certain amount of images in memory (determined by a LRU or
 * some policy) and whenever we request an image, we'd hit here first and if it's a miss then
 * do the https request to fetch the image data.
 */


public class ImageStore {
    private static ImageStore instance;
    private Map<String, Bitmap> savedBitmaps;

    private ImageStore() {
        savedBitmaps = new HashMap<>();
    }

    public static ImageStore getInstance() {
        if (instance == null) {
            instance = new ImageStore();
        }

        return instance;
    }

    public void storeBitmap(String imageUrl, Bitmap bitmap) {
        savedBitmaps.put(imageUrl, bitmap);
    }

    public boolean has(String imageUrl) {
        return savedBitmaps.containsKey(imageUrl);
    }

    public Bitmap getBitmap(String imageUrl) {
        return savedBitmaps.get(imageUrl);
    }


}
