package com.example.unitix;

import org.json.JSONObject;

import java.util.Date;

/**
 * Will be used to represent events that can be displayed
 * and interacted with
 */
public class Event {

    // instance variables (TODO: revise)
    String name;
    String group;
    String description;
    Date date;
    int ticketsRemaining;
    Show[] shows;


    public Event(JSONObject jo) {
        // TODO:
    }

    public Event(String name, String group, String description, Date date, int ticketsRemaining) {
        this.name = name;
        this.group = group;
        this.description = description;
        this.date = date;
        this.ticketsRemaining = ticketsRemaining;
    }

    public boolean buyTicket() {
        // TODO: returns true if successfully bought ticket ?
        // TODO: needs to pass ticket to user...
        return false;
    }
}
