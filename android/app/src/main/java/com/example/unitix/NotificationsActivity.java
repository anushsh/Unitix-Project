package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

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
        AsyncTask<Integer,Integer,Notification[]> task = new HandleNotificationsTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class HandleNotificationsTask extends AsyncTask<Integer, Integer, Notification[]> {
        protected Notification[] doInBackground(Integer... ints) {
            return ds.getAllNotifications(user.email);
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
            feed.addView(notificationText);
        }
    }

}
