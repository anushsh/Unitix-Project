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

import java.util.List;

public class EventActivity extends AppCompatActivity {

    DataSource ds;
    Event event;
    Show show;
    User user;
    String eventID;
    List<Ticket> userTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        this.ds = new DataSource();

        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        this.eventID = intent.getStringExtra("eventID");
        this.user = this.ds.getUser(getIntent().getStringExtra("EMAIL"));

        TextView eventName = findViewById(R.id.event_name);
        eventName.setText(name);

        // execute in background to keep main thread smooth and allow for parallel execution
        AsyncTask<String, Integer, List<Ticket>> ticketTask = new LoadTicketTask();
        ticketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.user.currTickets);
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
            showView.setPadding(0, 0, 0, 50);
            showView.setOrientation(LinearLayout.VERTICAL);

            // show description
            TextView showDescription = new TextView(getApplicationContext());
            showDescription.setText(new StringBuilder()
                    .append("Show ").append(i + 1).append(": ").append(show.getPrettyStartDate())
                    .append("\n").append(show.getPrettyTimeRange())
                    .append("\nPrice: " + show.getPrettyPrice()).toString()
            );
            showView.addView(showDescription);

            Button purchaseButton = null;
            if (!hasTicket(show)) {
                purchaseButton = addPurchaseButton("Purchase Ticket");
            } else {
                purchaseButton = addPurchaseButton("Purchase Another Ticket");
            }
            showView.addView(purchaseButton);


            showList.addView(showView);
        }

        description.setText(descriptionText);
        Log.e("NOAH", "start time " + event.shows.get(0).startTime);

        // TODO: display each show with purchase link (port code from other page?)
    }

    // return if user has purchased a ticket to a show. userTickets was asynchronously populated
    boolean hasTicket(Show show) {
        for (Ticket ticket : userTickets) {
            Log.e("MICHAEL", ticket.toString());
            if (ticket.showID.equals(show.id)) {
                Log.e("MICHAEL", "TICKET MATCHES EVENT");
                return true;
            }
        }
        return false;
    }

    Button addPurchaseButton(String text) {
        Button purchaseButton = new Button(getApplicationContext());
        purchaseButton.setText(text);
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
        return purchaseButton;
    }

    void showPurchaseSuccessToast() {
        Toast.makeText(getApplicationContext(), "ticket purchased!", Toast.LENGTH_LONG).show();
    }

    void showPurchaseFailureToast() {
        Toast.makeText(getApplicationContext(), "an error has occurred...", Toast.LENGTH_LONG).show();
    }

    void handleNullEvent() {
        finish();
    }

    private class LoadTicketTask extends AsyncTask<String, Integer, List<Ticket>> {
        protected List<Ticket> doInBackground(String... ids) {
            return ds.getTickets(EventActivity.this.user.currTickets);
        }

        protected void onPostExecute(List<Ticket> tickets) {
            EventActivity.this.userTickets = tickets;

            // once tickets loaded, get events
            AsyncTask<String, Integer, Event> task = new LoadEventTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, EventActivity.this.eventID);
        }
    }

    private class LoadEventTask extends AsyncTask<String, Integer, Event> {
        protected Event doInBackground(String... ids) {
            return ds.getEventByID(ids[0]);
        }

        protected void onPostExecute(Event event) {
            EventActivity.this.event = event;
            if (event != null) {
                handleValidEvent();
                Log.e("NOAH", "got valid event " + event.name);
            } else {

                Log.e("NOAH", "got null event");
                handleNullEvent();
            }
        }
    }
}
