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


public class AccessWebJSONPutTask extends AsyncTask<String, String, String> {


    // reads all contents from scanner
    public static String exhaust(Scanner in) {
        String total = "";
        while (in.hasNext()) {
            String line = in.nextLine();
            total += line;
        }
        return total;
    }


    protected String doInBackground(String[] message) {

        try {

            URL url = new URL("http://10.0.2.2:3000/create_user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email", message[0]);
            jsonParam.put("password", message[1]);
            jsonParam.put("first_name", message[2]);
            jsonParam.put("last_name", message[3]);
            jsonParam.put("phone", message[4]);


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
