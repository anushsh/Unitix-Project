package com.example.unitix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;


public class DashboardActivity extends AppCompatActivity {

    protected DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Log.e("NOAH", "here?");
        ds = new DataSource();
        ds.getAllEvents();
    }


}
