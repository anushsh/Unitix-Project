package com.example.unitix;

import org.json.JSONObject;

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

}
