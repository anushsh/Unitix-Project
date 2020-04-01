package com.example.unitix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
            eventView.setOrientation(LinearLayout.VERTICAL);
            TextView eventText = new TextView(getApplicationContext());
            eventText.setText(events[i].toString());
            eventView.addView(eventText);
            for (int j = 0; j < events[i].shows.size(); j++) {
                TextView showText = new TextView(getApplicationContext());
                showText.setText(events[i].shows.get(j).toString());
                eventView.addView(showText);
                Button purchaseButton = new Button(getApplicationContext());
                purchaseButton.setText("Purchase ticket");
                eventView.addView(purchaseButton);
                purchaseButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO: handle ticket purchasing!
                    }
                });
            }


            feed.addView(eventView);
        }
    }


}
