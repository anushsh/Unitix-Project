package com.example.unitix.models;

import android.util.Log;

import org.json.JSONObject;

public class Ticket extends Model {

    String showID;
    boolean isRedeemed;
    boolean isRequested;
    String customer;

    public Ticket(JSONObject jo) {
        try {
            this.id = jo.getString("_id");
            this.showID = jo.getString("show");
            this.isRedeemed = jo.getBoolean("redeemed");
            this.isRequested = jo.getBoolean("requested");
            this.customer = jo.optString("customer", "NAME N/A");
            this.isValid = true;
        } catch (Exception e) {
            Log.e("MICHAEL", "Error creating ticket - " + e);
            this.isValid = false;
        }
    }

    @Override
    public String toString() {
        return "Ticket for " + this.showID + " and has " + (this.isRedeemed ? "" : "not ") + "been redeemed";
    }

    public String getShowId() {
        return this.showID;
    }

    public boolean isRequested() {
        return this.isRequested;
    }

    public boolean isRedeemed() {
        return this.isRedeemed;
    }
}
