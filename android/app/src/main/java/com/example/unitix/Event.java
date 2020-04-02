package com.example.unitix;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Will be used to represent events that can be displayed
 * and interacted with
 */
public class Event {

    // instance variables TODO: (change to private?)
    public String name;
    public String id;
    public List<Show> shows;
    public boolean isValid;


    public String getDescription() {
        if (shows.size() > 0) {
            return shows.get(0).description;
        }
        return "";
    }

    public Event(JSONObject jo) {
        this.shows = new ArrayList<Show>();
        try {
            this.name = jo.getString("name");
            this.id = jo.getString("_id");
            JSONArray shows = jo.getJSONArray("shows");
            for (int i = 0; i < shows.length(); i++){
                Show show = new Show(this, shows.getJSONObject(i));
                if (show.isValid) {
                    this.shows.add(show);
                }
            }
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }
    }

    public String toString() {
        return "name: " + name + " number of shows: " + shows.size();
    }

    public static Event[] createEventList(JSONArray jsonArray) {
        List<Event> list = new ArrayList<Event>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Event e = new Event(jsonArray.getJSONObject(i));
                if (e.isValid) {
                    // only add if no errors
                    list.add(e);
                }
            } catch (Exception e) {
                // pass
            }
        }
        return list.toArray(new Event[0]); // convert list to array
    }
}
