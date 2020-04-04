package com.example.unitix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    DataSource ds = new DataSource();
    User user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        this.user = this.ds.getUser(intent.getStringExtra("EMAIL"));
        // TODO: determine who user is
        // TODO: this should probably happen asynchronously...
        TextView emailProfile = (TextView) findViewById(R.id.profile_email);

        emailProfile.setText(("Email: " + user.email));

        showUser();
        showTickets();
    }

    void showUser() {
        if (user != null) {
            TextView userName = findViewById(R.id.profile_user_name);
            userName.setText("Name: " + user.firstName + " " + user.lastName);
            TextView phoneNumber = findViewById(R.id.profile_phone_number);
            phoneNumber.setText("Phone Number: " + user.phone);
//            TextView emailText = findViewById(R.id.profile_email);
//            emailText.setText("Email: " + user.email);
        }
    }

    void showTickets() {
        if (user != null) {
            LinearLayout ticketList = findViewById(R.id.ticket_list);
            for (String ticket : user.currTickets) {
                TextView ticketView = new TextView(getApplicationContext());
                ticketView.setText(ticket);
                ticketList.addView(ticketView);
            }
            // TODO: get user shows
        }
    }
}
