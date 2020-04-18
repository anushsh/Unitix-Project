package com.example.unitix.models;

import android.util.Log;

import org.json.JSONObject;

public class Change extends Model {

    String fieldChanged;
    String priorValue;
    String updatedValue;
    String time;

    public Change(JSONObject jo) {
        try {
            this.fieldChanged = jo.optString("field_changed");
            this.priorValue = jo.optString("prior_value");
            this.updatedValue = jo.optString("updated_value");
            this.time = jo.optString("time");
            this.isValid = true;
        } catch (Exception e){
            this.isValid = false;
            Log.e("MICHAEL", e.toString());
        }
    }

    @Override
    public String toString() {
        return "@" + time + ", changed " + fieldChanged + " from " + priorValue + " to " + updatedValue;
    }
}
