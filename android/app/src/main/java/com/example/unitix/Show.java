package com.example.unitix;

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

//    public Show(Event event, String name, int capacity, int ticketsSold, String startTime,
//                String endTime, String description, String location) {
//        this.event = event;
//        this.name = name;
//        this.capacity = capacity;
//        this.ticketsSold = ticketsSold;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.description = description;
//        this.location = location;
//        this.price = price;
//    }

    public Show(Event event, JSONObject jo) {
        try {
            // TODO: fix parsing
            this.event = event;
            this.name = (String) jo.get("name");
            this.capacity = (Integer) jo.get("capacity");
            this.ticketsSold = (Integer) jo.get("ticketsSold");
            this.startTime = (String) jo.get("start_date");
            this.endTime = (String) jo.get("end_date");
            this.description = (String) jo.get("description");
            this.location = (String) jo.get("location");
            this.price = (Double) jo.get("location");

        } catch (Exception e) {
            //
        }
    }


}
