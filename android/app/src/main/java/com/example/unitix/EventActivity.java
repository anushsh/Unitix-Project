package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventActivity extends AppCompatActivity {

    DataSource ds;
    Event event;
    Show show;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        this.ds = new DataSource();

        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String eventID = intent.getStringExtra("eventID");
        this.user = this.ds.getUser(getIntent().getStringExtra("EMAIL"));

        // TODO: remove after testing
        if (this.user == null) {
            this.user = User.getNoah();
        }

        TextView eventName = findViewById(R.id.event_name);
        eventName.setText(name);

        // execute in background to keep main thread smooth
        AsyncTask<String,Integer,Event> task = new LoadEventTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, eventID);
    }

    void findShow() {
        try {
            this.show = this.event.shows.get(0);
        } catch (Exception e) {
            // event with no shows
            finish();
        }
    }

    void handleValidEvent() {

        findShow();


        TextView description = findViewById(R.id.event_description);
        String descriptionText = this.show.getDescription() + "\n\n";

        LinearLayout showList = findViewById(R.id.show_list);
        for (int i = 0; i < event.shows.size(); i++) {
            final Show show = event.shows.get(i);
            LinearLayout showView = new LinearLayout(getApplicationContext());
            showView.setPadding(0,0,0,50);
            showView.setOrientation(LinearLayout.VERTICAL);
            TextView showDescription = new TextView(getApplicationContext());
            showDescription.setText(new StringBuilder()
                    .append("Show ").append(i + 1).append(": ").append(show.getPrettyStartDate())
                    .append("\n").append(show.getPrettyTimeRange()).toString()
            );
            Button purchaseButton = new Button(getApplicationContext());
            purchaseButton.setText("Purchase Ticket");
            purchaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String showID = show.id;
                    String email = user.email;
                    if (ds.purchaseTicket(email, showID)) {
                        showPurchaseSuccessToast();
                    } else {
                        showPurchaseFailureToast();
                    }
                }
            });
            showView.addView(showDescription);
            showView.addView(purchaseButton);
            showList.addView(showView);
        }

        description.setText(descriptionText);
        Log.e("NOAH", "start time " + event.shows.get(0).startTime);


        // TODO: add description
        // TODO: display each show with purchase link (port code from other page?)
    }

    void showPurchaseSuccessToast() {
        Toast.makeText(getApplicationContext(), "ticket purchased!",Toast.LENGTH_LONG).show();
    }

    void showPurchaseFailureToast() {
        Toast.makeText(getApplicationContext(), "an error has occurred...",Toast.LENGTH_LONG).show();
    }

    void handleNullEvent() {
        finish();
    }

    private class LoadEventTask extends AsyncTask<String, Integer, Event> {
        protected Event doInBackground(String... ids) {
            return ds.getEventByID(ids[0]);
        }

        protected void onPostExecute(Event event) {
            EventActivity.this.event = event;
            if (event != null) {
                handleValidEvent();
                Log.e("NOAH","got valid event " + event.name);
            } else {

                Log.e("NOAH","got null event");
                handleNullEvent();
            }
        }
    }
}
