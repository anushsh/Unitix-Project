package com.example.unitix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventActivity extends AppCompatActivity {

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
    }
}
