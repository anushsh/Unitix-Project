package com.example.unitix;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.Iterator;
import java.util.Scanner;

import org.json.*;


public class AccessWebJSONPutTask extends AsyncTask<AccessWebJSONPutTask.Req, String, String> {


    // stores info (url and json) for request
    public static class Req {
        String urlString;
        JSONObject parameters;
        public Req(String urlString, JSONObject parameters) {
            this.urlString = urlString;
            this.parameters = parameters;
        }
    }

    protected String doInBackground(Req[] reqs) {

        try {

            Req req = reqs[0];
            URL url = new URL(req.urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonParam = req.parameters;

            String jsonInputString = jsonParam.toString();

            // use try-with to ensure os is closed
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            int code = conn.getResponseCode();
            System.out.println(code);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

            return "response.toString()";
        } catch (Exception e) {
            return "false";
        }
    }


}
