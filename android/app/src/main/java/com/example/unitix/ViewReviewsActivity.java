package com.example.unitix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unitix.models.Event;
import com.example.unitix.models.Review;
import com.example.unitix.server.DataSource;
import com.example.unitix.models.User;

public class ViewReviewsActivity extends AppCompatActivity {


    Button done_btn;
    DataSource ds;
    User user;
    private String email, eventID;
    Event event;
    UserManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);


        done_btn = (Button) findViewById(R.id.done_btn);
        ds = DataSource.getInstance();

        manager = UserManager.getManager(getApplicationContext());
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL");
        eventID = intent.getStringExtra("EVENTID");
        this.user = this.ds.getUser(email);
        this.event = this.ds.getEventByID(eventID);

        Review[] reviews = ds.getAllReviews(eventID);

        addReviewsToPage(reviews);
    }

    void addReviewsToPage(Review[] reviews) {

        if (reviews.length > 0) {
            LinearLayout feed = findViewById(R.id.reviews_list);
            for (Review review : reviews) {
                TextView reviewText = new TextView(getApplicationContext());
                reviewText.setText(review.review + " - by : " + review.email);
                reviewText.setTextSize(15);
                feed.addView(reviewText);
                Log.e("YASH", "displaying review " + review.review);
            }
        } else {
            LinearLayout feed = findViewById(R.id.reviews_list);
            TextView reviewText = new TextView(getApplicationContext());
            reviewText.setText("No Reviews have been published for this event till now");
            reviewText.setTextSize(18);
            feed.addView(reviewText);
        }
    }

    public void onDoneButtonClick(View v) {
        finish();
    }

}