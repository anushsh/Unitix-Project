package com.example.unitix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unitix.server.DataSource;
import com.example.unitix.models.User;

public class ReviewEventActivity extends AppCompatActivity {

    EditText rating, review;
    Button review_btn;
    DataSource ds;
    User user;
    private String email, eventID;
    UserManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_event);

        rating = (EditText) findViewById(R.id.rating);
        review = (EditText) findViewById(R.id.review);
        review_btn = (Button) findViewById(R.id.review_btn);
        ds = DataSource.getInstance();

        manager = UserManager.getManager(getApplicationContext());
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL");
        eventID = intent.getStringExtra("EVENTID");
        this.user = this.ds.getUser(email);

    }

    public void onReviewButtonClick(View v) {

        String ratingText = rating.getText().toString();
        String reviewText = review.getText().toString();


        if (ratingText.length() > 0 && reviewText.length() > 0) {
            boolean reviewed = ds.reviewEvent(email, eventID, ratingText, reviewText);

            if (reviewed) {
                Toast.makeText(getApplicationContext(), "Event Reviewed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please enter a valid rating and a review", Toast.LENGTH_LONG).show();
        }


    }

}