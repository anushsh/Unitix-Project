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
        host = "localhost";
        port = 3000;
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

    // for testing purposes
    public Event[] getAllEvents() {
        Event[] events = new Event[0];
        try {
            AsyncTask<String, Integer, Event[]> task =
                    new GetAllEventsTask();
            task.execute("http://10.0.2.2:" + port + "/list_events_with_shows");
            events = task.get();
        } catch (Exception e) {
            Log.e("NOAH","exception: " + e);
            // pass
        }
        Log.e("NOAH","about to return events, got" + events.length);
        return events;
    }

    private class GetAllEventsTask extends AsyncTask<String, Integer, Event[]> {
        protected Event[] doInBackground(String... urlStrings) {
            try {
                URL url = new URL(urlStrings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                conn.connect();
                int responsecode = conn.getResponseCode();
                if (responsecode != 200) {
                    Log.e("NOAH", "unexpected status code:" + responsecode);
                } else {
                    Scanner in = new Scanner(url.openStream());
                    String total = "";
                    while (in.hasNext()) {
                        String line = in.nextLine();
                        total += line;
                    }
                    conn.disconnect();
                    in.close();

                    JSONObject object = (JSONObject) new JSONTokener(total).nextValue();
                    if (object.getString("status").equals("success")) {
                        return Event.createEventList(object.getJSONArray("events"));
                    } else {
                        return new Event[0];
                    }


                }

            } catch (Exception e) {
                Log.e("NOAH", "exception: " + e);
                // TODO: handle differently?
                return new Event[0];

            }
            return new Event[0];
        }

//        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
//        }

//        protected void onPostExecute(Event[] result) {
//            // TODO:
//            Log.e("NOAH","again:?" + result);
//        }
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
