package com.example.unitix.models;

import android.util.Log;

import org.json.JSONObject;

public class Group extends Model {

    String[] currentEvents;
    String[] pastEvents;
    String displayName;
    String email;
    String password; // might delete
    String groupType;
    String bio;
    int followers;

    public Group(JSONObject jo) {
        try {
            this.id = jo.getString("_id");
            // TODO - get events
            this.displayName = jo.getString("displayName");
            this.email = jo.getString("email");
            this.groupType = jo.getString("groupType");
            this.bio = jo.getString("bio");
            this.followers = jo.getInt("followers");
        } catch (Exception e) {
            Log.e("MICHAEL", "Exception making Group: " + e);
            isValid = false;
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
