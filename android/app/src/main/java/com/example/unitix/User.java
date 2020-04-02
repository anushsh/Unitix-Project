package com.example.unitix;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {

    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public String phone;
    //following
//    public Event[] pastTickets;
//    public Event[] currTickets;
    public boolean isValid;

    public User(JSONObject jo) {
        try {
            this.email = jo.getString("email");
            this.password = jo.getString("password");
            this.firstName = jo.getString("first_name");
            this.lastName = jo.getString("last_name");
            this.phone = jo.getString("phone");
//            this.pastTickets = jo.getString("past_tickets");
//            this.currTickets = jo.getString("curr_tickets");
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }

    }

    public String toString() {
        return "Email: " + email ;
    }
}
