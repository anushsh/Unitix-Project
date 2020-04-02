package com.example.unitix;
import android.os.AsyncTask;

import java.net.*;
import java.util.Iterator;
import java.util.Scanner;

import org.json.*;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

public class AccessWebJSONTask extends AsyncTask<URL, String, JSONObject> {


    String type = "GET";

    public void setRequestType(String type) {
        this.type = type;
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


    protected JSONObject doInBackground(URL[] urls) {

        try {
            URL url = urls[0];
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            conn.connect();

            Scanner in = new Scanner(url.openStream());
            String msg = exhaust(in);

            return new JSONObject(msg);

        } catch (Exception e) {
            return null;
        }
    }
}
