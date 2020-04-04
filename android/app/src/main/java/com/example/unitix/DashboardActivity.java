package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DashboardActivity extends AppCompatActivity {

    protected DataSource ds;

    // track session user
    User user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL");
        this.ds = new DataSource();


        // execute in background to keep main thread smooth
        AsyncTask<Integer,Integer,Event[]> task = new HandleEventsTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);




//        Log.e("NOAH", "here?");

    }

    private class HandleEventsTask extends AsyncTask<Integer, Integer, Event[]> {
        protected Event[] doInBackground(Integer... ints) {
            return ds.getAllEvents();
        }
        protected void onPostExecute(Event[] events) {
//            Log.e("NOAH","dashboard received events, got" + events.length);
            addEventsToPage(events);
        }
    }

    void addEventsToPage(Event[] events) {
        LinearLayout feed = findViewById(R.id.event_feed);

        for (Event event : events) {
            LinearLayout eventView = new LinearLayout(getApplicationContext());
            eventView.setOrientation(LinearLayout.VERTICAL);

            // event title text
            TextView eventText = new TextView(getApplicationContext());
            eventText.setText(event.toString());
            eventView.addView(eventText);

            LinearLayout allShowsView = new LinearLayout(getApplicationContext());
            allShowsView.setOrientation(LinearLayout.VERTICAL);

            for (Show show : event.shows) {
                LinearLayout showView = new LinearLayout(getApplicationContext());
                showView.setOrientation(LinearLayout.VERTICAL);
                TextView showText = new TextView(getApplicationContext());
                showText.setText(show.toString());
                showView.addView(showText);
                Button purchaseButton = new Button(getApplicationContext());
                purchaseButton.setText("View Details"); // MICHAEL: Renamed since it opens the EventActivity screen
                showView.addView(purchaseButton);
                // add show to button so can have it when clicked
                purchaseButton.setTag(show);

                purchaseButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO: handle ticket purchasing!
                        Show show = (Show) v.getTag();
                        String eventName = show.event.name;
                        String eventID = show.event.id;
                        String showID = show.id;
                        Intent i = new Intent(DashboardActivity.this, EventActivity.class);
                        i.putExtra("eventName", eventName);
                        i.putExtra("showID", showID);
                        i.putExtra("eventID", eventID);
                        i.putExtra("EMAIL", getIntent().getStringExtra("EMAIL"));
                        startActivityForResult(i, 1);

                    }
                });
                allShowsView.addView(showView);
            }
            eventView.addView(allShowsView);
            feed.addView(eventView);
        }
    }

    public void onProfileButtonClick(View v) {

        Intent i = new Intent(this, ProfileActivity.class);

        i.putExtra("EMAIL", email);

        startActivityForResult(i, 1);
    }
}
