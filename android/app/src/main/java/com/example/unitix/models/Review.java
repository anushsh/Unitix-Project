package com.example.unitix.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Review extends Model {

    public String review;
    public String email;

    public Review(JSONObject jo) {
        try {
            this.review = jo.getString("review");
            this.email = jo.getString("email");
        } catch (Exception e) {
        }
    }

    public static Review[] createReviewsList(JSONArray jsonArray) {
        //Log.e("YASH", "Entered here to make a list");

        List<Review> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Review n = new Review(jsonArray.getJSONObject(i));
                list.add(n);
            } catch (Exception e) {
            }
        }
        return list.toArray(new Review[0]); // convert list to array
    }

}
