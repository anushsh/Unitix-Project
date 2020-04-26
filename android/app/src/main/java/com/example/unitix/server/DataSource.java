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
import com.example.unitix.models.Review;
import com.example.unitix.models.Show;
import com.example.unitix.models.ShowInfo;
import com.example.unitix.models.Ticket;
import com.example.unitix.models.User;
import com.example.unitix.server.AccessWebJSONPutTask;
import com.example.unitix.server.AccessWebJSONTask;


public class DataSource {

    private String host;
    private int port;
    private String routePrefix;
    private static DataSource instance;

    // Singleton Pattern
    private DataSource() {
        // use Node Express defaults
        host = "http://10.0.2.2";
        port = 3000;
        routePrefix = host + ":" + port + "/";
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
            urlBuilder.append(routePrefix).append(route);

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
            //Log.e("MICHAEL", "Exception using generalized get route");
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

    private boolean postRoute(String route, String[] params, String[] vals) {
        try {
            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            JSONObject jo = new JSONObject();
            for (int i = 0; i < params.length; i++) {
                jo.put(params[i], vals[i]);
            }
            String url = routePrefix + route;
            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(url, jo);
            task.execute(req);
            JSONObject res = task.get();
            //Log.e("MICHAEL", "res for " + route + " is " + res);
            if (res.getString("status").equals("success")) return true;
        } catch (Exception e) {
            //Log.e("MICHAEL", "Error in generic post route for " + route);
        }
        return false;
    }

    private boolean postRoute(String route, String param, String val) {
        String[] params = {param};
        String[] vals = {val};
        return postRoute(route, params, vals);
    }

    private JSONArray getJSONArray(JSONObject jo, String field) {
        try {
            return jo.getJSONArray(field);
        } catch (Exception e) {
            //Log.e("MICHAEL", "Exception getting json array from json object in generalized method");
            return new JSONArray(); // empty if issue
        }
    }

    private JSONObject getJSONObject(JSONObject jo, String field) {
        try {
            return jo.getJSONObject(field);
        } catch (Exception e) {
            //Log.e("MICHAEL", "Exception getting json object from a json object in generalized method");
            return new JSONObject(); // empty if issue
        }
    }

    private JSONObject getJSONObject(JSONArray ja, int index) {
        try {
            return ja.getJSONObject(index);
        } catch (Exception e) {
            //Log.e("MICHAEL", "Exception getting json object from a json array in generalized method");
            return new JSONObject();
        }
    }

    public User getUser(String email) {
        JSONObject jo = getRoute("find_user", "email", email);
        return new User(getJSONObject(jo, "user"));
    }

    public Group[] getFollowedGroups(String email) {
        JSONObject jo = getRoute("get_followed_groups", "email", email);
        return Group.createGroupList(getJSONArray(jo, "following"));
    }

    public Notification[] getAllNotifications(String email) {
        JSONObject jo = getRoute("get_user_notifications", "email", email);
        return Notification.createNotificationList(getJSONArray(jo, "notifications"));
    }

    public Notification[] getAllReadNotifications(String email) {
        JSONObject jo = getRoute("get_user_read_notifications", "email", email);
        return Notification.createNotificationList(getJSONArray(jo, "read_notifications"));
    }

    public Review[] getAllReviews(String eventID) {
        JSONObject jo = getRoute("get_event_reviews", "eventID", eventID);
        Review[] toReturn = Review.createReviewsList(getJSONArray(jo, "reviews"));
        return toReturn;
    }

    public Event[] getAllEvents() {
        JSONObject jo = getRoute("list_events_with_shows");
        return Event.createEventList(getJSONArray(jo, "events"));
    }

    public Event[] getFavoritedEvents(String email) {
        JSONObject jo = getRoute("get_favorites", "email", email);
        return Event.createEventList(getJSONArray(jo, "favorites"));
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

    public Show[] getShowsByEventId(String eventID) {
        JSONObject jo = getRoute("get_shows_for_event", "eventID", eventID);
        return Show.createShowList(getJSONArray(jo, "shows"));
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

    public boolean readNotifications(String email) {
        return postRoute("read_all_notifications", "email", email);
    }

    public boolean purchaseTicket(String email, String showID) {
        String[] params = {"email", "showID"};
        String[] vals = {email, showID};
        return postRoute("purchase_ticket", params, vals);
    }


    public boolean favoriteEvent(String email, String eventID) {
        try {
            AccessWebJSONPutTask task = new AccessWebJSONPutTask();
            JSONObject jo = new JSONObject();
            jo.put("email", email);
            jo.put("eventID", eventID);
            String url = host + ":" + port + "/favorite_event";
            AccessWebJSONPutTask.Req req = new AccessWebJSONPutTask.Req(url, jo);
            task.execute(req);
            JSONObject res = task.get();
            if (res.getString("status").equals("success")) {
                return true;
            }
        } catch (Exception e) {
            Log.e("ANUSH", "failed to favorite");
        }
        return false;
    }

//    public List<Ticket> getTickets(String[] ticketIDs) {
//        List<Ticket> tickets = new ArrayList<>();
//        for (String ticketID : ticketIDs) {
//            try {
//                URL url = new URL(host + ":" + port + "/get_ticket?ticketID=" + ticketID);
//                AsyncTask<URL, String, JSONObject> task = new AccessWebJSONTask();
//                task.execute(url);
//                JSONObject jo = task.get();
//                System.out.println("TICKET JSON OBJECT IS:\n" + jo.toString());
//                tickets.add(new Ticket(jo.getJSONObject("ticket")));
//            } catch (Exception e) {
//                // skip this ticket
//                Log.e("MICHAEL", "ERROR - COULD NOT RETRIEVE TICKET:" + e);
//            }
//        }
//        return tickets;
//    }

    public boolean redeemTicket(String ticketID) {
        return postRoute("redeem_ticket", "ticketID", ticketID);
    }

    public boolean createUser(String email, String password, String firstName, String lastName, String phone) {
        String[] params = {"email", "password", "first_name", "last_name", "phone"};
        String[] vals = {email, password, firstName, lastName, phone};
        return postRoute("create_user", params, vals);
    }

    public boolean reviewEvent(String email, String eventID, String rating, String review) {
        String[] params = {"email", "eventID", "rating", "review"};
        String[] vals = {email, eventID, rating, review};
        return postRoute("review_event", params, vals);
    }

    public boolean createCharge(String token) {
        return postRoute("api/stripe", "token", token);
    }

    public boolean updateUser(String email, String password, String firstName, String lastName, String phone) {
        String[] params = {"email", "password", "first_name", "last_name", "phone"};
        String[] vals = {email, password, firstName, lastName, phone};
        return postRoute("update_user", params, vals);
    }

    public boolean followGroup(String email, String groupID) {
        String[] params = {"email", "groupID"};
        String[] vals = {email, groupID};
        return postRoute("follow_group", params, vals);
    }
}
