package com.example.unitix;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

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

        // execute in background to keep main thread smooth
        AsyncTask<Integer,Integer,Group[]> task = new HandleGroupsTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Log.e("ANUSH", "HERE");
    }

    private class HandleGroupsTask extends AsyncTask<Integer, Integer, Group[]> {
        @Override
        protected Group[] doInBackground(Integer... integers) { return ds.getAllGroups(); }
        protected void onPostExecute(Group[] groups) {
            Log.e("ANUSH", "group listings received groups got " + groups.length);
            
            addGroupsToPage(groups);
        }
    }

    void addGroupsToPage(Group[] groups) {
        LinearLayout feed = findViewById(R.id.group_feed);

        for (Group g: groups) {
            LinearLayout groupView = new LinearLayout(getApplicationContext());
            groupView.setOrientation(LinearLayout.VERTICAL);

            //Group display name
            TextView groupText = new TextView(getApplicationContext());
            groupText.setText(g.displayName);
            groupView.addView(groupText);

            LinearLayout allGroupView = new LinearLayout(getApplicationContext());
            allGroupView.setOrientation(LinearLayout.VERTICAL);
            groupView.addView(allGroupView);

            feed.addView(groupView);
        }
    }


}
