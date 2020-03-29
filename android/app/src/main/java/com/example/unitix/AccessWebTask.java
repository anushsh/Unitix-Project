package com.example.unitix;
import android.os.AsyncTask;

import java.net.*;
import java.util.Iterator;
import java.util.Scanner;

import org.json.*;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

public class AccessWebTask extends AsyncTask<URL, String, String> {

    protected String doInBackground(URL[] urls) {

        try {
            URL url = urls[0];
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            Scanner in = new Scanner(url.openStream());
            String msg = in.nextLine();

//            JSONObject jo = new JSONObject(msg);
//            String email = jo.getString("email");
            return msg;

        } catch (Exception e) {
            return e.toString();
        }
    }
}
