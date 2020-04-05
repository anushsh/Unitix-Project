package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        TextView emailProfile = (TextView) findViewById(R.id.profile_email);

        emailProfile.setText(("Email: " + user.email));

        showUser();
//        showTickets();
        LoadTicketsTask task = new LoadTicketsTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void showUser() {
        if (user != null) {
            TextView userName = findViewById(R.id.profile_user_name);
            userName.setText("Name: " + user.firstName + " " + user.lastName);
            TextView phoneNumber = findViewById(R.id.profile_phone_number);
            phoneNumber.setText("Phone Number: " + user.phone);
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

    private class LoadTicketsTask extends AsyncTask<String, Integer, Ticket[]> {
        protected Ticket[] doInBackground(String... blank) {
            return ds.getUserTickets(user.email);
        }

        protected void onPostExecute(Ticket[] tickets) {
            Log.e("NOAH","got " + tickets.length + " tickets");
            LinearLayout ticketList = findViewById(R.id.ticket_list);
            for (final Ticket ticket : tickets) {
                TextView ticketView = new TextView(getApplicationContext());
                ticketView.setText(ticket.toString());
                ticketList.addView(ticketView);
                Button redeemButton = new Button(getApplicationContext());
                redeemButton.setText("Accept Redeem Request");
                redeemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ds.redeemTicket(ticket.id);
                    }
                });
                if (ticket.isRequested && !ticket.isRedeemed) {
                    ticketList.addView(redeemButton);
                }
            }
        }
        }
}
