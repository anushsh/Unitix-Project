package com.example.unitix.models;

import com.example.unitix.server.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User extends Model {

    public String password;
    public String firstName;
    public String lastName;
    public String phone;
    public String[] followers;
    public String[] pastTickets;
    public String[] currTickets;
    public String[] notifications;
    public String[] readNotifications;
    public int numNotifications;
    public int numReadNotifications;

    public User(JSONObject jo) {
        try {
            this.id = jo.getString("email");
            this.password = jo.getString("password");
            this.firstName = jo.getString("first_name");
            this.lastName = jo.getString("last_name");
            this.phone = jo.getString("phone");
            this.pastTickets = makeStringArray(jo.optJSONArray("past_tickets"));
            this.currTickets = makeStringArray(jo.optJSONArray("curr_tickets"));
            this.followers = makeStringArray(jo.optJSONArray("following"));

            JSONArray notifications = jo.optJSONArray("notifications");
            JSONArray readNotifications = jo.optJSONArray("read_notifications");
            if (notifications != null) {
                this.numNotifications = notifications.length();
            } else {
                this.numNotifications = 0;
            }

            if (readNotifications != null) {
                this.numReadNotifications = readNotifications.length();
            } else {
                this.numReadNotifications = 0;
            }
            this.notifications = makeStringArray(jo.optJSONArray("notifications"));
            this.readNotifications = makeStringArray(jo.optJSONArray("read_notifications"));

            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }

    }

    public String toString() {
        return "Email: " + this.id ;
    }

    public static String[] makeStringArray(JSONArray arr) throws JSONException {
        if (arr == null) {
            return new String[0];
        }
        String[] out = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            out[i] = (String) arr.get(i);
        }
        return out;
    }

    // this is for testing...
    public static User getNoahEmpty() {
        try {
            JSONObject jo = new JSONObject();
            jo.put("email", "noahsylv@gmail.com");
            jo.put("password", "1234");
            jo.put("first_name", "Noah");
            jo.put("last_name", "Sylvester");
            jo.put("phone", "18185198475");
            jo.put("past_tickets", new JSONArray());
            jo.put("curr_tickets", new JSONArray());
            return new User(jo);
        } catch (Exception e) {
            return null;
        }
    }

    // used for testing
    public static User getNoah() {
        return new DataSource().getUser("noahsylv@gmail.com");
    }
}
