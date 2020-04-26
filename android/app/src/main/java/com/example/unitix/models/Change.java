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
            //Log.e("MICHAEL", e.toString());
        }
    }

    @Override
    public String toString() {
        if (this.fieldChanged.contains("tickets available")) {
            return "Sold out of tickets for " + this.fieldChanged.split("for")[1] + " at " + prettyTime();
        } else {

        }
        return prettyTime() + ", changed " + fieldChanged + " from " + priorValue + " to " + updatedValue;
    }

    private String prettyTime() {
        // split on the date and time
        String[] dt = time.split("T");
        String[] ymd = dt[0].split("-");
        String[] hms = dt[1].split(":");

        int hour = Integer.parseInt(hms[0]);

        String date = getMonth(Integer.parseInt(ymd[1])) + " " + ymd[2] + ", " + ymd[0];
        String time = (hour > 12 ? hour - 12 : hour) + ":" + hms[1] + ":" + hms[2].substring(0, hms[2].indexOf("."));
        return date + " at " + time;
    }


}
