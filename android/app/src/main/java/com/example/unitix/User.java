package com.example.unitix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {

    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public String phone;
    //following
    public String[] pastTickets;
    public String[] currTickets;
    public boolean isValid;

    public User(JSONObject jo) {
        try {
            this.email = jo.getString("email");
            this.password = jo.getString("password");
            this.firstName = jo.getString("first_name");
            this.lastName = jo.getString("last_name");
            this.phone = jo.getString("phone");
            this.pastTickets = makeStringArray(jo.optJSONArray("past_tickets"));
            this.currTickets = makeStringArray(jo.optJSONArray("curr_tickets"));
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }

    }

    public String toString() {
        return "Email: " + email ;
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
    public static User getNoah() {
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
}
