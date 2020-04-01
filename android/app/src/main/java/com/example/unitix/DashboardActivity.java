package com.example.unitix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DashboardActivity extends AppCompatActivity {

    protected DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Log.e("NOAH", "here?");
        ds = new DataSource();
        Event[] events = ds.getAllEvents();
        addEventsToPage(events);
        Log.e("NOAH", "IN DASHBOARD, got " + events.length + " events");
    }

    void addEventsToPage(Event[] events) {
        LinearLayout feed = findViewById(R.id.event_feed);
        for (int i = 0; i < events.length; i++) {
            TextView text = new TextView(getApplicationContext());
            text.setText(events[i].toString());
            feed.addView(text);
        }
    }


}
