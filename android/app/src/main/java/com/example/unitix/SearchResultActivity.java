package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitix.server.DataSource;
import com.example.unitix.models.Event;
import com.example.unitix.models.Show;

import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    DataSource ds = new DataSource();
    //User user;
    //private String email;
    //TextView mTextViewSearchResult;
    String query;
    ViewGroup feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Spinner spinner = (Spinner) findViewById(R.id.search_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_spinner_array, android.R.layout.simple_spinner_dropdown_item);

        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    public void onSearchButtonClick(View view) {
        EditText searchQuery = (EditText) findViewById(R.id.search_query_input);
        query = searchQuery.getText().toString();

        Spinner searchSpinner = (Spinner) findViewById(R.id.search_spinner);
        String selectedSearchSetting = searchSpinner.getSelectedItem().toString();

        AsyncTask<Integer,Integer, Event[]> task = new AsyncTask<Integer, Integer, Event[]>() {
            @Override
            protected Event[] doInBackground(Integer... integers) {
                return new Event[0];
            }
        };

        if (selectedSearchSetting.equals("Title, Group, Descrip.")) {
            task = new SearchResultActivity.HandleSearch();
        } else if (selectedSearchSetting.equals("Tag")) {
            task = new SearchResultActivity.HandleTagSearch();
        }

//        // execute in background to keep main thread smooth
//        AsyncTask<Integer,Integer, Event[]> task = new SearchResultActivity.HandleTagSearch();
        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private class HandleSearch extends AsyncTask<Integer, Integer, Event[]> {
        protected Event[] doInBackground(Integer... ints) {
            return ds.getEventSearchResults(query);
        }
        protected void onPostExecute(Event[] events) {
            //Log.e("NOAH","dashboard received events, got" + events.length);
            addSearchResultsToPage(events);
        }
    }

    private class HandleTagSearch extends AsyncTask<Integer, Integer, Event[]> {
        protected Event[] doInBackground(Integer... ints) {
            return ds.getEventSearchResultsByTag(query);
        }
        protected void onPostExecute(Event[] events) {
            Log.e("Kara","dashboard received events(search by tag), got " + events.length);
            addSearchResultsToPage(events);
        }
    }

    void addSearchResultsToPage(Event[] events) {
        Log.e("KARA", "in addSearchResultsToPage events.length: " + events.length);
        feed = findViewById(R.id.event_feed);
        feed.removeAllViews();
        for (Event event : events) {
            List<Show> shows = event.getShows();
            if (shows.size() == 0) {
                Log.e("KARA", "EVENTS WITH 0 SHOWS");
                continue;
            }
            Log.e("KARA", "event with more than 0 shows");
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
                    Intent i = new Intent(SearchResultActivity.this, EventActivity.class);
                    i.putExtra("eventName", eventName);
                    i.putExtra("eventID", eventID);
                    i.putExtra("EMAIL", getIntent().getStringExtra("EMAIL"));
                    startActivityForResult(i, 1);

                }
            });


            feed.addView(eventView);
        }
    }


    //used to be in onCreate
//        mTextViewSearchResult = (TextView) findViewById(R.id.textViewSearchResult);
//
//        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
//            handleSearch(getIntent().getStringExtra(SearchManager.QUERY));
//        }

//        Intent intent = getIntent();
//        this.user = this.ds.getUser(intent.getStringExtra("EMAIL"));



//    public void handleSearch(String searchQuery) {
//        mTextViewSearchResult.setText(searchQuery);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.options_menu, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        return true;
//    }
}
