package com.example.unitix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        email = intent.getStringExtra("EMAIL");
        this.user = this.ds.getUser(email);
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

            Button notificationsButton = findViewById(R.id.notificationbtn);
            notificationsButton.setText(notificationsButton.getText() +
                    " (" + user.numNotifications + ")");

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
        //TODO replace with getUserShowInfo
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

    public void onEditProfileButtonClick(View v) {

        Intent i = new Intent(this, EditProfileActivity.class);
        i.putExtra("EMAIL", email);

        startActivityForResult(i, 1);
    }

    public void onNotificationButtonClick(View v) {

        Intent i = new Intent(this, NotificationsActivity.class);
        i.putExtra("EMAIL", email);

        startActivityForResult(i, 1);
    }

    public void onBackButtonClick(View v) {

        finish();
    }

    public void onLogoutButtonClick(View v) {

        //need to figure out a way to remove all active activities from stack instead of starting new activity to  login page
        Intent i = new Intent(this, MainActivity.class);

        startActivityForResult(i, 1);

        Log.d("Yash", "User logged out");

        Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_LONG).show();
        finish();

    }
}
