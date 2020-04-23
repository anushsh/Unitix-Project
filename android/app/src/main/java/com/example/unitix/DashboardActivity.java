package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.unitix.models.Group;
import com.example.unitix.server.DataSource;
import com.example.unitix.models.Event;
import com.example.unitix.models.Show;
import com.example.unitix.models.User;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class DashboardActivity extends AppCompatActivity {

    protected DataSource ds;

    // track session user
    User user;
    private String email;
    UserManager manager;

//    Set<Group> following;
    Set<String> followingGroupIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//        Intent intent = getIntent();
        manager = UserManager.getManager(getApplicationContext());
//        email = intent.getStringExtra("EMAIL");

        this.ds = DataSource.getInstance();

//        this.user = ds.getUser(email);
        this.user = manager.getUser();
        manager.handleSession(this);
        if (this.user == null) {
            finish();
        }
        this.email = user.getId();

        // execute in background to keep main thread smooth
        AsyncTask<Integer,Integer, Event[]> task = new HandleEventsTask();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class HandleEventsTask extends AsyncTask<Integer, Integer, Event[]> {
        protected Event[] doInBackground(Integer... ints) {
//            DashboardActivity.this.following = new HashSet<>(Arrays.asList(ds.getFollowedGroups(email)));
            Set<Group> tempGroups = new HashSet<>(Arrays.asList(ds.getFollowedGroups(email)));
            DashboardActivity.this.followingGroupIds = new HashSet<>();
            for (Group group : tempGroups) {
                DashboardActivity.this.followingGroupIds.add(group.getID());
            }
            return ds.getAllEvents();
        }
        protected void onPostExecute(Event[] events) {
            Log.e("NOAH","dashboard received events, got" + events.length);
            addEventsToPage(events);
        }
    }

    private Event[] sortEvents(Event[] events) {
//        final Map<Event, Group> eventGroupMap = new HashMap<>();
//        for (Event event : events) {
//            eventGroupMap.putIfAbsent(event, ds.getGroupByID(event.getGroup()));
//        }
        Arrays.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event one, Event two) {
//                boolean followingOne = following.contains(eventGroupMap.get(one));
//                boolean followingTwo = following.contains(eventGroupMap.get(two));

                boolean followingOne = followingGroupIds.contains(one.getGroup());
                boolean followingTwo = followingGroupIds.contains(two.getGroup());

                if (followingOne && !followingTwo) {
                    return -1;
                } else if (!followingOne && followingTwo) {
                    return 1;
                } else {
                    return one.getName().compareTo(two.getName());
                }
            }
        });
        return events;
    }

    void addEventsToPage(Event[] events) {
        LinearLayout feed = findViewById(R.id.event_feed);

        events = sortEvents(events);


        for (Event event : events) {
            Log.e("MICHAEL", event.toString());
            List<Show> shows = event.getShows();
            if (shows.size() == 0) {
                continue;
            }
            LinearLayout eventView = new LinearLayout(getApplicationContext());
            eventView.setOrientation(LinearLayout.VERTICAL);

            // event title text
            TextView eventText = new TextView(getApplicationContext());
            eventText.setText(event.toString());
            eventView.addView(eventText);

            LinearLayout allShowsView = new LinearLayout(getApplicationContext());
            allShowsView.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < shows.size(); i++) {
                Show show = shows.get(i);
                LinearLayout showView = new LinearLayout(getApplicationContext());
                showView.setOrientation(LinearLayout.VERTICAL);
                TextView showText = new TextView(getApplicationContext());
                showText.setText("Show " + (i + 1) + ": " + show);
                showView.addView(showText);
                allShowsView.addView(showView);
            }
            eventView.addView(allShowsView);

            Button viewDetailsButton = new Button(getApplicationContext());
            viewDetailsButton.setText("View Details");
            viewDetailsButton.setTag(event);
            eventView.addView(viewDetailsButton);
            viewDetailsButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Event event = (Event) v.getTag();
                    String eventName = event.getName();
                    String eventID = event.getId();
                    Intent i = new Intent(DashboardActivity.this, EventActivity.class);
                    i.putExtra("eventName", eventName);
                    i.putExtra("eventID", eventID);
                    i.putExtra("EMAIL", getIntent().getStringExtra("EMAIL"));
                    startActivityForResult(i, 1);

                }
            });


            feed.addView(eventView);
        }
    }

    public void onProfileButtonClick(View v) {

        Intent i = new Intent(this, ProfileActivity.class);

        i.putExtra("EMAIL", email);

        startActivityForResult(i, 1);
    }

    public void onSearchButtonClick(View v) {
        Intent i = new Intent(this, SearchResultActivity.class);

        i.putExtra("EMAIL", email);

        startActivityForResult(i, 1);
    }

    public void onGroupButtonClick(View v) {
        Intent i = new Intent(this, GroupListActivity.class);

        i.putExtra("EMAIL", email);

        startActivityForResult(i, 1);
    }
}
