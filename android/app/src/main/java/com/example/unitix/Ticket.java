package com.example.unitix;

import android.util.Log;

import org.json.JSONObject;

public class Ticket {
    String id;
    String showID;
    boolean isRedeemed;
    boolean isRequested;
    String customer;

    boolean isValid;

    public Ticket(JSONObject jo) {
        try {
            this.id = jo.getString("_id");
            this.showID = jo.getString("show");
            this.isRedeemed = jo.getBoolean("redeemed");
            this.isRequested = jo.getBoolean("requested");
            this.customer = jo.optString("customer", "NAME N/A");
        } catch (Exception e) {
            Log.e("MICHAEL", "Error creating ticket - " + e);
            this.isValid = false;
        }
    }

    @Override
    public String toString() {
        return "Ticket applies to " + this.showID + " and has " + (this.isRedeemed ? "" : "not ") + "been redeemed";
    }
}
