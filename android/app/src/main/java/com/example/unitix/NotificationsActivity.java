package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitix.server.DataSource;
import com.example.unitix.R;
import com.example.unitix.models.Notification;
import com.example.unitix.models.User;

public class NotificationsActivity extends AppCompatActivity  {

    DataSource ds;
    User user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Intent intent = getIntent();
        ds = new DataSource();
        email = intent.getStringExtra("EMAIL");
        this.user = this.ds.getUser(email);

        // execute in background to keep main thread smooth
        AsyncTask<Integer,Integer, Notification[]> task = new HandleNotificationsTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class HandleNotificationsTask extends AsyncTask<Integer, Integer, Notification[]> {
        protected Notification[] doInBackground(Integer... ints) {
            return ds.getAllNotifications(user.getId());
        }
        protected void onPostExecute(Notification[] notifications) {
            Log.e("NOAH","notification view received notifications, got" + notifications.length);
            addNotificationsToPage(notifications);
        }
    }

    void addNotificationsToPage(Notification[] notifications) {
        LinearLayout feed = findViewById(R.id.notification_list);
        for (Notification notification : notifications) {
            TextView notificationText = new TextView(getApplicationContext());
            notificationText.setText(notification.content);
            notificationText.setTextSize(30);
            feed.addView(notificationText);
        }
        if (notifications.length == 0) {
            hideReadNotificationsButton();
        }
    }

    void hideReadNotificationsButton() {
        Button button = findViewById(R.id.readnotificationsbtn);
        button.setVisibility(View.INVISIBLE);
        LinearLayout feed = findViewById(R.id.notification_list);
        TextView notificationText = new TextView(getApplicationContext());
        notificationText.setText("No notifications");
        notificationText.setTextSize(30);
        feed.addView(notificationText);
    }

    public void onReadNotificationButtonClick(View v) {
        ds.readNotifications(email);
        LinearLayout feed = findViewById(R.id.notification_list);
        feed.removeAllViews();
        hideReadNotificationsButton();
    }



}
