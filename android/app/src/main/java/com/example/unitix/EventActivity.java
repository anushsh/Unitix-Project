package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventActivity extends AppCompatActivity {

    DataSource ds;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String eventID = intent.getStringExtra("eventID");
        String showID = intent.getStringExtra("showID");
        TextView eventName = (TextView) findViewById(R.id.event_name);
        eventName.setText(name);

        ds = new DataSource();

        // execute in background to keep main thread smooth
        AsyncTask<String,Integer,Event> task = new LoadEventTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, eventID);
    }


    void handleValidEvent() {
        // TODO: add description
        // TODO: display each show with purchase link (port code from other page?)
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
