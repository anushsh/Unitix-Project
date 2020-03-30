package com.example.unitix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EventsListActivity extends AppCompatActivity {

    DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventslist);
        ds = new DataSource();
        getEvents();
    }

    void getEvents() {
        ds.getAllEvents();
    }
}
