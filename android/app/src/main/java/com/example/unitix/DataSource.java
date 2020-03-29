package com.example.unitix;


import java.net.*;
import java.util.Iterator;
import java.util.Scanner;

import org.json.*;

public class DataSource {

//    private String host;
//    private int port;

    public DataSource() {
        // use Node Express defaults
//        host = "localhost";
//        port = 3000;
    }


    public JSONObject getUser(String email) {
        try {
            //need to figure out URL
            String urlString = "http://10.0.2.2:3000/api?email=" + email;
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

    public boolean createUser(String email, String password, String firstName, String lastName, String phone) {
        try {
            //haven't changed this - need to for create user
            //need to figure out URL

            String urlString = "http://10.0.2.2:3000/api?email=" + email;
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
