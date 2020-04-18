package com.example.unitix.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
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

    public static Group[] createGroupList(JSONArray jarray) {
        List<Group> list = new ArrayList<Group>();
        for (int i = 0; i < jarray.length(); i++){
            try {
                Group g = new Group(jarray.getJSONObject(i));
                if (g.isValid) {
                    list.add(g);
                }
            } catch (JSONException e) {
                //Catch Exception
            }
        }
        return list.toArray(new Group[0]); //Return list as an array
    }

    public String getDisplayName() {
        return this.displayName;
    }
