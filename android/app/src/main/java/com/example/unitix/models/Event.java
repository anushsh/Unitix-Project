package com.example.unitix.models;

import android.util.Log;

import com.example.unitix.server.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Will be used to represent events that can be displayed
 * and interacted with
 */
public class Event extends Model {

    // instance variables, package protected
    String name;
    List<Show> shows;
    String group;

    List<Change> changes;


    public String getDescription() {
        if (shows.size() > 0) {
            return shows.get(0).description;
        }
        return "";
    }

    public Event(JSONObject jo) {
        this.shows = new ArrayList<>();
        this.changes = new ArrayList<>();
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

            // this is really ugly, sorry in advance
            JSONArray changes = jo.getJSONArray("changes");
            DataSource ds = new DataSource();
            for (int i = 0; i < changes.length(); i++) {
                Change change = ds.getChangeByID("" + changes.get(i));
                if (change.isValid()) this.changes.add(change);
            }

            isValid = true;
        } catch (Exception e) {
            Log.e("MICHAEL, event constructor error", e.toString());
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

    public List<Show> getShows() {
        return this.shows;
    }

    public String getName() {
        return this.name;
    }

    public String getGroup() {
        return this.group;
    }

    public List<Change> getChanges() {
        return this.changes;
    }
}
