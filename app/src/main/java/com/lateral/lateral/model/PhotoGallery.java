/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.lateral.lateral.Constants;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;


/**
 * Class to manage photos for tasks
 */
public class PhotoGallery {

    private Bitmap[] photoList = new Bitmap[Constants.MAX_PHOTOS];

    /**
     * Create a new Photo Gallery
     */
    public PhotoGallery() {
// TODO: Testing
        photoList[1] = Bitmap.createBitmap(20, 20, Bitmap.Config.ALPHA_8);
        photoList[2] = Bitmap.createBitmap(30, 30, Bitmap.Config.ALPHA_8);
    }

    /**
     * Insert a photo
     * @param bitmap bitmap image
     * @param index index in list to store photo
     */
    public void insert(Bitmap bitmap, int index){
        if (index < 0 || index > Constants.MAX_PHOTOS)
            throw new IllegalArgumentException("Index out of range");

        photoList[index] = bitmap;
    }

    /**
     * Get image by index
     * @param index index in list
     * @return Bitmap image
     */
    public Bitmap get(int index){
        if (index < 0 || index > Constants.MAX_PHOTOS)
            throw new IllegalArgumentException("Index out of range");

        return photoList[index];
    }

    /**
     * PhotoGallery Json Serializer
     */
    public class Serializer implements JsonSerializer<PhotoGallery>, JsonDeserializer<PhotoGallery> {

        /**
         * Serialize
         * @param gallery gallery
         * @param type actual type
         * @param jsonSerializationContext context
         * @return serialized gallery
         */
        @Override
        public JsonElement serialize(PhotoGallery gallery, Type type, JsonSerializationContext jsonSerializationContext) {

            JsonArray photoArray = new JsonArray();

            for (Bitmap bitmap : gallery.photoList) {
                if (bitmap == null) {
                    photoArray.add(JsonNull.INSTANCE);
                } else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    photoArray.add(Base64.encodeToString(byteArray, Base64.URL_SAFE));
                }
            }

            return photoArray;
        }

        /**
         * Deserialize
         * @param jsonElement json element to deserialize
         * @param type actual type
         * @param jsonDeserializationContext context
         * @return PhotoGallery
         * @throws JsonParseException if the JsonElement is invalid
         */
        @Override
        public PhotoGallery deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            PhotoGallery gallery = new PhotoGallery();

            if (!(jsonElement instanceof JsonArray)) {
                throw new JsonParseException("String array expected");
            }
            JsonArray photoArray = (JsonArray) jsonElement;

            for (int i = 0; i < Constants.MAX_PHOTOS; i++) {

                JsonElement element = photoArray.get(i);

                if (element == JsonNull.INSTANCE) {
                    gallery.photoList[i] = null;
                } else {
                    try {
                        String base64String = element.getAsString();
                        byte[] byteArray = Base64.decode(base64String, Base64.URL_SAFE);
                        gallery.photoList[i] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    } catch (Exception e) {
                        throw new JsonParseException("Base64 image could not be decoded");
                    }
                }
            }

            return gallery;
        }
    }
}
