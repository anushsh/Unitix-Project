package com.example.unitix;

import android.util.Log;

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
    String description;
    String location;
    boolean isValid;

    public String toString() {
        return name + " " + startTime + " " + endTime + " ticketsRemaining: " +
                (capacity - ticketsSold);
    }

    public Show(Event event, JSONObject jo) {
        try {
            // TODO: fix parsing
            this.event = event;
            this.name = (String) jo.get("name");
            this.capacity = (Integer) jo.get("capacity");
            this.ticketsSold = jo.optInt("ticketsSold", 0);
            this.startTime = (String) jo.get("start_date");
            this.endTime = (String) jo.get("end_date");
            this.description = (String) jo.get("description");
            this.location = (String) jo.get("location");
            this.isValid = true;
            // TODO: figure out price
//            this.price = (Double) jo.get("location");

        } catch (Exception e) {
            Log.e("NOAH","exception in show" + e);
            this.isValid = false;
        }
    }


}
