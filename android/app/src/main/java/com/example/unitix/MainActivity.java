package com.example.unitix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText email,password;
    Button login, register;
    DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.loginbtn);
        register = (Button)findViewById(R.id.registerbtn);
        ds = new DataSource();
    }


    public void onLoginButtonClick(View v) {

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        String currPassword = ds.getUserPassword(emailText);

        if (currPassword.equals(passwordText) && currPassword.length() != 0) {
            //login
        }
        //find user in mongodb
        //if not found || password does not match, empty fields
        //else start/launch new activity - show dashboard with user logged in

    }

    public void onRegisterButtonClick(View v) {

        Intent i = new Intent(this, RegisterActivity.class);

        i.putExtra("message", "hi");

        startActivityForResult(i, 1);
    }

    // this is simply a temporary button to get to skip login
    public void onEventsListButtonClick(View v) {
        Intent i = new Intent(this, EventsListActivity.class);

        startActivityForResult(i, 1);
    }
}
