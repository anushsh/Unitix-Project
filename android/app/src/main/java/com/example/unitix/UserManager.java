package com.example.unitix;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.unitix.models.User;
import com.example.unitix.server.DataSource;

import java.util.Map;
import java.util.Set;

// singleton class that keeps track of session user
public class UserManager {

    private User user;
    private SharedPreferences prefs;
    DataSource ds;


    // static instance
    private static UserManager manager;

    // private constructor
    private UserManager() {
        ds = new DataSource();
    }

    public static UserManager getManager(Context context) {
        if (manager == null) {
            manager = new UserManager();
        }
        manager.prefs = context.getSharedPreferences("Req",0);
        Log.e("NOAH","is prefs null "+ (null == manager.prefs));
        return manager;
    }

    private void resolveUser() {
        if (prefs != null) {
            String email = prefs.getString("email",null);
            Log.e("NOAH","email is " + email);
            user = ds.getUser(email);
        } else {
            user = null;
        }
    }

    public User getUser() {
        resolveUser();
        return user;
    }

    public boolean loggedIn() {
        if (user == null) {
            resolveUser();
        }
        return user != null;
    }

    public boolean logIn(String email) {
        if (prefs == null) {
            return false;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email",email);
        editor.apply();
        resolveUser();
        return loggedIn();
    }

    public void logOut() {
        user = null;
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("email");
        editor.apply();
    }

    public void handleSession(Activity activity) {
        if (user == null) {
            activity.finish();
        }
    }


}
