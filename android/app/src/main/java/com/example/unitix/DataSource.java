package com.example.unitix;


import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public User getUser(String email) {

        try {
            String urlString = host + ":" + port + "/find_user?email=" + email;
            URL url = new URL(urlString);
            AsyncTask<URL, String, JSONObject> task =
                    new AccessWebJSONTask();
            task.execute(url);
            JSONObject jo = task.get();
            return new User(jo.getJSONObject("user"));

        } catch (Exception e) {
            Log.e("Yash","getUser exception " + e);
            return null;
        }
    }

    public Notification[] getAllNotifications(String email) {
        Notification[] notifications = new Notification[0];
        try {
            URL url = new URL(host + ":" + port + "/get_user_notifications?email=" + email);
            AsyncTask<URL, String, JSONObject> task =
                    new AccessWebJSONTask();
            task.execute(url);
            JSONObject jo = task.get();

            notifications = Notification.createNotificationList(jo.getJSONArray("notifications"));
        } catch (Exception e) {
            Log.e("NOAH","exception: " + e);
        }
        return notifications;
    }

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

    public Event[] getEventSearchResults(String searchQuery) {
        Event[] events = new Event[0];
        try {
            URL url = new URL(host + ":" + port + "/get_search_result_events?searchQuery="+searchQuery);
            AsyncTask<URL, String, JSONObject> task =
                    new AccessWebJSONTask();
            task.execute(url);
            JSONObject jo = task.get();

            events = Event.createEventList(jo.getJSONArray("events"));
        } catch (Exception e) {
            Log.e("KARA","exception: " + e);
            // pass
        }
        Log.e("KARA","about to return search event results, got" + events.length);
        return events;
    }

    public Group getGroupByID(String groupID) {
        try {
            URL url = new URL(host + ":" + port + "/get_group_by_id?groupID=" + groupID);
            AsyncTask<URL, String, JSONObject> task = new AccessWebJSONTask();
            task.execute(url);
            JSONObject jo = task.get();
            return new Group(jo.getJSONObject("group"));
        } catch (Exception e) {
            Log.e("MICHAEL", "Error getting group by id: " + e);
            return null;
        }
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
            Log.e("NOAH","json object is " + jo.toString());
            return new Event(jo.getJSONObject("event"));

        } catch (Exception e) {
            Log.e("NOAH","getEventByID exception " + e);
            return null;
        }
    }

    public Show[] getUserShowInfo(String email) {
        try {
            AccessWebJSONTask task = new AccessWebJSONTask();
            String urlString = host + ":" + port + "/get_user_show_info?email=" + email;
            URL url = new URL(urlString);
            task.execute(url);
            JSONObject jo = task.get();
            JSONArray showInfoArray = jo.getJSONArray("shows");
            List<ShowInfo> showInfo = new LinkedList();
            for (int i = 0; i < showInfoArray.length(); i++) {
                ShowInfo show = new ShowInfo(showInfoArray.getJSONObject(i));
                if (show.isValid) {
                    showInfo.add(show);
                }
            }
            //TODO figure this out
            return showInfo.toArray(new Show[0]);

        } catch (Exception e) {
            return new Show[0];
        }
    }

    public Ticket[] getUserTickets(String email) {
        try {
            AccessWebJSONTask task = new AccessWebJSONTask();
            String urlString = host + ":" + port + "/get_user_tickets?email=" + email;
            URL url = new URL(urlString);
            task.execute(url);
            JSONObject jo = task.get();
            JSONArray ticketsArray = jo.getJSONArray("tickets");
            List<Ticket> tickets = new LinkedList();
            for (int i = 0; i < ticketsArray.length(); i++) {
                Ticket ticket = new Ticket(ticketsArray.getJSONObject(i));
                if (ticket.isValid) {
                    tickets.add(ticket);
                }
            }
            return tickets.toArray(new Ticket[0]);

        } catch (Exception e) {
            return new Ticket[0];
        }
    }

    public void readNotifications(String email) {
        try {
            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            JSONObject jo = new JSONObject();
            jo.put("email", email);
            String url = host + ":" + port + "/read_all_notifications";
            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(url, jo);
            task.execute(req);
            JSONObject res = task.get();
            Log.e("NOAH","res is " + res);
        } catch (Exception e) {
            Log.e("NOAH","failed to purchase");
        }
    }

    public boolean purchaseTicket(String email, String showID) {
        try {
            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            JSONObject jo = new JSONObject();
            jo.put("email", email);
            jo.put("showID", showID);
            String url = host + ":" + port + "/purchase_ticket";
            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(url, jo);
            task.execute(req);
            JSONObject res = task.get();
            Log.e("NOAH","res is " + res);
            if (res.getString("status").equals("success")) {
                return true;
            }
        } catch (Exception e) {
            Log.e("NOAH","failed to purchase");
        }
        return false;
    }

    public List<Ticket> getTickets(String[] ticketIDs) {
        List<Ticket> tickets = new ArrayList<>();
        for (String ticketID : ticketIDs) {
            try {
                URL url = new URL(host + ":" + port + "/get_ticket?ticketID=" + ticketID);
                AsyncTask<URL, String, JSONObject> task = new AccessWebJSONTask();
                task.execute(url);
                JSONObject jo = task.get();
                System.out.println("TICKET JSON OBJECT IS:\n" + jo.toString());
                tickets.add(new Ticket(jo.getJSONObject("ticket")));
            } catch (Exception e) {
                // skip this ticket
                Log.e("MICHAEL", "ERROR - COULD NOT RETRIEVE TICKET:" + e);
            }
        }
        return tickets;
    }

    public void redeemTicket(String ticketID) {
        try {
            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            String urlString = host + ":" + port + "/redeem_ticket";
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("ticketID", ticketID);
            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(urlString, jsonParam);
            task.execute(req);

            Log.e("NOAH","redeem ticket RESPONSE" + task.get().toString());
        } catch (Exception e) {
            Log.e("NOAH","redeem ticket exception " +e);
        }
    }



    public boolean createUser(String email, String password, String firstName, String lastName, String phone) {
        try {

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email", email);
            jsonParam.put("password", password);
            jsonParam.put("first_name", firstName);
            jsonParam.put("last_name", lastName);
            jsonParam.put("phone", phone);
            String urlString = "http://10.0.2.2:3000/create_user";

            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(urlString, jsonParam);

            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            task.execute(req);

            return true;

        } catch (Exception e) {
            Log.e("Yash","createUser exception " + e);
            return false;
        }
    }

    public boolean updateUser(String email, String password, String firstName, String lastName, String phone) {
        try {

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email", email);
            jsonParam.put("password", password);
            jsonParam.put("first_name", firstName);
            jsonParam.put("last_name", lastName);
            jsonParam.put("phone", phone);
            String urlString = "http://10.0.2.2:3000/update_user";

            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(urlString, jsonParam);

            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            task.execute(req);

            return true;

        } catch (Exception e) {
            Log.e("Yash","updateUser exception " + e);
            return false;
        }
    }

}
