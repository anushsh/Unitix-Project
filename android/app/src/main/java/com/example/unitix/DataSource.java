package com.example.unitix;


import java.net.*;
import java.util.Scanner;
import android.os.AsyncTask;
import org.json.*;
import android.util.Log;

public class DataSource {

    private String host;
    private int port;

    public DataSource() {
        // use Node Express defaults
        host = "http://10.0.2.2";
        port = 3000;
    }


    public JSONObject getUser(String email) {
        try {
            //need to figure out URL
            String urlString = host + ":" + port + "/api?email=" + email;
            URL url = new URL(urlString);
            AccessWebTask task = new AccessWebTask();
            task.execute(url);
            String msg = task.get();
            JSONObject jo = new JSONObject(msg);
            return jo;

        } catch (Exception e) {
            return null;
        }
    }


    public String getUserPassword(String email) {
        JSONObject jo = getUser(email);

        if (jo == null) {
            return "";
        }
        try {
            String passwordUser = jo.getString("password");
            return passwordUser;
        } catch (Exception e) {
            return "";
        }
    }

    // for testing purposes
    public Event[] getAllEvents() {
        Event[] events = new Event[0];
        try {
            URL url = new URL(host + ":" + port + "/list_events_with_shows");
            AsyncTask<URL, String, JSONObject> task =
                    new AccessWebJSONTask();
            task.execute(url);
            JSONObject jo = task.get();

            events = Event.createEventList(jo.getJSONArray("events"));
        } catch (Exception e) {
            Log.e("NOAH","exception: " + e);
            // pass
        }
        Log.e("NOAH","about to return events, got" + events.length);
        return events;
    }


    // reads all contents from scanner
    public static String exhaust(Scanner in) {
        String total = "";
        while (in.hasNext()) {
            String line = in.nextLine();
            total += line;
        }
        return total;
    }

    public Event getEventByID(String eventID) {
        try {
            // TODO: make url based on variables !
            String urlString = host + ":" + port + "/find_event_with_shows?eventID=" + eventID;

            URL url = new URL(urlString);
            AsyncTask<URL, String, JSONObject> task =
                    new AccessWebJSONTask();
            task.execute(url);
            JSONObject jo = task.get();
            return new Event(jo.getJSONObject("event"));

        } catch (Exception e) {
            Log.e("NOAH","getEventByID exception " + e);
            return null;
        }
    }



    public boolean createUser(String email, String password, String firstName, String lastName, String phone) {
        try {
            //haven't changed this - need to for create user
            //need to figure out URL

            String urlString = host + ":" + port + "/api?email=" + email;
            urlString += "&password=" + password;
            urlString += "&first_name=" + firstName;
            urlString += "&last_name=" + lastName;
            urlString += "&phone=" + phone;

            URL url = new URL(urlString);
            AccessWebTask task = new AccessWebTask();

            task.execute(url);
            String msg = task.get();
            JSONObject jo = new JSONObject(msg);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}
