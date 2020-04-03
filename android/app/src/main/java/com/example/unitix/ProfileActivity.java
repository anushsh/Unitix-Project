package com.example.unitix;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    DataSource ds = new DataSource();
    User user = ds.getUser(User.getNoah().email); // TODO: use session user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // TODO: determine who user is
        // TODO: this should probably happen asynchronously...
        showUser();
        showTickets();
    }

    void showUser() {
        if (user != null) {
            TextView userName = findViewById(R.id.profile_user_name);
            userName.setText(user.firstName + " " + user.lastName);
            TextView emailText = findViewById(R.id.profile_email);
            emailText.setText(user.email);
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
