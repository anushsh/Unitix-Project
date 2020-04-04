package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class EventActivity extends AppCompatActivity {

    DataSource ds;
    Event event;
    User user = User.getNoah(); // TODO: change to actually receive session user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String eventID = intent.getStringExtra("eventID");
        String showID = intent.getStringExtra("showID");
        // TODO: get session user

        TextView eventName = (TextView) findViewById(R.id.event_name);
        eventName.setText(name);

        ds = new DataSource();

        // execute in background to keep main thread smooth
        AsyncTask<String,Integer,Event> task = new LoadEventTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, eventID);
    }


    void handleValidEvent() {
        TextView description = findViewById(R.id.event_description);
        String descriptionText = event.getDescription() + "\n\n";
        LinearLayout showList = findViewById(R.id.show_list);
        for (int i = 0; i < event.shows.size(); i++) {
            final Show show = event.shows.get(i);
            TextView showView = new TextView(getApplicationContext());
            showView.setText("Show " + (i+1) + ": " + show.getPrettyStartDate() + "\n" +
                    show.getPrettyTimeRange() + "\n" +
                    "Click here to purchase tickets");
            showView.setOnClickListener(new View.OnClickListener() {
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
            showList.addView(showView);
        }

        description.setText(descriptionText);
        Log.e("NOAH","start time " + event.shows.get(0).startTime);

        // TODO: add description
        // TODO: display each show with purchase link (port code from other page?)
    }

    void showPurchaseSuccessToast() {
        Toast.makeText(getApplicationContext(), "ticket purchased!",Toast.LENGTH_LONG).show();
    }

    void showPurchaseFailureToast() {
        Toast.makeText(getApplicationContext(), "an error has occured...",Toast.LENGTH_LONG).show();
    }

    void handleNullEvent() {
        // TODO: handle null event (LEAVE PAGE?)
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
