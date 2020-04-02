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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ds = new DataSource();

        // execute in background to keep main thread smooth
        AsyncTask<Integer,Integer,Event[]> task = new HandleEventsTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        Log.e("NOAH", "here?");

    }

    private class HandleEventsTask extends AsyncTask<Integer, Integer, Event[]> {
        protected Event[] doInBackground(Integer... ints) {
            return ds.getAllEvents();
        }
        protected void onPostExecute(Event[] events) {
            Log.e("NOAH","dashboard received events, got" + events.length);
            addEventsToPage(events);
        }

    }

    void addEventsToPage(Event[] events) {
        LinearLayout feed = findViewById(R.id.event_feed);

        for (int i = 0; i < events.length; i++) {
            Event event = events[i];

            Log.e("NOAH","event " + i + " out of " + events.length);
            LinearLayout eventView = new LinearLayout(getApplicationContext());
            eventView.setOrientation(LinearLayout.VERTICAL);

            // event title text
            TextView eventText = new TextView(getApplicationContext());
            eventText.setText(event.toString());
            eventView.addView(eventText);

            LinearLayout allShowsView = new LinearLayout(getApplicationContext());
            allShowsView.setOrientation(LinearLayout.VERTICAL);

            for (int j = 0; j < event.shows.size(); j++) {
                Log.e("NOAH","show " + j);
                Show show = event.shows.get(j);
                LinearLayout showView = new LinearLayout(getApplicationContext());
                showView.setOrientation(LinearLayout.VERTICAL);
                TextView showText = new TextView(getApplicationContext());
                showText.setText(show.toString());
                showView.addView(showText);
                Button purchaseButton = new Button(getApplicationContext());
                purchaseButton.setText("Purchase ticket");
                showView.addView(purchaseButton);
                // add show to button
                purchaseButton.setTag(show);
//                purchaseButton.setTag(0, event.name);
//                purchaseButton.setTag(1, event.id);
//                purchaseButton.setTag(2, show.id);

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
                        startActivityForResult(i, 1);

                    }
                });
                allShowsView.addView(showView);

            }
            eventView.addView(allShowsView);
            feed.addView(eventView);
        }
    }


}
