package com.example.unitix.server;


import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import org.json.*;
import android.util.Log;

import com.example.unitix.models.Change;
import com.example.unitix.models.Event;
import com.example.unitix.models.Group;
import com.example.unitix.models.Notification;
import com.example.unitix.models.Show;
import com.example.unitix.models.ShowInfo;
import com.example.unitix.models.Ticket;
import com.example.unitix.models.User;
import com.example.unitix.server.AccessWebJSONPutTask;
import com.example.unitix.server.AccessWebJSONTask;


public class DataSource {

    private String host;
    private int port;
    private static DataSource instance;

    // Singleton Pattern
    private DataSource() {
        // use Node Express defaults
        host = "http://10.0.2.2";
        port = 3000;
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    private JSONObject getRoute(String route, String[] params, String[] vals) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(host).append(":").append(port).append("/").append(route);

            if (params != null && vals != null) {
                for (int i = 0; i < params.length; i++) {
                    urlBuilder.append("?").append(params[i]).append("=").append(vals[i]);
                }
            }

            URL url = new URL(urlBuilder.toString());
            AsyncTask<URL, String, JSONObject> task = new AccessWebJSONTask();
            task.execute(url);
            return task.get();
        } catch (Exception e) {
            Log.e("MICHAEL", "Exception using generalized get route");
            return null;
        }
    }

    private JSONObject getRoute(String route, String param, String val) {
        String[] params = {param};
        String[] vals = {val};
        return getRoute(route, params, vals);
    }

    private JSONObject getRoute(String route) {
        return getRoute(route, new String[0], new String[0]);
    }

    private JSONArray getJSONArray(JSONObject jo, String field) {
        try {
            return jo.getJSONArray(field);
        } catch (Exception e) {
            Log.e("MICHAEL", "Exception getting json array from json object in generalized method");
            return new JSONArray(); // empty if issue
        }
    }

    private JSONObject getJSONObject(JSONObject jo, String field) {
        try {
            return jo.getJSONObject(field);
        } catch (Exception e) {
            Log.e("MICHAEL", "Exception getting json object from a json object in generalized method");
            return new JSONObject(); // empty if issue
        }
    }

    private JSONObject getJSONObject(JSONArray ja, int index) {
        try {
            return ja.getJSONObject(index);
        } catch (Exception e) {
            Log.e("MICHAEL", "Exception getting json object from a json array in generalized method");
            return new JSONObject();
        }
    }

    public User getUser(String email) {
        JSONObject jo = getRoute("find_user", "email", email);
        return new User(getJSONObject(jo, "user"));
    }

    public Group[] getFollowedGroups(String email) {
        JSONObject jo = getRoute("get_followed_groups", "email", email);
        Group[] groups = Group.createGroupList(getJSONArray(jo, "following"));
        return groups;
    }

    public Notification[] getAllNotifications(String email) {
        JSONObject jo = getRoute("get_user_notifications", "email", email);
        return Notification.createNotificationList(getJSONArray(jo, "notifications"));
    }


    public Notification[] getAllReadNotifications(String email) {
        JSONObject jo = getRoute("get_user_read_notifications", "email", email);
        return Notification.createNotificationList(getJSONArray(jo, "read_notifications"));
    }

    public Event[] getAllEvents() {
        JSONObject jo = getRoute("list_events_with_shows");
        return Event.createEventList(getJSONArray(jo, "events"));
    }

    public Group[] getAllGroups() {
        JSONObject jo = getRoute("get_all_groups");
        return Group.createGroupList(getJSONArray(jo, "groups"));
    }

    public Event[] getEventSearchResults(String searchQuery) {
        JSONObject jo = getRoute("get_search_result_events", "searchQuery", searchQuery);
        return Event.createEventList(getJSONArray(jo, "events"));
    }

    public Event[] getEventSearchResultsByTag(String searchQuery) {
        JSONObject jo = getRoute("get_search_result_events_by_tag", "searchQuery", searchQuery);
        return Event.createEventList(getJSONArray(jo, "events"));
    }

    public Group getGroupByID(String groupID) {
        JSONObject jo = getRoute("get_group_by_id", "groupID", groupID);
        return new Group(getJSONObject(jo, "group"));
    }

    public Change getChangeByID(String changeID) {
        JSONObject jo = getRoute("get_change", "change", changeID);
        return new Change(jo);
    }

    public Show getShowByID(String showID) {
        JSONObject jo = getRoute("get_show", "showID", showID);
        return new Show(getJSONObject(jo, "show"));
    }

    public Event getEventByID(String eventID) {
        JSONObject jo = getRoute("find_event_with_shows", "eventID", eventID);
        return new Event(getJSONObject(jo, "event"));
    }

    public Show[] getUserShowInfo(String email) {
        JSONObject jo = getRoute("get_user_show_info", "email", email);
        JSONArray showInfoArray = getJSONArray(jo, "shows");
        List<ShowInfo> showInfo = new LinkedList();
        for (int i = 0; i < showInfoArray.length(); i++) {
            ShowInfo show = new ShowInfo(getJSONObject(showInfoArray, i));
            if (show.isValid()) {
                showInfo.add(show);
            }
        }
        //TODO figure this out
        return showInfo.toArray(new Show[0]);
    }

    public Ticket[] getUserTickets(String email) {
        JSONObject jo = getRoute("get_user_tickets", "email", email);
        JSONArray ticketsArray = getJSONArray(jo, "tickets");
        List<Ticket> tickets = new LinkedList<>();
        for (int i = 0; i < ticketsArray.length(); i++) {
            Ticket ticket = new Ticket(getJSONObject(ticketsArray, i));
            if (ticket.isValid()) {
                tickets.add(ticket);
            }
        }
        return tickets.toArray(new Ticket[0]);
    }

    public List<Ticket> getTickets(String[] ticketIDs) {
        List<Ticket> tickets = new ArrayList<>();
        for (String ticketID: ticketIDs) {
            JSONObject jo = getRoute("get_ticket", "ticketID", ticketID);
            tickets.add(new Ticket(getJSONObject(jo, "ticket")));
        }
        return tickets;
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

    public boolean createCharge(String token) {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", token);
            String urlString = "http://10.0.2.2:3000/api/stripe";

            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(urlString, jsonParam);
            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            task.execute(req);
            return true;

        } catch (JSONException e) {
            Log.e("ANUSH", "Create Charge Exception: " + e);
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

    public boolean followGroup(String email, String groupID) {
        try {
            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            JSONObject jo = new JSONObject();
            jo.put("email", email);
            jo.put("groupID", groupID);
            String url = host + ":" + port + "/follow_group";
            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(url, jo);
            task.execute(req);
            JSONObject res = task.get();
            Log.e("KARA","res is " + res);
            if (res.getString("status").equals("success")) {
                return true;
            }
        } catch (Exception e) {
            Log.e("KARA","failed to follow");
        }
        return false;
    }

}
