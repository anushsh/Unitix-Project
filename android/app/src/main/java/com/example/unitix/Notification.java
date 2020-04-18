package com.example.unitix;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notification {

    public String content;
    public boolean isValid;

    public Notification(JSONObject jo) {
        try {
            this.content = jo.getString("content");
            this.isValid = true;
        } catch (Exception e) {
            this.isValid = false;
        }
    }

    public static Notification[] createNotificationList(JSONArray jsonArray) {
        List<Notification> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Notification n = new Notification(jsonArray.getJSONObject(i));
                if (n.isValid) {
                    // only add if no errors
                    list.add(n);
                }
            } catch (Exception e) {
                // pass
            }
        }
        return list.toArray(new Notification[0]); // convert list to array
    }

}
