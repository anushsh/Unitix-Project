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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {
    DataSource ds = DataSource.getInstance();
    String query;
    ViewGroup feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Spinner searchSpinner = (Spinner) findViewById(R.id.search_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_spinner_array, android.R.layout.simple_spinner_dropdown_item);

        Spinner sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_sort_spinner_array, android.R.layout.simple_spinner_dropdown_item);

        Spinner priceFilterSpinner = (Spinner) findViewById(R.id.price_filter_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> priceFilterAdapter = ArrayAdapter.createFromResource(this,
                R.array.price_filter_spinner_array, android.R.layout.simple_spinner_dropdown_item);

        Spinner dateFilterSpinner = (Spinner) findViewById(R.id.date_filter_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> dateFilterAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_filter_spinner_array, android.R.layout.simple_spinner_dropdown_item);

        Spinner timeFilterSpinner = (Spinner) findViewById(R.id.time_filter_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> timeFilterAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_filter_spinner_array, android.R.layout.simple_spinner_dropdown_item);

        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Apply the adapter to the spinner
        searchSpinner.setAdapter(adapter);
        sortSpinner.setAdapter(sortAdapter);
        priceFilterSpinner.setAdapter(priceFilterAdapter);
        dateFilterSpinner.setAdapter(dateFilterAdapter);
        timeFilterSpinner.setAdapter(timeFilterAdapter);
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

        if (selectedSearchSetting.equals("Search by Title, Group, Descrip.")) {
            task = new SearchResultActivity.HandleSearch();
        } else if (selectedSearchSetting.equals("Search by Tag")) {
            task = new SearchResultActivity.HandleTagSearch();
        }

        // allow for parallel execution
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private class HandleSearch extends AsyncTask<Integer, Integer, Event[]> {
        protected Event[] doInBackground(Integer... ints) {
            return ds.getEventSearchResults(query);
        }
        protected void onPostExecute(Event[] events) {
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

    private Map<Event, String> createSortMapGroupName(Event[] events) {
        Map<Event, String> eventToGroupNameMap = new HashMap<>();
        for (Event e : events) {
            eventToGroupNameMap.putIfAbsent(e, e.getGroup());
        }

        return eventToGroupNameMap;
    }

    private Map<Event, String> createSortMapEventName(Event[] events) {
        Map<Event, String> eventToEventNameMap = new HashMap<>();
        for (Event e : events) {
            eventToEventNameMap.putIfAbsent(e, e.getName());
        }
        return eventToEventNameMap;
    }

    //TODO: Fill in
    private Map<Event, String> createSortMapEarliestDate(Event[] events) {
        Map<Event, String> eventToEventDateMap = new HashMap<>();
        for (Event e : events) {
            Show[] shows = ds.getShowsByEventId(e.getId());
            String earliestDate = shows[0].getPrettyStartDate();
            for (Show s : shows) {
                if (s.getPrettyStartDate().compareTo(earliestDate) < 0) {
                    earliestDate = s.getPrettyStartDate();
                }
            }
            eventToEventDateMap.putIfAbsent(e, earliestDate);
        }
        return eventToEventDateMap;
    }

    private Map<Event, String> createSortMapLowestPrice(Event[] events) {
        Map<Event, String> eventToEventPriceMap = new HashMap<>();
        for (Event e :  events) {
            Show[] shows = ds.getShowsByEventId(e.getId());
            double price = shows[0].getPrice();
            for (Show s : shows) {
                if (s.getPrice() < price) {
                    price = s.getPrice();
                }
            }
            eventToEventPriceMap.putIfAbsent(e, price + "");
        }
        return eventToEventPriceMap;
    }

    private Event[] sortEventsAscending(Event[] events, final Map<Event, String> eventToValueMap) {

        Arrays.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event one, Event two) {
                String valueOne = eventToValueMap.get(one);
                String valueTwo = eventToValueMap.get(two);
                return valueOne.compareTo(valueTwo);
            }
        });
        return events;
    }

    private Event[] sortEventsDescending(Event[] events, final Map<Event, String> eventToValueMap) {

        Arrays.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event one, Event two) {
                String valueOne = eventToValueMap.get(one);
                String valueTwo = eventToValueMap.get(two);
                return -1 * valueOne.compareTo(valueTwo);
            }
        });
        return events;
    }

    private Event[] filterByPriceRange(Event[] events, double low, double high) {
        List<Event> eventList = new ArrayList<>();
        for (Event e :  events) {
            Show[] shows = ds.getShowsByEventId(e.getId());
            for (Show s : shows) {
                if (s.getPrice() >= low && s.getPrice() <= high) {
                    eventList.add(e);
                    Log.e("KARA FILTER PRICE", e.getName() + " " + s.getPrice());
                    break;
                }
            }
        }
        return eventList.toArray(new Event[0]);
    }

    private Date convertMongoToDate(String dateString, String timeString) {
        //Log.e("KARA", "dateString " + dateString);
        int year = Integer.parseInt(dateString.substring(0, 4));
        //Log.e("KARA", "year " + year);
        int month = Integer.parseInt(dateString.substring(5, 7));
        //Log.e("KARA", "month " + month);
        int day = Integer.parseInt(dateString.substring(8, 10));
        //Log.e("KARA", "day " + day);
        int hour = Integer.parseInt(timeString.substring(0, 2));
        //Log.e("KARA", "hour " + hour);
        int min = Integer.parseInt(timeString.substring(3, 5));
        return new Date(year - 1900, month - 1, day, hour, min);
    }

    private Event[] filterByDate(Event[] events, Date low, Date high) {

        List<Event> eventList = new ArrayList<>();
        for (Event e : events) {
            //Log.e("KARA", "event " + e.getName());
            Show[] shows = ds.getShowsByEventId(e.getId());
            for (Show s : shows) {
                Date showDate = convertMongoToDate(s.getPrettyStartDate(), s.getStartTime());
                //Log.e("KARA", "low date " + low);
                //Log.e("KARA", "high date" + high);
                //Log.e("KARA", "show date " + showDate);
                //Log.e("KARA", "compare to low " + showDate.compareTo(low));
                //Log.e("KARA", "compare to high" + showDate.compareTo(high));
                if (showDate.compareTo(low) >= 0 && showDate.compareTo(high) <= 0) {
                    eventList.add(e);
                    //Log.e("KARA FILTER DATE", "adding event to date filter list" + e.getName() + " " + s.getPrice());
                    break;
                }
                //Log.e("KARA DATE FILTER", e.getName() + " " + s.getPrettyStartDate());
            }

        }

        return eventList.toArray(new Event[0]);
    }

    private Event[] filterByTime(Event[] events, int lowHour, int highHour) {
        List<Event> eventList = new ArrayList<>();

        for (Event e : events) {
            Log.e("KARA", "event " + e.getName());
            Show[] shows = ds.getShowsByEventId(e.getId());
            for (Show s : shows) {
                String showTime = s.getStartTime();
                Log.e("KARA", "showtime " + showTime);
                int hour = Integer.parseInt(showTime.substring(0, 2));
                int min = Integer.parseInt(showTime.substring(3, 5));
                if (hour >= lowHour && hour < highHour) {
                    eventList.add(e);
                    break;
                } else if (hour == highHour && min == 0) {
                    eventList.add(e);
                    break;
                }
            }
        }

        return eventList.toArray(new Event[0]);
    }

    void addSearchResultsToPage(Event[] events) {
        Log.e("KARA", "in addSearchResultsToPage events.length: " + events.length);
        feed = findViewById(R.id.event_feed);
        feed.removeAllViews();

        //handle price filter setting
        Spinner priceFilter = (Spinner) findViewById(R.id.price_filter_spinner);
        String selectedPriceFilterSetting = priceFilter.getSelectedItem().toString();
        if (!selectedPriceFilterSetting.equals("Filter by Ticket Price")) {
            if (selectedPriceFilterSetting.equals("$0 to $5")) {
                events = filterByPriceRange(events, 0, 5);
            } else if (selectedPriceFilterSetting.equals("$5 to $10")) {
                events = filterByPriceRange(events, 5, 10);
            } else if (selectedPriceFilterSetting.equals("$10 to $15")) {
                events = filterByPriceRange(events, 10, 15);
            } else if (selectedPriceFilterSetting.equals("$15 to $20")) {
                events = filterByPriceRange(events, 15, 20);
            } else if (selectedPriceFilterSetting.equals("$20+")) {
                events = filterByPriceRange(events, 20, Double.POSITIVE_INFINITY);
            }
        }

        //handle date filter setting
        Spinner dateFilter = (Spinner) findViewById(R.id.date_filter_spinner);
        String selectedDateFilterSetting = dateFilter.getSelectedItem().toString();
        Log.e("KARA", "GOT RIGHT BEFORE DATE IF BLOCK");
        if (!selectedDateFilterSetting.equals("Filter by Date")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            Date currentDate = new Date();
            Log.e("KARA", "current date " + currentDate);
            // convert date to calendar
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);

            if (selectedDateFilterSetting.equals("Today")) {
                Log.e("KARA", "GOT IN TODAY DATE IF BLOCK");
                c.add(Calendar.DATE, 1);
                // convert calendar to date
                Date currentDatePlusDay = c.getTime();
                Log.e("KARA", "current day plus day " + currentDatePlusDay);
                events = filterByDate(events, new Date(), currentDatePlusDay);
            } else if (selectedDateFilterSetting.equals("This Week")) {
                Log.e("KARA", "GOT IN THIS WEEK DATE IF BLOCK");
                c.add(Calendar.WEEK_OF_MONTH, 1);
                Date currentDatePlusWeek = c.getTime();
                Log.e("KARA", "current day plus week " + currentDatePlusWeek);
                events = filterByDate(events, new Date(), currentDatePlusWeek);
            } else if (selectedDateFilterSetting.equals("This Month")) {
                Log.e("KARA", "GOT IN THIS MONTH DATE IF BLOCK");
                c.add(Calendar.MONTH, 1);
                Date currentDatePlusMonth = c.getTime();
                Log.e("KARA", "current day plus month " + currentDatePlusMonth);
                events = filterByDate(events, new Date(), currentDatePlusMonth);
            }
        }

        //handle time filter setting
        Spinner timeFilter = (Spinner) findViewById(R.id.time_filter_spinner);
        String selectedTimeFilterSetting = timeFilter.getSelectedItem().toString();
        if (!selectedTimeFilterSetting.equals("Filter by Start Time")) {
            if (selectedTimeFilterSetting.equals("4:00 PM - 6:00 PM")) {
                events = filterByTime(events, 16, 18);
            } else if (selectedTimeFilterSetting.equals("6:00 PM - 8:00 PM")) {
                events = filterByTime(events, 18, 20);
            } else if (selectedTimeFilterSetting.equals("8:00 PM - 10:00 PM")) {
                events = filterByTime(events, 20, 22);
            } else if ((selectedTimeFilterSetting.equals("10:00 PM - Midnight"))) {
                events = filterByTime(events, 22, 24);
            }
        }
        
        //handle sorting setting
        Spinner sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        String selectedSortSetting = sortSpinner.getSelectedItem().toString();
        if (selectedSortSetting.equals("Sort by Event Name A to Z")) {
            events = sortEventsAscending(events, createSortMapEventName(events));
        } else if (selectedSortSetting.equals("Sort by Event Name Z to A")) {
            events = sortEventsDescending(events, createSortMapEventName(events));
        } else if (selectedSortSetting.equals("Sort by Group Name A to Z")) {
            events = sortEventsAscending(events, createSortMapGroupName(events));
        } else if (selectedSortSetting.equals("Sort by Group Name Z to A")) {
            events = sortEventsDescending(events, createSortMapGroupName(events));
        } else if (selectedSortSetting.equals("Sort by Price Asc")) {
            events = sortEventsAscending(events, createSortMapLowestPrice(events));
        } else if (selectedSortSetting.equals("Sort by Price Desc")) {
            events = sortEventsDescending(events, createSortMapLowestPrice(events));
        } else if (selectedSortSetting.equals("Sort by Date Asc")) {
            events = sortEventsAscending(events, createSortMapEarliestDate(events));
        } else if (selectedSortSetting.equals("Sort by Date Desc")) {
            events = sortEventsDescending(events, createSortMapEarliestDate(events));
        }

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

}
