package com.example.unitix;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitix.models.Event;
import com.example.unitix.server.DataSource;
import com.example.unitix.R;
import com.example.unitix.models.Group;

public class GroupListActivity extends AppCompatActivity {

    protected DataSource ds;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupslist);
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL");
        this.ds = DataSource.getInstance();
        Log.e("ANUSH", "Wassup*********************************");
        // execute in background to keep main thread smooth
        AsyncTask<String,Integer,Group[]> task = new HandleGroupsTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Log.e("ANUSH", "HERE");
    }

    private class HandleGroupsTask extends AsyncTask<String, Integer, Group[]> {
        protected Group[] doInBackground(String... ids) {
            return ds.getAllGroups();
        }
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
            groupText.setText(g.getDisplayName());
            groupView.addView(groupText);

            Button viewDetailsButton = new Button(getApplicationContext());
            viewDetailsButton.setText("View Group Page");
            viewDetailsButton.setTag(g);
            groupView.addView(viewDetailsButton);
            viewDetailsButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Group group = (Group) v.getTag();
                    String groupName = group.getDisplayName();
                    String groupID = group.getId();
                    String groupDescription = group.getBio();
                    Intent i = new Intent(GroupListActivity.this, GroupPageActivity.class);
                    i.putExtra("groupName", groupName);
                    i.putExtra("bio", groupDescription);
                    i.putExtra("groupID", groupID);
                    i.putExtra("EMAIL", getIntent().getStringExtra("EMAIL"));
                    startActivityForResult(i, 1);

                }
            });

            feed.addView(groupView);
        }
    }


}
