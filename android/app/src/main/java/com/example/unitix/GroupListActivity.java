package com.example.unitix;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupListActivity extends AppCompatActivity {

    protected DataSource ds;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupslist);
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL");
        this.ds = new DataSource();
        Log.e("ANUSH", "HERE");
    }

//    // execute in background to keep main thread smooth
//    AsyncTask<Integer,Integer,Event[]> task = new DashboardActivity.HandleEventsTask();
//    // allow for parallel execution
//    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

}
