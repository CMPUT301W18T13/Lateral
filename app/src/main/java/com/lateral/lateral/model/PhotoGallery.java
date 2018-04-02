/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
     * Create a new Photo Gallery
     */
    public PhotoGallery() {
        // TODO: Testing default images
        String base64String =
                "_9j_4AAQSkZJRgABAQAAAQABAAD_2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB" +
                "AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH_2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEB" +
                "AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH_wAARCAA_AD8DASIA" +
                "AhEBAxEB_8QAGQAAAwEBAQAAAAAAAAAAAAAAAAoLCQcI_8QAMRAAAQMDAwAFDQEBAAAAAAAABAID" +
                "BQEGBwAICQoSFBo5ERMVVlh2eJaXtrfT1CEj_8QAGgEAAgMBAQAAAAAAAAAAAAAABgcACAkCA__E" +
                "AD4RAAICAgECAgQJCAsAAAAAAAMEAgUBBgcIEQASExRh8AkXGBkhMZWW1TdTVFVWd9TWFSIjNjh1" +
                "drKztbb_2gAMAwEAAhEDEQA_AJ_-jXuzjg2TzO__AHW2Pt6BuBdo2-aFL3dkG7WBWzjLesK12WX5" +
                "kmMCdUlkiZkyiY23YXz9FiDSs0Gac06CMS2pzyN6NpxnCBDhvxObZggRpDBEkfll1s8t1KEqq-Y3" +
                "EwUZHNkOJUlxaA48NilFJq2OhNaU1Ufn7rX4Q6cNlrdP31rZXdksakd3Ks1elFakr6xhhhVNixO1" +
                "YVqoJOGUawusM52sDD6c4QgOqQ7G0_i3at2RPZVA0RIhYkrg77Ul8GPCEJlgGAwnJLAokH5yShAf" +
                "ml5ISnOE4xnr6NUNu7ecZXqtmL6ty38Gju3nGV6rZi-rct_BpDfOvdL_AOgcp_dOn_mnwXfJ7338" +
                "9r_2iz-H-_bPs7zydGqG3dvOMr1WzF9W5b-DR3bzjK9VsxfVuW_g1PnXul_9A5T-6dP_ADT4nye9" +
                "9_Pa_wDaLP4f79s-zvPJ0aoI3d0afjjm4CTibcczdZM-WGRSJuMDJLU0_GGJbqkcpyIuCCPjpINk" +
                "hxhRgamx3SGfKwyeA66gltG3dRt6ujahuJy9t1vI8SWn8TXlI2u9NAMOihT8ehLJ0DcIoj7jrwQ9" +
                "wQJsZMtBPPPOhoOoK488tpTirGdPfWFw11L2N9S8dt7Avd66iG1dp9mqIVbpqorEE5WSUlnbFRhZ" +
                "dwq6zOPWoMBKyv5gejLGfgK3LjXZtGAo1dDTmq6WS4mUWcsCgxGGS4AXBBAJAkxxnOH9nmEojn2n" +
                "3jnHjZzoyviNznw2ZN-6sba1G5eZrk02Q7zpXfztabuCR27S9gWDDZIjo5JV7437VZwhYJyM1Y3Y" +
                "dYJiIt1haGo3IgKQHIkYzsIN7wEqdQN3LnoyviNznw2ZN-6sbaoBONtvNuMvNodadQtt1pxCVtuN" +
                "rTVK23EKpVK0LTWqVoVSqVJrWlaVpWtNZZ9cHLg-GutxnZHtJ1jkagtuH9d1fadN21b1iru9etHW" +
                "2XVxEzA8EbGBq9UyFgRR6Chx4JJM_wBEcP3irXZbNxZBENq_SOL7I6-hZ10_Iwq4AQ4CnKPeGShz" +
                "ExImDEgskhLMcFh9fjD3jo51dsO9z0Fjm_3g9vW4s7swDVh3XMNuWbfcqvqs0rjO9ymggzTD31N1" +
                "Fsy4ERd1UfJpHQqLsbDIl17i6W_5F-jv4H3I-nso7TnoHbnmsrtMiZaKA32MHX7JOdZaklwsWwQV" +
                "jOSJXVFVS9mxxsBXzblS7IIkDyJprKvb7yw8hnEpkALbFyEYxvrJ-NYlFBoRN3nJfyND26IugjEt" +
                "ibKxD5lvZSssVP8AwHiZaVlBhasDQMZdVoNR5EZRF23TPxH1GVru59G-yZV2cCxLDYem7e7ECW4V" +
                "WIY8zJNFu32cr7JUizmORQbdPkIsxy3dAeOClGWL7zselHFWcmJYIhOcQpbvUgmWtYzntiGLZUMM" +
                "TRYl9PmkMUMSl3wNWQYSak8i442y24664hpppCnHXXFJQ222hNVLccWqtEoQhNKqUpVaJSmla1rS" +
                "lK10uFyL9IgwPtt9O4u2nMwG43NYvaY4y7kGPv4OsKRb6yFKLm4p8crJkkMuiKKh7NkgoFPnHKF3" +
                "uPIAEQruPuTd33KLzu35M4Q2z2TJYo21JMTH3RCQcxJQtggw5Nes0RuDzBQJhy53nR1LNbsGEj0C" +
                "yTAyHYnH1wykXWVXv7x0cFW2HZH6CyNf7Ie4XcWD2Y9q_LriG27NsSVR1XvJjSyCnTQwjAH0t0Fv" +
                "O4Fyl01fGpIwq7TbMIiEe6XAXBvS0orsnVhdC3zk3K4n6Xps0O1CcqxZjiZQnJm0pkmvWJ5lMcio" +
                "JmjAwo-mSJtCuWq-PBdv2vfiER48VlUUXnkFreLdeUIzjjOYkxR15Y4mcmMYliJiRzmMs-UuECYG" +
                "bPA-D6yeRLIOUs5b1d9bl7Ng5ksC27YxcHkV-sDMJi2LieuB12z8WtDij4-sBQ9WXY5v0VbqJ50l" +
                "qaBBl2DHpp1Xfm98VDd9742b-KbB1TJ1M25vfFQ3fe-Nm_imwdWT-Ds5QPzB1e8q7uTWNb0tZrhX" +
                "-i6nVNSS9QoKGko9k0Cqqa5Jfv5e4UlResmGNcR2pGMFRQU4LCCOaKGGt8b6_VRfetCD2nB2LCxL" +
                "6ZxtppG4YYMWf19pFJLyRzKcoDxGMiEljM5exOjK-I3OfDZk37qxtqgLqf10ZXxG5z4bMm_dWNtU" +
                "BdV1-FT_AMVBP3daf_y3Pg06f_yfx_zqy_2reDXDtwW2rA-6rH5uLtwmL7XylZBq6voi7hGfQbEm" +
                "9SrVJW2bhjCI-5bSnEMqcHbnbXl4iYbGefHbOSw-82vuOjWdlVbWtDZJXNHZ2FNb1rA3K61qnWa6" +
                "yr2wy8wmknkyBaVYFL-sM4CjLCX0xljPh0MLLtgKq2ALSx4SGddgUDAMOWO0hlESMhkhLH0ShOOY" +
                "5x9ePHP8XYoxphKx4LGmIrFtfHFhW0NQWEtS0IcOEhwkV8inn-zBtN0JPNd6xUlJlqfkpQ1x46RL" +
                "KMfefX0DRo14OuuWLbVhYNsvvunK0466crTbbR5yKdlpk8yGYOYkpEKYs5kJOUpzlKWc5z2IQgDG" +
                "EIxhCKERiEKERjGOGMRhAcIYxGEIxxjEYxxiMcYxjGMYx4NTNub3xUN33vjZv4psHVMnUzbm98VD" +
                "d9742b-KbB1ql8EP-X_kH9z1p_7XSPFf-o7-59P_AKlX_wCrtfHlTZVu7yRsb3FWNuKxePGyc3aa" +
                "z4-Ytqb8_SFu-0p0RcdcVsybgykkjIPCc88BIMecdiZkWNlUDlVC7K811H9Kvwe6CK5K7ScrBSS2" +
                "UKNEj8hWhJgjkVp_0aFkCYmIfMZTX_EPuxgK3Kf6oZuv-aSZ0a2K5o6R-Bef7ut2Xk3TJ22wVddG" +
                "oXuK-7vKJwlZA5mQpOZp7BMTol2GGCLSaEUy_pzQCWAyShmtWr8jbdp6p0aKzwumwbLM1jKqtiif" +
                "MYQkUXrISSFKcIQiTA5RjPyxzKOZRxLDuXeq9v8A7KeYvnSyv1aO9V7f_ZTzF86WV-rSRujSY-bN" +
                "6Qf2BvPv3uP4x798-zsT_HpyR-t1Psmt_hvfvn2dncu9V7f_AGU8xfOllfq0d6r2_wDsp5i-dLK_" +
                "VpI3RqfNm9IP7A3n373H8Y9--fZ2nx6ckfrdT7Jrf4b3759nZ0-7elX4tbt6UVYm0i_zLrqI8iFb" +
                "u3IluxtvNnLbWkciUeh4aVknhB3qtvPBiDsPGNoWOg8BTiSm1BM45lvrcPl_I2cMmnjyV-ZRuyXv" +
                "C5iQx6hx6JCWJU9QGLDq6-oKIix_MRkQEp99QcaIKMp96rVXFcr0ae3CfS1wj09M3T_FmoZpLPYF" +
                "wJ2lq7cXN5YGRWL6cSAWLh5z1NPLHZg4U4gw0YYJt5PlVX0IltO_bVuQ1Q39l60unOZV1xLLKBia";

        byte[] byteArray = Base64.decode(base64String, Base64.URL_SAFE);
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        this.insert(image, 0);
        this.insert(image, 2);
        this.insert(image, 4);
    }

    /**
     * Insert a photo
     * @param bitmap bitmap image
     * @param index index in list to store photo
     */
    public void insert(Bitmap bitmap, int index){
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
