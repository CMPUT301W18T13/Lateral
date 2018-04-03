/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

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

    public static int MAX_PHOTOS = 5;

    private Bitmap[] photoList = new Bitmap[MAX_PHOTOS];

    /**
     * Insert a photo
     * @param bitmap bitmap image
     * @param index index in list to store photo
     */
    public void insert(@Nullable Bitmap bitmap, int index){
        if (index < 0 || index > MAX_PHOTOS)
            throw new IllegalArgumentException("Index out of range");

        photoList[index] = bitmap;
    }

    /**
     * Get image by index
     * @param index index in list
     * @return Bitmap image
     */
    public Bitmap get(int index){
        if (index < 0 || index > MAX_PHOTOS)
            throw new IllegalArgumentException("Index out of range");

        return photoList[index];
    }

    /**
     * PhotoGallery Json Serializer
     */
    public static class Serializer implements JsonSerializer<PhotoGallery>, JsonDeserializer<PhotoGallery> {

        /**
         * Create a PhotoGallery serializer
         */
        public Serializer(){
        }

        /**
         * Serialize
         * @param gallery gallery
         * @param type actual type
         * @param jsonSerializationContext context
         * @return serialized gallery
         */
        @Override
        public JsonElement serialize(PhotoGallery gallery, Type type, JsonSerializationContext jsonSerializationContext) {

            // TODO: Investigate why serialize appends a space to the end
            JsonArray photoArray = new JsonArray();

            for (Bitmap bitmap : gallery.photoList) {
                if (bitmap == null) {
                    photoArray.add(JsonNull.INSTANCE);
                } else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // TODO: Maybe use JPG if I can ensure quality isn't repeatedly lost
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

            for (int i = 0; i < MAX_PHOTOS; i++) {

                JsonElement element = photoArray.get(i);

                if (element == JsonNull.INSTANCE) {
                    gallery.photoList[i] = null;
                } else {
                    String base64String = element.getAsString();
                    byte[] byteArray = Base64.decode(base64String, Base64.URL_SAFE);
                    Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    if (image == null) throw new JsonParseException("Base64 image could not be decoded");
                    else gallery.photoList[i] = image;
                }
            }

            return gallery;
        }
    }
}
