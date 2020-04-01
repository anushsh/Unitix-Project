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
            LinearLayout eventView = new LinearLayout(getApplicationContext());
            TextView eventText = new TextView(getApplicationContext());
            eventText.setText(events[i].toString());
            eventView.addView(eventText);
            for (int j = 0; j < events[i].shows.size(); j++) {
                TextView showText = new TextView(getApplicationContext());
                showText.setText(events[i].shows.get(j).toString());
                eventView.addView(showText);
            }
            feed.addView(eventView);
        }
    }


}
