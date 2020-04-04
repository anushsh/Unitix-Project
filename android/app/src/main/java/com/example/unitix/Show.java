package com.example.unitix;

import android.util.Log;
import android.widget.Button;

import org.json.JSONObject;

/**
 * java representation of show objects. Each show has an event parent
 */

public class Show {

    Event event;
    String name;
    int capacity;
    int ticketsSold;
    double price;
    String startTime;
    String endTime;
    String startDate;
    String endDate;
    String description;
    String location;
    String id;
    boolean isValid;

    public Show(Event event, JSONObject jo) {
        try {
            // TODO: fix parsing
            this.event = event;
            this.name = (String) jo.get("name");
            this.capacity = (Integer) jo.get("capacity");
            this.ticketsSold = jo.optInt("ticketsSold", 0);
            this.description = (String) jo.get("description");
            this.location = (String) jo.get("location");
            this.id = jo.getString("_id");
//            this.price = Double.parseDouble((String) jo.getJSONObject("price").get("$numberDecimal"));
            this.isValid = true;
            this.startTime = jo.optString("start_time");
            this.endTime = jo.optString("end_time");
            this.startDate = jo.optString("start_date");
            this.endDate =  jo.optString("end_date");


        } catch (Exception e) {
            Log.e("NOAH","exception in show" + e);
            Log.e("NOAH",jo.toString());
            this.isValid = false;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(name)
                .append(" ")
                .append(getPrettyStartTime())
                .append(getPrettyEndTime())
                .append(" ticketsRemaining: ")
                .append(capacity - ticketsSold)
                .toString();
    }

    public String getPrettyStartDate() {
        if (startDate == null) {
            return "";
        }
        String date = startDate.split("T")[0];
        // TODO: use time class or something to make pretty
        return date;
    }

    public String getPrettyTimeRange() {
        return getPrettyStartTime() + " - " + getPrettyEndTime();
    }
    private static String getPrettyTime(String time) {
        if (time == null || time.equals("")) {
            return "";
        }
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        String min = parts[1];
        String end = "AM";
        // fix 24 hour clock
        if (hour > 12) {
            end = "PM";
            hour = hour % 12;
        }
        // fix integer minute parsing
        if (min.length() < 2) {
            min = "0" + min;
        }
        return hour + ":" + min + " " + end;
    }

    public String getPrettyStartTime() {
        return getPrettyTime(startTime);
    }

    public String getPrettyEndTime() {
        return getPrettyTime(endTime);
    }

    // TODO: handle ticket purchasing



    public boolean isSoldOut() {
        return ticketsSold >= capacity;
    }


    public String getDescription() {
        return this.description;
    }




}
