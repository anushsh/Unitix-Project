package com.example.unitix.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Will be used to represent events that can be displayed
 * and interacted with
 */
public class Event {

    // instance variables, package protected
    String name;
    String id;
    List<Show> shows;
    String group;
    boolean isValid;


    public String getDescription() {
        if (shows.size() > 0) {
            return shows.get(0).description;
        }
        return "";
    }

    public Event(JSONObject jo) {
        this.shows = new ArrayList();
        try {
            this.name = jo.getString("name");
            this.id = jo.getString("_id");
            this.group = (String) jo.get("group");
            JSONArray shows = jo.getJSONArray("shows");
            for (int i = 0; i < shows.length(); i++){
                Show show = new Show(this, shows.getJSONObject(i));
                if (show.isValid) {
                    this.shows.add(show);
                }
            }
            Collections.sort(this.shows);
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("name: ")
                .append(name)
                .append(" number of shows: ")
                .append(shows.size())
                .toString();
    }

    public static Event[] createEventList(JSONArray jsonArray) {
        List<Event> list = new ArrayList<>();
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

    public boolean isValid() {
        return this.isValid;
    }

    public List<Show> getShows() {
        return this.shows;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getGroup() {
        return this.group;
    }
}
